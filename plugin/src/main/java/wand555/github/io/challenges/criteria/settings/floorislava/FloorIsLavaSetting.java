package wand555.github.io.challenges.criteria.settings.floorislava;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.criteria.settings.BaseSetting;
import wand555.github.io.challenges.generated.FloorIsLavaSettingConfig;
import wand555.github.io.challenges.generated.SettingsConfig;
import wand555.github.io.challenges.mlg.MLGHandler;

import java.util.HashMap;
import java.util.Map;

public class FloorIsLavaSetting extends BaseSetting implements Storable<FloorIsLavaSettingConfig>, Listener {

    private final int timeToNextBlockChangeInTicks;
    private final boolean lavaRemainsPermanently;

    private final Map<Location, FloorBlock> floorBlockMap;
    private final Map<Location, BlockChangeTimer> blockChangeTimerMap;

    public FloorIsLavaSetting(Context context, FloorIsLavaSettingConfig config) {
        super(context);
        this.timeToNextBlockChangeInTicks = config.getTimeToNextBlockChangeInTicks();
        this.lavaRemainsPermanently = config.isLavaRemainsPermanently();
        this.floorBlockMap = new HashMap<>();
        this.blockChangeTimerMap = new HashMap<>();

        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @Override
    public void addToGeneratedConfig(SettingsConfig config) {
        config.setFloorIsLavaSetting(toGeneratedJSONClass());
    }

    @Override
    public FloorIsLavaSettingConfig toGeneratedJSONClass() {
        return new FloorIsLavaSettingConfig(lavaRemainsPermanently, timeToNextBlockChangeInTicks);
    }

    @Override
    public void onEnd() {
        blockChangeTimerMap.forEach((location, blockChangeTimer) -> blockChangeTimer.abort());
    }

    @EventHandler
    public void onPlayerMoveWithFloorIsLavaChallenge(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        // we don't care if the player is possibly spoofing using a hacked client, this a fun plugin
        if(!Challenges.isLoadedFromTests(context.plugin().getServer())) {
            if(!player.isOnGround()) { // throws UnImplementedException in tests
                return;
            }
        }
        if(!context.challengeManager().canTakeEffect(context, player)) {
            return;
        }
        if(MLGHandler.isInMLGWorld(context.plugin(), player)) {
            return;
        }
        if(event.getFrom().getBlock().getType() == Material.LAVA) {
            // don't set the floor to lava if it already is lava
            return;
        }
        Location locationOfInterest = event.getFrom().clone().subtract(0, 1, 0);

        if(hasBlockChangeTimerAlreadyScheduledForBlockLocation(locationOfInterest)) {
            return;
        }
        startFloorBlockTransformation(locationOfInterest);
    }

    public void startFloorBlockTransformation(Location source) {
        Preconditions.checkArgument(!floorBlockMap.containsKey(source), "%s already in the floorBlockMap!".formatted(source.toString()));
        floorBlockMap.put(source, new FloorBlock(FloorBlock.FloorBlockStatus.REGULAR, source.getBlock().getType()));
        blockChangeTimerMap.put(source, new BlockChangeTimer(context, source, floorBlockMap.get(source), this).start());
    }

    public void moveToNextBlockChangeTimer(Location source, FloorBlock oldFloorBlock, FloorBlock.FloorBlockStatus newStatus) {
        Preconditions.checkArgument(blockChangeTimerMap.containsKey(source), "%s is not in the map!".formatted(source.toString()));
        floorBlockMap.put(source, new FloorBlock(newStatus, oldFloorBlock.previousMaterial()));
        blockChangeTimerMap.put(source, new BlockChangeTimer(context, source, floorBlockMap.get(source), this).start());
    }

    private boolean hasBlockChangeTimerAlreadyScheduledForBlockLocation(Location blockLoc) {
        return getBlockChangeTimerMap().entrySet().stream().anyMatch(locationBlockChangeTimerEntry -> isSameBlock(blockLoc, locationBlockChangeTimerEntry.getKey()));
    }

    private boolean isSameBlock(Location from, Location to) {
        return from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ();
    }

    public int getTimeToNextBlockChangeInTicks() {
        return timeToNextBlockChangeInTicks;
    }

    public boolean isLavaRemainsPermanently() {
        return lavaRemainsPermanently;
    }

    public Map<Location, FloorBlock> getFloorBlockMap() {
        return floorBlockMap;
    }

    public Map<Location, BlockChangeTimer> getBlockChangeTimerMap() {
        return blockChangeTimerMap;
    }
}
