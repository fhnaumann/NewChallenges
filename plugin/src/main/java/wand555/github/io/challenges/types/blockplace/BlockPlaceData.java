package wand555.github.io.challenges.types.blockplace;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import wand555.github.io.challenges.generated.BlockPlaceDataConfig;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.types.Data;
import wand555.github.io.challenges.utils.LiveUtil;

import java.util.Objects;
import java.util.UUID;

public record BlockPlaceData(BlockPlaceEvent event, int timestamp, Material placed, int amount, Player player) implements Data<BlockPlaceEvent, Material> {

    public BlockPlaceData(BlockPlaceEvent event, int timestamp, Material placed, Player player) {
        this(event, timestamp, placed, 1, player);
    }

    @Override
    public UUID playerUUID() {
        return player.getUniqueId();
    }

    @Override
    public Material mainDataInvolved() {
        return placed;
    }

    @Override
    public Object constructMCEventData() {
        return new BlockPlaceDataConfig(
                amount(),
                DataSourceJSON.toCode(mainDataInvolved()),
                LiveUtil.constructPlayerConfig(playerUUID()),
                timestamp()
        );
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockPlaceData that = (BlockPlaceData) o;
        return amount == that.amount && placed == that.placed && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placed, amount, player);
    }
}
