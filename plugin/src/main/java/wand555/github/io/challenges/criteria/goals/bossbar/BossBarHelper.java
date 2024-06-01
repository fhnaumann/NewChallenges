package wand555.github.io.challenges.criteria.goals.bossbar;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import wand555.github.io.challenges.BossBarDisplay;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarBuilder;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarPart;
import wand555.github.io.challenges.types.Data;
import wand555.github.io.challenges.utils.TimerUtil;

import java.util.List;
import java.util.Map;

public class BossBarHelper {

    protected final Context context;
    protected BossBar bossBar;
    protected boolean hasTimer;

    protected final List<BossBarPart<?>> parts;

    public BossBarHelper(Context context, List<BossBarPart<?>> parts) {
        this.context = context;
        this.parts = parts;
        this.bossBar = new BossBarBuilder().parts(parts).build();
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public void updateBossBar() {
        BossBarBuilder bossBarBuilder = new BossBarBuilder().parts(parts);
        bossBar.name(bossBarBuilder.buildName());
        bossBar.progress(bossBarBuilder.buildProgress());
    }

    /*
    private BossBar createTimerBossBar(int time) {
        Component formattedBossBarComponent = formatTimeLimitBossBarComponent(time).appendSpace().append(formatGoalNameAsPrefix());
        return BossBar.bossBar(formattedBossBarComponent, 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    protected Component formatTimeLimitBossBarComponent(int time) {
        Map<TimerUtil.TimeParts, String> mappedTime = TimerUtil.format(time);
        Component timer = ComponentUtil.formatTimer(context.plugin(), context.resourceBundleContext().goalResourceBundle(), "goal.bossbar.time_left", mappedTime);
        return timer;
    }

    protected Component formatGoalNameAsPrefix() {
        return Component.text("(").append(ComponentUtil.formatBossBarMessage(context.plugin(), context.resourceBundleContext().goalResourceBundle(), "%s.name".formatted(getGoalNameInResourceBundle()), Map.of(), Map.of())).append(Component.text(")"));
    }*/

}
