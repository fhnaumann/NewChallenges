package wand555.github.io.challenges.types.crafting.detectors;

import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.inventory.StonecuttingRecipe;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.types.crafting.CraftingData;
import wand555.github.io.challenges.types.crafting.CraftingType;

import java.util.HashMap;
import java.util.Map;

public class StoneCuttingDetector implements Listener {

    private final Context context;
    private final CraftingType craftingType;
    private final Map<Player, StonecuttingRecipe> selectedStonecuttingRecipes;

    public StoneCuttingDetector(Context context, CraftingType craftingType) {
        this.context = context;
        this.craftingType = craftingType;
        this.selectedStonecuttingRecipes = new HashMap<>();
    }

    @EventHandler
    public void onPlayerFinishStoneCuttingEvent(InventoryClickEvent event) {
        // covers stonecutting
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if(!context.challengeManager().canTakeEffect(context, player)) {
            return;
        }
        if(event.getCurrentItem() == null || event.getCurrentItem().isEmpty()) {
            return;
        }
        if(event.getSlot() != 1) {
            // stonecutter inventory has its result slot at index 1
            return;
        }
        if(!(event.getClickedInventory() instanceof StonecutterInventory stonecutterInventory)) {
            return;
        }
        if(!selectedStonecuttingRecipes.containsKey(player)) {
            return;
        }
        CraftingData craftingData = craftingType.constructCraftingData(selectedStonecuttingRecipes.get(player), player.getUniqueId(), false);
        craftingType.triggerIfCheckPasses(craftingData, event);
    }

    @EventHandler
    public void onPlayerSelectStonecuttingRecipe(PlayerStonecutterRecipeSelectEvent event) {
        selectedStonecuttingRecipes.put(event.getPlayer(), event.getStonecuttingRecipe());
    }

    @EventHandler
    public void onPlayerDeselectStonecuttingRecipe(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player)) {
            return;
        }
        selectedStonecuttingRecipes.remove(player);
    }

    @EventHandler
    public void onPlayerDeselectStonecuttingRecipe(PlayerQuitEvent event) {
        selectedStonecuttingRecipes.remove(event.getPlayer());
    }
}
