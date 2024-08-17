package wand555.github.io.challenges.types;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;
import java.util.UUID;

public interface Data<E extends Event, K> {

    default Player player() {
        return Bukkit.getPlayer(playerUUID());
    }

    default String playerName() {
        return Bukkit.getOfflinePlayer(playerUUID()).getName();
    }

    UUID playerUUID();

    K mainDataInvolved();

    int amount();

    @Nullable
    E event();
}
