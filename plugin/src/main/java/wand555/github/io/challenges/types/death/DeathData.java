package wand555.github.io.challenges.types.death;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import wand555.github.io.challenges.mapping.DeathMessage;
import wand555.github.io.challenges.types.Data;

import java.util.Objects;
import java.util.UUID;

public record DeathData(
        PlayerDeathEvent event,
        Player player, int amount, DeathMessage deathMessage, boolean usedTotem
) implements Data<PlayerDeathEvent, DeathMessage> {

    public DeathData(PlayerDeathEvent event, Player player, DeathMessage deathMessage) {
        this(event, player, 1, deathMessage, false);
    }

    @Override
    public UUID playerUUID() {
        return player.getUniqueId();
    }

    @Override
    public DeathMessage mainDataInvolved() {
        return deathMessage;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        DeathData deathData = (DeathData) o;
        return amount == deathData.amount && usedTotem == deathData.usedTotem && Objects.equals(player,
                                                                                                deathData.player
        ) && Objects.equals(deathMessage, deathData.deathMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, amount, deathMessage, usedTotem);
    }
}
