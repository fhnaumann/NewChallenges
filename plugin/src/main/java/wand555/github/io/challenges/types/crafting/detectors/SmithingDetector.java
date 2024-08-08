package wand555.github.io.challenges.types.crafting.detectors;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.SmithingInventory;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.types.crafting.CraftingData;
import wand555.github.io.challenges.types.crafting.CraftingType;

public class SmithingDetector implements Listener {

    private final Context context;
    private final CraftingType craftingType;

    public SmithingDetector(Context context, CraftingType craftingType) {
        this.context = context;
        this.craftingType = craftingType;
    }

    @EventHandler
    public void onPlayerFinishSmithingEvent(InventoryClickEvent event) {
        // covers SMITHING
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if(!context.challengeManager().canTakeEffect(context, player)) {
            return;
        }
        if(event.getCurrentItem() == null || event.getCurrentItem().isEmpty()) {
            return;
        }
        if(event.getSlot() != 3) {
            // smithing inventory has its result slot at index 3
            return;
        }
        if(!(event.getClickedInventory() instanceof SmithingInventory smithingInventory)) {
            return;
        }
        if(!(smithingInventory.getRecipe() instanceof Keyed keyedSmithingRecipe)) {
            return;
        }
        if(event.getCurrentItem().getType() != Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE) {
            // Only works with netherite upgrade template (for now)
            return;
        }
        CraftingData craftingData = craftingType.constructCraftingData(keyedSmithingRecipe, player.getUniqueId(), false);
        craftingType.triggerIfCheckPasses(craftingData, event);
    }
}
