package wand555.github.io.challenges.goals;

import com.fasterxml.jackson.databind.JsonNode;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemGoal extends Goal implements Storable<ItemGoalConfig>, BossBarDisplay, InvProgress, Listener {

    private final Map<Material, Collect> toCollect;
    private final boolean allItems;
    private final boolean allBlocks;
    private final BossBar bossBar;
    private final CollectedInventory collectedInventory;

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
    public void onItemCollectedThroughInventory(InventoryClickEvent event) {
        if(!context.challengeManager().isRunning()) {
            return;
        }
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if(event.getCurrentItem() == null) {
            return;
        }
        if(event.getClick() == ClickType.CONTROL_DROP || event.getClick() == ClickType.DROP) {
            return;
        }
        if(event.getAction() == InventoryAction.PICKUP_HALF) {
            return;
        }
        Inventory inv = event.getWhoClicked().getInventory();
        Arrays.stream(inv.getContents())
                .filter(itemStack -> itemStack != null && !itemStack.getType().isItem() && !itemStack.getType().isAir())
                .forEach(itemStack -> newItemCollected(player, itemStack));
    }

    private void newItemCollected(Player collector, ItemStack collected) {
        Collect updatedCollect = getToCollect().computeIfPresent(collected.getType(), (material, collect) -> {
            // don't overshoot the total amount, because the player might have picked up more items (in the stack) that they needed
            int newCurrentAmount = Math.min(collect.getCurrentAmount()+collected.getAmount(), collect.getAmountNeeded());
            collect.setCurrentAmount(newCurrentAmount);
            return collect;
        });
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
