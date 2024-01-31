package wand555.github.io.challenges;

import net.kyori.adventure.bossbar.BossBar;

public interface BossBarDisplay {

    BossBar createBossBar();



    BossBarPriority getBossBarPriority();

    enum BossBarPriority {
        INFO(0),
        URGENT(100);

        private final int priority;

        BossBarPriority(int priority) {
            this.priority = priority;
        }
    }
}
