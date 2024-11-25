package wand555.github.io.challenges.types.mob;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import wand555.github.io.challenges.generated.MobDataConfig;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.types.Data;
import wand555.github.io.challenges.utils.LiveUtil;

import java.util.Objects;
import java.util.UUID;

public record MobData(EntityDeathEvent event, int timestamp, EntityType entityInteractedWith, int amount, Player player) implements Data<EntityDeathEvent, EntityType> {

    public MobData(EntityDeathEvent event, int timestamp, EntityType entityInteractedWith, Player player) {
        this(event, timestamp, entityInteractedWith, 1, player);
    }

    @Override
    public UUID playerUUID() {
        return player.getUniqueId();
    }

    @Override
    public EntityType mainDataInvolved() {
        return entityInteractedWith;
    }

    @Override
    public Object constructMCEventData() {
        return new MobDataConfig(
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
        MobData mobData = (MobData) o;
        return amount == mobData.amount && entityInteractedWith == mobData.entityInteractedWith && Objects.equals(
                player,
                mobData.player
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityInteractedWith, amount, player);
    }
}
