package wand555.github.io.challenges.criteria.settings.floorislava;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.Context;

import java.util.logging.Logger;

public class BlockChangeTimer extends BukkitRunnable {

    private static final Logger logger = ChallengesDebugLogger.getLogger(BlockChangeTimer.class);

    private final Context context;
    private final Location blockLocationToChange;
    private final FloorBlock floorBlock;
    private final FloorIsLavaSetting floorIsLavaSetting;

    public BlockChangeTimer(Context context, Location blockLocationToChange, FloorBlock floorBlock, FloorIsLavaSetting floorIsLavaSetting) {
        this.context = context;
        this.blockLocationToChange = blockLocationToChange;
        this.floorBlock = floorBlock;
        this.floorIsLavaSetting = floorIsLavaSetting;
    }

    public BlockChangeTimer start() {
        logger.finest("Started new runnable with %s, %s".formatted(blockLocationToChange, floorBlock));
        runTaskLater(context.plugin(), floorIsLavaSetting.getTimeToNextBlockChangeInTicks());
        return this;
    }

    @Override
    public void run() {
        switch(floorBlock.status()) {
            case REGULAR -> applyAndScheduleNewWith(Material.MAGMA_BLOCK, FloorBlock.FloorBlockStatus.MAGMA);
            case MAGMA -> applyAndScheduleNewWith(Material.LAVA, FloorBlock.FloorBlockStatus.LAVA);
            case LAVA -> {
                if(!floorIsLavaSetting.isLavaRemainsPermanently()) {
                    blockLocationToChange.getBlock().setType(floorBlock.previousMaterial());
                }
                floorIsLavaSetting.getBlockChangeTimerMap().remove(blockLocationToChange);
                floorIsLavaSetting.getFloorBlockMap().remove(blockLocationToChange);
            }
        }
    }

    public void abort() {
        cancel();
        blockLocationToChange.getBlock().setType(floorBlock.previousMaterial());
    }

    private void applyAndScheduleNewWith(Material material, FloorBlock.FloorBlockStatus status) {
        blockLocationToChange.getBlock().setType(material);
        floorIsLavaSetting.moveToNextBlockChangeTimer(blockLocationToChange, floorBlock, status);
    }
}
