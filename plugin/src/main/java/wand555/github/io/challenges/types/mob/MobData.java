package wand555.github.io.challenges.types.mob;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.types.Data;

public record MobData(EntityType entityInteractedWith, int amount, Player player) implements Data<EntityType> {

    public MobData(EntityType entityInteractedWith, Player player) {
        this(entityInteractedWith, 1, player);
    }

    @Override
    public EntityType mainDataInvolved() {
        return entityInteractedWith;
    }
}
