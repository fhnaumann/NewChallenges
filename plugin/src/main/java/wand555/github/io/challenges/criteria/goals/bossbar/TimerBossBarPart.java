package wand555.github.io.challenges.criteria.goals.bossbar;

import net.kyori.adventure.text.Component;
import net.sf.saxon.om.Function;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.utils.TimerUtil;

import java.util.Map;
import java.util.function.Supplier;

public class TimerBossBarPart extends BossBarPart<Object> implements BuildProgress {

    private final Timer timer;

    public TimerBossBarPart(Context context, Timer timer) {
        super(context, new GoalInformation<>("", new NoAdditionalPlaceholderInformation()));
        this.timer = timer;
    }

    @Override
    public Component buildPart() {
        Map<TimerUtil.TimeParts, String> mappedTime = TimerUtil.format(timer.getTime());
        Component timer = ComponentUtil.formatTimer(context.plugin(), context.resourceBundleContext().goalResourceBundle(), "goal.bossbar.time_left", mappedTime);
        return timer;
    }

    @Override
    public float buildProgress() {
        return ((float) timer.getTime()) / timer.getStartingTime();
    }
}
