package wand555.github.io.challenges.types.item;

import com.destroystokyo.paper.event.player.PlayerRecipeBookClickEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.types.EventContainer;
import wand555.github.io.challenges.types.Type;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ItemType extends Type<ItemData> {

    private final NamespacedKey markedKey;

    public ItemType(Context context, TriggerCheck<ItemData> triggerCheck, Trigger<ItemData> whenTriggered) {
        this(context, triggerCheck, whenTriggered, event -> {}, event -> {});
    }

    public ItemType(Context context, TriggerCheck<ItemData> triggerCheck, Trigger<ItemData> whenTriggered, EventContainer<EntityPickupItemEvent> entityPickUpItem, EventContainer<InventoryClickEvent> inventoryClick) {
        super(context, triggerCheck, whenTriggered, Map.of(
                EntityPickupItemEvent.class, entityPickUpItem,
                InventoryClickEvent.class, inventoryClick
        ));
        this.markedKey = new NamespacedKey(context.plugin(), "marked");
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @Override
    protected <E extends Event & Cancellable> void triggerIfCheckPasses(ItemData data, E event) {
        if(triggerCheck.applies(data) && !isMarked(data.itemStackInteractedWith())) {
            callEventInContainer(event);
            markItemStack(data.itemStackInteractedWith());
            whenTriggered.actOnTriggered(data);
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

    @EventHandler
    public void onItemCollectedThroughPickup(EntityPickupItemEvent event) {
        if(!context.challengeManager().isRunning()) {
            return;
        }
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }
        if(event.isCancelled()) {
            return;
        }

        triggerIfCheckPasses(new ItemData(event.getItem().getItemStack(), event.getItem().getItemStack().getAmount(), player), event);
    }

    @EventHandler
    public void onItemCollectedThroughInventory(InventoryClickEvent event) {
        if(!context.challengeManager().isRunning()) {
            return;
        }
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if(event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) {
            return;
        }
        if(Set.of(ClickType.CONTROL_DROP, ClickType.DROP, ClickType.WINDOW_BORDER_LEFT, ClickType.WINDOW_BORDER_RIGHT, ClickType.CREATIVE).contains(event.getClick())) {
            return;
        }
        ItemStack currentItem = event.getCurrentItem();
        if(event.getClickedInventory() == null) {
            return;
        }
        // If a player clicked in the result slot of a crafting inventory (3x3 or 2x2)
        if(event.getClickedInventory() instanceof CraftingInventory craftingInventory && event.getSlot() == 0) {
            // handle shift clicks
            if(event.isShiftClick()) {
                Bukkit.broadcastMessage("shift click: " + currentItem.getType() + currentItem.getAmount());
                int totalResultAmount = getTotalResultAmount(craftingInventory, currentItem);
                int leastIngredientAmount = getLeastIngredientAmount(craftingInventory);
                ItemStack[] craftingMatrix = craftingInventory.getMatrix();
                ItemStack[] fakeMatrix = createFakeMatrix(craftingMatrix, leastIngredientAmount);
                craftingInventory.setMatrix(fakeMatrix);

                ItemStack fakeResult = new ItemStack(currentItem.getType(), totalResultAmount);
                triggerIfCheckPasses(new ItemData(fakeResult, totalResultAmount,player), event);

                craftingInventory.setResult(fakeResult);
            }
            else {
                triggerIfCheckPasses(new ItemData(currentItem, currentItem.getAmount(), player), event);
            }

        }
        else {
            // If a player right clicks (selects half of the current stack amount), then mark only half
            // Right click causing the player to pick up half does not work when used in a crafting result.
            // There it behaves exactly the same as a regular left click.
            if(event.getClick() == ClickType.RIGHT) {
                currentItem = new ItemStack(currentItem.getType(), (int) Math.ceil((double)currentItem.getAmount()/2));
            }
            triggerIfCheckPasses(new ItemData(currentItem, currentItem.getAmount(), player), event);

        }
    }

    @EventHandler
    public void onRecipeBookClick(PlayerRecipeBookClickEvent event) {
        event.setCancelled(true);
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "itemgoal.recipebook.disabled",
                true
        );
        event.getPlayer().sendMessage(toSend);
    }

    private ItemStack[] createFakeMatrix(ItemStack[] craftingMatrix, int leastIngredientAmount) {
        /*
        Reduce the items in the crafting matrix as if the shift click already happened.
        This is done by subtracting the leastIngredientAmount from every itemstack in the matrix. +1 has to
        be added because the shift click has to be possible exactly once.
        In a nutshell we always reduce a shift click to only be able to complete the recipe exactly once.
        A shift click which completes a recipe exactly once (due to the sparsity of the itemstacks in the matrix)
        behaves exactly the same as a single left/right click + directly moving the itemstack into a slot in the
        players inventory.
         */
        ItemStack[] fakeMatrix = new ItemStack[craftingMatrix.length];
        for(int i=0; i<fakeMatrix.length; i++) {
            if(craftingMatrix[i] == null || craftingMatrix[i].isEmpty()) {
                continue;
            }
            ItemStack itemInMatrix = craftingMatrix[i];
            int amount = itemInMatrix.getAmount() - leastIngredientAmount + 1;
            itemInMatrix.setAmount(amount);
            fakeMatrix[i] = itemInMatrix;
        }
        return fakeMatrix;
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
}
