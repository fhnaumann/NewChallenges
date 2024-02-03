package wand555.github.io.challenges;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface BossBarDisplay {

    public BossBar createBossBar();

    public BossBar getBossBar();

    public BossBarPriority getBossBarPriority();

    default void showBossBar(Player player) {
        player.showBossBar(getBossBar());
    }

    default void showBossBar(Collection<? extends Player> players) {
        players.forEach(this::showBossBar);
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
