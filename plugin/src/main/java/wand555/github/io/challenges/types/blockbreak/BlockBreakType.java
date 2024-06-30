package wand555.github.io.challenges.types.blockbreak;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.types.EventContainer;
import wand555.github.io.challenges.types.Type;
import wand555.github.io.challenges.validation.goals.BlockBreakGoalValidator;

import java.util.Map;
import java.util.logging.Logger;

public class BlockBreakType extends Type<BlockBreakData> {

    private static final Logger logger = ChallengesDebugLogger.getLogger(BlockBreakType.class);

    public BlockBreakType(Context context, TriggerCheck<BlockBreakData> triggerCheck, Trigger<BlockBreakData> whenTriggered) {
        this(context, triggerCheck, whenTriggered, event -> {});
    }

    public BlockBreakType(Context context, TriggerCheck<BlockBreakData> triggerCheck, Trigger<BlockBreakData> whenTriggered, EventContainer<BlockBreakEvent> onBlockBreak) {
        super(context, triggerCheck, whenTriggered, Map.of(BlockBreakEvent.class, onBlockBreak));
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
        logger.fine("Registered listeners for %s.".formatted(getClass().getSimpleName()));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        String eventClassName = event.getEventName();
        logger.fine("%s triggered in %s.".formatted(eventClassName, getClass().getSimpleName()));
        if(!context.challengeManager().isRunning()) {
            return;
        }
        logger.fine("Initial check for %s passes.".formatted(eventClassName));
        Player player = event.getPlayer();
        Material broken = event.getBlock().getType();
        BlockBreakData blockBreakData = new BlockBreakData(broken, player);
        logger.fine("Construct data '%s'.".formatted(blockBreakData));
        triggerIfCheckPasses(blockBreakData, event);
    }
}
