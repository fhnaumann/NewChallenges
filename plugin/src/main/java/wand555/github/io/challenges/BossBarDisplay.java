package wand555.github.io.challenges;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface BossBarDisplay {

    public BossBar getBossBar();

    default void showBossBar(Player player) {
        if(getBossBar() != null) {
            player.showBossBar(getBossBar());
        }

    }

    default void showBossBar(Collection<? extends Player> players) {
        players.forEach(this::showBossBar);
    }

    default void removeBossBar(Player player) {
        player.hideBossBar(getBossBar());
    }

    default void removeBossBar(Collection<? extends Player> players) {
        players.forEach(this::removeBossBar);
    }

    enum BossBarPriority {
        INFO(0),
        URGENT(100);

        private final int priority;

        BossBarPriority(int priority) {
            this.priority = priority;
        }
    }
}
