package wand555.github.io.challenges.criteria.goals;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Keyed;
import wand555.github.io.challenges.BossBarDisplay;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.types.Data;

public abstract class BossBarHelper {

    protected final Context context;
    protected BossBar bossBar;

    protected BossBarHelper(Context context) {
        this.context = context;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public abstract void updateBossBar();
}
