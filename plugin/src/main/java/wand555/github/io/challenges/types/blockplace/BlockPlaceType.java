package wand555.github.io.challenges.types.blockplace;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.types.EventContainer;
import wand555.github.io.challenges.types.Type;

import java.util.Map;

public class BlockPlaceType extends Type<BlockPlaceData> {

    public BlockPlaceType(Context context, TriggerCheck<BlockPlaceData> triggerCheck, Trigger<BlockPlaceData> whenTriggered) {
        this(context, triggerCheck, whenTriggered, event -> {});
    }

    public BlockPlaceType(Context context, TriggerCheck<BlockPlaceData> triggerCheck, Trigger<BlockPlaceData> whenTriggered, EventContainer<BlockPlaceEvent> onBlockPlace) {
        super(context, triggerCheck, whenTriggered, Map.of(BlockPlaceEvent.class, onBlockPlace));
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!context.challengeManager().isRunning()) {
            return;
        }
        Player player = event.getPlayer();
        Material placed = event.getBlockPlaced().getType();
        BlockPlaceData blockPlaceData = new BlockPlaceData(placed, player);
        triggerIfCheckPasses(blockPlaceData, event);
    }

}
