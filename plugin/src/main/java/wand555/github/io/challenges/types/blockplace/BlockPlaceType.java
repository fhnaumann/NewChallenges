package wand555.github.io.challenges.types.blockplace;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.generated.MCEventAlias;
import wand555.github.io.challenges.mlg.MLGHandler;
import wand555.github.io.challenges.types.EventContainer;
import wand555.github.io.challenges.types.Type;

public class BlockPlaceType extends Type<BlockPlaceData> {

    public BlockPlaceType(Context context, TriggerCheck<BlockPlaceData> triggerCheck, Trigger<BlockPlaceData> whenTriggered, MCEventAlias.EventType eventType) {
        super(context, triggerCheck, whenTriggered, eventType);
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!context.challengeManager().isRunning()) {
            return;
        }
        if(MLGHandler.isInMLGWorld(context.plugin(), event.getPlayer())) {
            // don't act when the player placed a bucket from performing an MLG
            return;
        }
        Player player = event.getPlayer();
        Material placed = event.getBlockPlaced().getType();
        BlockPlaceData blockPlaceData = new BlockPlaceData(event, context.challengeManager().getTime(), placed, player);
        triggerIfCheckPasses(blockPlaceData, event);
    }
}
