package wand555.github.io.challenges.types.mob;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.types.Data;

public record MobData(EntityType entityInteractedWith, Player player) implements Data<EntityType> {
    @Override
    public EntityType mainDataInvolved() {
        return entityInteractedWith;
    }
}
