package wand555.github.io.challenges.types.crafting.detectors;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Campfire;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.types.crafting.CraftingData;
import wand555.github.io.challenges.types.crafting.CraftingType;

import java.util.UUID;

public class CampfireCookingDetector implements Listener {

    private final Context context;
    private final CraftingType craftingType;

    public CampfireCookingDetector(Context context, CraftingType craftingType) {
        this.context = context;
        this.craftingType = craftingType;
    }

    @EventHandler
    public void onPlayerPlaceCampfireBlockEvent(BlockPlaceEvent event) {
        // covers CAMPFIRE
        Player player = event.getPlayer();
        BlockState state = event.getBlockPlaced().getState();
        if(state instanceof Campfire campfire) {
            craftingType.markLastOpenedByOn(campfire, player);
        }
    }

    @EventHandler
    public void onPlayerStartCampfireEvent(PlayerInteractEvent event) {
        // covers CAMPFIRE
        if(event.getClickedBlock() == null) {
            return;
        }
        if(event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        if(!(event.getClickedBlock().getState() instanceof Campfire campfire)) {
            return;
        }
        craftingType.markLastOpenedByOn(campfire, event.getPlayer());
    }

    @EventHandler
    public void onPlayerCampfireSmeltFinishEvent(BlockCookEvent event) {
        // covers CAMPFIRE
        if(!(event.getRecipe() instanceof CampfireRecipe)) {
            return;
        }
        craftingType.handleBlockCookEvent(event);
        // Fix: If the event is canceled (CancelPunishment), then this leads to an infinite spiral call.
        // Manually intervene and spawn the original itemstack in the world
        if(event.isCancelled()) {
            event.setCancelled(false); // prevent infinite spiral
            event.setResult(new ItemStack(Material.AIR));
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), event.getSource());
        }
    }
}
