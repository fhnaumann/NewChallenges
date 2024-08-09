package wand555.github.io.challenges.types.death;

import org.bukkit.entity.Player;
import wand555.github.io.challenges.mapping.DeathMessage;
import wand555.github.io.challenges.types.Data;

import java.util.UUID;

public record DeathData(
        Player player, int amount, DeathMessage deathMessage, boolean usedTotem
) implements Data<DeathMessage> {

    public DeathData(Player player, DeathMessage deathMessage) {
        this(player, 1, deathMessage, false);
    }

    @Override
    public UUID playerUUID() {
        return player.getUniqueId();
    }

    @Override
    public DeathMessage mainDataInvolved() {
        return deathMessage;
    }
}
