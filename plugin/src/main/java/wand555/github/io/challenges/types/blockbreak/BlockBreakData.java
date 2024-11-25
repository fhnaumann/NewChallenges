package wand555.github.io.challenges.types.blockbreak;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import wand555.github.io.challenges.generated.BlockBreakDataConfig;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.types.Data;
import wand555.github.io.challenges.utils.LiveUtil;

import java.util.Objects;
import java.util.UUID;

public record BlockBreakData(BlockBreakEvent event, int timestamp, Material broken, int amount, Player player) implements Data<BlockBreakEvent, Material> {

    public BlockBreakData(BlockBreakEvent event, int timestamp, Material broken, Player player) {
        this(event, timestamp, broken, 1, player);
    }

    @Override
    public UUID playerUUID() {
        return player.getUniqueId();
    }

    @Override
    public Material mainDataInvolved() {
        return broken;
    }

    @Override
    public Object constructMCEventData() {
        return new BlockBreakDataConfig(
                amount(),
                DataSourceJSON.toCode(broken()),
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
        BlockBreakData data = (BlockBreakData) o;
        return amount == data.amount && broken == data.broken && Objects.equals(player, data.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(broken, amount, player);
    }
}
