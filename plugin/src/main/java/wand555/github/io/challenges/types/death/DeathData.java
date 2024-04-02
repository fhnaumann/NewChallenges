package wand555.github.io.challenges.types.death;

import org.bukkit.damage.DeathMessageType;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.types.Data;

public record DeathData(Player player, int amount, DeathMessageType messageType) implements Data<DeathMessageType> {

    public DeathData(Player player, DeathMessageType messageType) {
        this(player, 1, messageType);
    }

    @Override
    public DeathMessageType mainDataInvolved() {
        return messageType;
    }
}
