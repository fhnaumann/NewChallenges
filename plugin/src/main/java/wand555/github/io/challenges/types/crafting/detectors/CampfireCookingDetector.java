package wand555.github.io.challenges.types.crafting.detectors;

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
        craftingType.handleBlockCookEvent(event);
    }
}
