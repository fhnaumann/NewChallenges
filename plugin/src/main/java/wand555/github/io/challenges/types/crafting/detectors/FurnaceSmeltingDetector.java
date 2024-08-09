package wand555.github.io.challenges.types.crafting.detectors;

import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.FurnaceRecipe;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.types.crafting.CraftingData;
import wand555.github.io.challenges.types.crafting.CraftingType;

import java.util.UUID;

public class FurnaceSmeltingDetector implements Listener {

    private final Context context;
    private final CraftingType craftingType;

    public FurnaceSmeltingDetector(Context context, CraftingType craftingType) {
        this.context = context;
        this.craftingType = craftingType;
    }

    @EventHandler
    public void onPlayerPlaceFurnaceLikeBlockEvent(BlockPlaceEvent event) {
        // covers FURNACE, SMOKING, BLASTING
        Player player = event.getPlayer();
        BlockState state = event.getBlockPlaced().getState();
        if(state instanceof Furnace furnace) {
            craftingType.markLastOpenedByOn(furnace, player);
        }
    }

    @EventHandler
    public void onPlayerStartFurnaceSmeltingEvent(InventoryOpenEvent event) {
        // covers FURNACE, SMOKING, BLASTING
        if(!(event.getPlayer() instanceof Player player)) {
            return;
        }
        if(!(event.getInventory() instanceof FurnaceInventory furnaceInventory)) {
            return;
        }
        if(furnaceInventory.getHolder() == null) {
            return;
        }
        craftingType.markLastOpenedByOn(furnaceInventory.getHolder(), player);
    }

    @EventHandler
    public void onPlayerFurnaceLikeSmeltFinishEvent(FurnaceSmeltEvent event) {
        // covers FURNACE, SMOKING, BLASTING
        if(!(event.getRecipe() instanceof FurnaceRecipe)) {
            return;
        }
        craftingType.handleBlockCookEvent(event);
    }
}
