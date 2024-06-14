package wand555.github.io.challenges.types.blockbreak;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.types.EventContainer;
import wand555.github.io.challenges.types.Type;

import java.util.Map;

public class BlockBreakType extends Type<BlockBreakData> {

    private final EventContainer<BlockBreakEvent> onBlockBreak;

    public BlockBreakType(Context context, TriggerCheck<BlockBreakData> triggerCheck, Trigger<BlockBreakData> whenTriggered) {
        this(context, triggerCheck, whenTriggered, event -> {});
    }

    public BlockBreakType(Context context, TriggerCheck<BlockBreakData> triggerCheck, Trigger<BlockBreakData> whenTriggered, EventContainer<BlockBreakEvent> onBlockBreak) {
        super(context, triggerCheck, whenTriggered, Map.of(BlockBreakEvent.class, onBlockBreak));
        this.onBlockBreak = onBlockBreak;
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!context.challengeManager().isRunning()) {
            return;
        }
        Player player = event.getPlayer();
        Material broken = event.getBlock().getType();
        BlockBreakData blockBreakData = new BlockBreakData(broken, player);
        triggerIfCheckPasses(blockBreakData, event);
    }
}
