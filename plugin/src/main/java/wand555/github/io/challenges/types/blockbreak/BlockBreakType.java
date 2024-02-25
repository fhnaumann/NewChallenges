package wand555.github.io.challenges.types.blockbreak;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.types.Type;

public class BlockBreakType extends Type<BlockBreakData> implements Listener {

    public BlockBreakType(Context context, TriggerCheck<BlockBreakData> triggerCheck, Trigger<BlockBreakData> whenTriggered) {
        super(context, triggerCheck, whenTriggered);
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material broken = event.getBlock().getType();
        BlockBreakData blockBreakData = new BlockBreakData(broken, player);
        triggerIfCheckPasses(blockBreakData);
    }
}
