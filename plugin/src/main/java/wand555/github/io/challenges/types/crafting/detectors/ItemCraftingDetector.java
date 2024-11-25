package wand555.github.io.challenges.types.crafting.detectors;

import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.Recipe;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.types.crafting.CraftingData;
import wand555.github.io.challenges.types.crafting.CraftingType;

public class ItemCraftingDetector implements Listener {

    private final Context context;
    private final CraftingType craftingType;

    public ItemCraftingDetector(Context context, CraftingType craftingType) {
        this.context = context;
        this.craftingType = craftingType;
    }

    @EventHandler
    public void onPlayerCraftEvent(CraftItemEvent event) {
        // covers CRAFTING (internal and workbench
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if(!context.challengeManager().canTakeEffect(context, player)) {
            return;
        }
        if(event.getCurrentItem() == null || event.getCurrentItem().isEmpty()) {
            return;
        }
        if(!(event.getRecipe() instanceof Keyed keyedRecipe)) {
            return;
        }
        // CRAFTING = 2x2 internal
        // WORKBENCH = 3x3 workbench with crafting table
        boolean internallyCrafted = event.getClickedInventory().getType() == InventoryType.CRAFTING;
        CraftingData<CraftItemEvent> craftingData = craftingType.constructCraftingData(event, keyedRecipe, player.getUniqueId(), internallyCrafted);
        craftingType.triggerIfCheckPasses(craftingData, event);
    }
}
