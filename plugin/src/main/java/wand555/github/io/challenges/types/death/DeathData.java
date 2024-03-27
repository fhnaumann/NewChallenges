package wand555.github.io.challenges.types.death;

import org.bukkit.damage.DeathMessageType;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.types.Data;

public record DeathData(Player player, DeathMessageType messageType) implements Data<DeathMessageType> {
    @Override
    public DeathMessageType mainDataInvolved() {
        return messageType;
    }
}
