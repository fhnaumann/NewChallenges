package wand555.github.io.challenges.goals;

import com.fasterxml.jackson.databind.JsonNode;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import wand555.github.io.challenges.BossBarDisplay;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.ItemGoalConfig;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.utils.ActionHelper;
import wand555.github.io.challenges.utils.RandomUtil;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wand555.github.io.challenges.goals.NewItemCollect.*;

public class ItemGoal extends Goal implements Storable<ItemGoalConfig>, BossBarDisplay, InvProgress, Listener {

    private final Map<Material, Collect> toCollect;
    private final boolean allItems;
    private final boolean allBlocks;
    private final BossBar bossBar;
    private final CollectedInventory collectedInventory;

    private final NamespacedKey markedKey;

    public ItemGoal(Context context, ItemGoalConfig config) {
        super(context, config.getComplete());
        JsonNode amountNeeded = context.schemaRoot().at("/definitions/CollectableDataConfig/properties/amountNeeded/default");
        if(amountNeeded.isMissingNode()) {
            throw new RuntimeException();
        }
        JsonNode currentAmount = context.schemaRoot().at("/definitions/CollectableDataConfig/properties/currentAmount/default");
        if(currentAmount.isMissingNode()) {
            throw new RuntimeException();
        }
        this.allItems = config.getAllItems();
        this.allBlocks = config.getAllBlocks();
        if(this.allItems) {
            this.toCollect = Stream.of(Material.values()).filter(ModelMapper.VALID_ITEMS).collect(Collectors.toMap(
                    Function.identity(),
                    material -> new Collect(amountNeeded.asInt(), currentAmount.asInt())
            ));
        }
        else if(this.allBlocks) {
            this.toCollect = Stream.of(Material.values()).filter(ModelMapper.VALID_BLOCKS).collect(Collectors.toMap(
                    Function.identity(),
                    material -> new Collect(amountNeeded.asInt(), currentAmount.asInt())
            ));
        }
        else {
            this.toCollect = ModelMapper.str2Collectable(config.getItems().getAdditionalProperties(), Material.class);
        }

        this.bossBar = createBossBar();
        this.collectedInventory = new CollectedInventory(context.plugin());

        this.markedKey = new NamespacedKey(context.plugin(), "marked");

        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    private boolean determineComplete() {
        return toCollect.values().stream().allMatch(Collect::isComplete);
    }

    @Override
    public BossBar createBossBar() {
        Map.Entry<Material, Collect> randomToCollect = RandomUtil.pickRandom(toCollect);

        Component formattedBossBarComponent = formatBossBarComponent(randomToCollect.getKey(), randomToCollect.getValue());
        return BossBar.bossBar(formattedBossBarComponent, 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    private Component formatBossBarComponent(Material material, Collect collect) {
        Component unicodeComponent = ResourcePackHelper.getMaterialUnicodeMapping(material);
        return ComponentUtil.formatBossBarMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "itemgoal.bossbar.message",
                Map.of(
                        "amount", Component.text(collect.getCurrentAmount()),
                        "total_amount", Component.text(collect.getAmountNeeded())
                ),
                Map.of(
                        "item", unicodeComponent
                )
        );
    }

    private void refreshBossBar(Material material, Collect collect) {
        Component name;
        if(!collect.isComplete()) {
            name = formatBossBarComponent(material, collect);
        }
        else {
            Map.Entry<Material, Collect> randomToCollect = RandomUtil.pickRandom(toCollect);
            name = formatBossBarComponent(randomToCollect.getKey(), randomToCollect.getValue());
        }
        bossBar.name(name);
    }

    @Override
    public BossBar getBossBar() {
        return bossBar;
    }

    @Override
    public BossBarPriority getBossBarPriority() {
        return BossBarPriority.INFO;
    }

    @Override
    public void addToGeneratedConfig(GoalsConfig config) {
        config.setItemGoal(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return Component.text("ItemGoal TODO");
    }

    @Override
    public ItemGoalConfig toGeneratedJSONClass() {
        CollectableEntryConfig collectableEntryConfig = new CollectableEntryConfig();
        toCollect.forEach((material, collect) -> collectableEntryConfig.setAdditionalProperty(material.key().asMinimalString(), collect.toGeneratedJSONClass()));
        return new ItemGoalConfig(allBlocks, allItems, isComplete(), collectableEntryConfig);
    }

    @EventHandler
    public void onItemCollectedThroughPickup(EntityPickupItemEvent event) {
        if(!context.challengeManager().isRunning()) {
            return;
        }
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }
        newItemCollected(player, event.getItem().getItemStack());
    }

    @EventHandler
    public void test(CraftItemEvent event) {
        if(event.getCurrentItem() == null || event.getCurrentItem().isEmpty()) {
            return;
        }
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if(!event.isShiftClick()) {
            return;
        }




        /*
        for(ItemStack craftingComponentItemStack : craftingInventory.getMatrix()) {
            if(craftingComponentItemStack == null) {
                continue;
            }
            if(craftingComponentItemStack.getAmount() <= leastIngredient) {
                craftingComponentItemStack.setType(Material.AIR);
            }
            else {
                craftingComponentItemStack.setAmount(leastIngredient);
            }

        }*/

        //newItemCollected(player, event.getCurrentItem());
    }
    @EventHandler
    public void onItemCollectedThroughInventory(InventoryClickEvent event) {
        Bukkit.broadcastMessage("current item: " + (event.getCurrentItem() != null ? event.getCurrentItem().getType().toString() : "null"));
        Bukkit.broadcastMessage("cursor item: " + event.getCursor().getType());
        Bukkit.broadcastMessage("inv:" + (event.getClickedInventory() != null ? event.getClickedInventory().getType() : "null"));

        if(!context.challengeManager().isRunning()) {
            return;
        }
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // player has an item on their cursor and placed it (somewhere) in the inventory
        if(event.getCursor().getType() != Material.AIR) {

        }

        player.getOpenInventory().getSlotType(event.getSlot());
        Map<InventoryType, NewItemCollect> newItemSlot = new HashMap<>();
        newItemSlot.put(InventoryType.ANVIL, NEVER); // cannot store persistent items
        newItemSlot.put(InventoryType.BARREL, ALL);
        newItemSlot.put(InventoryType.BEACON, NEVER); // cannot store persistent items
        newItemSlot.put(InventoryType.BLAST_FURNACE, slots(2)); // furnace has result always in slot 2
        newItemSlot.put(InventoryType.BREWING, slots(0,1,2));
        newItemSlot.put(InventoryType.CARTOGRAPHY, NEVER);

        if(event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) {
            return;
        }
        ItemStack currentItem = event.getCurrentItem();
        if(event.getClickedInventory() == null) {
            return;
        }
        /*
        InventoryType inventoryType = event.getClickedInventory().getType();
        boolean clickedInSlotThatMayContainNewItems = newItemSlot.get(inventoryType).matches(event.getSlot());
        if(!clickedInSlotThatMayContainNewItems) {
            return;
        }
        ItemStack newItem = event.getClickedInventory().getItem(event.getSlot());
        if(newItem == null) {
            return;
        }*/
        if(event.getClickedInventory() instanceof CraftingInventory craftingInventory && event.getSlot() == 0) {
            // handle shift clicksd
            if(event.isShiftClick()) {
                Bukkit.broadcastMessage("shift click: " + currentItem.getType() + currentItem.getAmount());
                int totalResultAmount = getTotalResultAmount(craftingInventory, currentItem);
                int leastIngredientAmount = getLeastIngredientAmount(craftingInventory);
                ItemStack[] craftingMatrix = craftingInventory.getMatrix();
                ItemStack[] fakeMatrix = new ItemStack[craftingMatrix.length];

                for(int i=0; i<fakeMatrix.length; i++) {
                    if(craftingMatrix[i] == null || craftingMatrix[i].isEmpty()) {
                        continue;
                    }
                    ItemStack itemInMatrix = craftingMatrix[i];
                    //itemInMatrix.setAmount(Math.abs(leastIngredientAmount - itemInMatrix.getAmount()) + 1);
                    int amount = itemInMatrix.getAmount() - leastIngredientAmount + 1;
                    itemInMatrix.setAmount(amount);
                    fakeMatrix[i] = itemInMatrix;

                }
                craftingInventory.setMatrix(fakeMatrix);

                ItemStack fakeResult = new ItemStack(currentItem.getType(), totalResultAmount);
                newItemCollected(player, fakeResult);
                craftingInventory.setResult(fakeResult);

                //craftingInventory.setMatrix();
                //craftingInventory.clear();

            }
            else {
                newItemCollected(player, event.getCurrentItem());
            }

        }
        else {
            newItemCollected(player, event.getCurrentItem());
        }


        /*
        if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getClickedInventory().getType() != InventoryType.PLAYER) {
            if(event.getClickedInventory() instanceof CraftingInventory craftingInventory) {
                if(craftingInventory.getResult() != null && craftingInventory.getResult().getType() != Material.AIR) {
                    // result is (by looking at the internal code) always slot 0 in this inventory
                    if(event.getRawSlot() == 0) {
                        Bukkit.broadcastMessage("new item crafting slot: " + event.getCurrentItem().getType());
                        markItemStack(event.getCurrentItem());
                    }

                }
            }
            else if(event.getClickedInventory() instanceof FurnaceInventory furnaceInventory) {
                if(furnaceInventory.getResult() != null && furnaceInventory.getResult().getType() != Material.AIR) {
                    // result is (by looking at the internal code) always slot 2 in this inventory
                    if(event.getRawSlot() == 2) {
                        Bukkit.broadcastMessage("new item furnace slot: " + event.getCurrentItem().getType());
                        markItemStack(event.getCurrentItem());
                    }

                }
            }
            else {
                Bukkit.broadcastMessage("new item: " + event.getCurrentItem().getType());
                markItemStack(event.getCurrentItem());
            }

        }*/
        /*
        Inventory inv = event.getWhoClicked().getInventory();
        Arrays.stream(inv.getContents())
                .filter(itemStack -> itemStack != null && !itemStack.getType().isAir())
                .forEach(itemStack -> newItemCollected(player, itemStack));
                */
    }


    private int getLeastIngredientAmount(CraftingInventory craftingInventory) {
        return Arrays.stream(craftingInventory.getMatrix())
                .filter(itemStack -> itemStack != null && !itemStack.isEmpty())
                .mapToInt(ItemStack::getAmount)
                .min()
                .orElseThrow();
    }

    private int getTotalResultAmount(CraftingInventory craftingInventory, ItemStack currentItem) {
        final int resultAmount = currentItem.getAmount();
        int leastIngredient = -1;
        for(ItemStack itemStack : craftingInventory.getMatrix()) {
            if(itemStack != null && !itemStack.getType().isAir()) {
                int re = itemStack.getAmount() * resultAmount;
                if(leastIngredient == -1 || re < leastIngredient) {
                    leastIngredient = itemStack.getAmount() * resultAmount;
                }
            }
        }
        return leastIngredient;
    }

    private void newItemCollected(Player collector, ItemStack collected) {
        if(!isMarked(collected)) {
            Bukkit.broadcastMessage("marking new itemstack: " + collected.getType() + collected.getAmount());
            markItemStack(collected);
        }
        Collect updatedCollect = getToCollect().computeIfPresent(collected.getType(), (material, collect) -> {
            // don't overshoot the total amount, because the player might have picked up more items (in the stack) that they needed
            int newCurrentAmount = Math.min(collect.getCurrentAmount()+collected.getAmount(), collect.getAmountNeeded());
            collect.setCurrentAmount(newCurrentAmount);
            return collect;
        });
        if(updatedCollect == null) {
            return;
        }
        Component toSend;
        String soundInBundleKey;
        if(updatedCollect.isComplete()) {
            toSend = ComponentUtil.formatChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().goalResourceBundle(),
                    "itemgoal.single.reached.message",
                    Map.of(
                            "item", ComponentUtil.translate(collected.getType())
                    )
            );
            soundInBundleKey = "itemgoal.single.reached.sound";
        }
        else {
            toSend = ComponentUtil.formatChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().goalResourceBundle(),
                    "itemgoal.single.step.message",
                    Map.of(
                            "player", Component.text(collector.getName()),
                            "item", ComponentUtil.translate(collected.getType()),
                            "amount", Component.text(updatedCollect.getCurrentAmount()),
                            "total_amount", Component.text(updatedCollect.getAmountNeeded())
                    )
            );
            soundInBundleKey = "itemgoal.single.step.sound";
        }
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                context.resourceBundleContext().goalResourceBundle(),
                soundInBundleKey
        );
        refreshBossBar(collected.getType(), updatedCollect);

        if(determineComplete()) {
            onComplete();
        }
    }

    private boolean isMarked(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        return meta.getPersistentDataContainer().has(markedKey, PersistentDataType.STRING) && meta.getPersistentDataContainer().get(markedKey, PersistentDataType.STRING).equals("marked");
    }

    private void markItemStack(ItemStack itemStack) {
        Key key = Key.key("challenges");
        ItemMeta meta = itemStack.getItemMeta();
        meta.getPersistentDataContainer().set(markedKey, PersistentDataType.STRING, "marked");
        itemStack.setItemMeta(meta);
    }


    @Override
    public void onComplete() {
        setComplete(true);

        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "itemgoal.all.reached.message"
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                context.resourceBundleContext().goalResourceBundle(),
                "itemgoal.all.reached.sound"
        );

        notifyManager();
    }

    public Map<Material, Collect> getToCollect() {
        return toCollect;
    }

    @Override
    public @NotNull CollectedInventory getCollectedInventory() {
        return collectedInventory;
    }
}
