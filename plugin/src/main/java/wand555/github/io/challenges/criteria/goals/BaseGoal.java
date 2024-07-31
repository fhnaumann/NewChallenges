package wand555.github.io.challenges.criteria.goals;

import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarBuilder;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarHelper;
import wand555.github.io.challenges.criteria.goals.bossbar.GoalNameBossBarPart;
import wand555.github.io.challenges.criteria.goals.bossbar.TimerBossBarPart;
import wand555.github.io.challenges.generated.GoalsConfig;

import javax.annotation.Nullable;
import java.util.ResourceBundle;

public abstract class BaseGoal implements Goal, JSONConfigGroup<GoalsConfig>, StatusInfo {

    protected final Context context;

    protected @Nullable Timer timer;
    protected BossBarHelper bossBarHelper;
    private boolean complete;

    protected BaseGoal(Context context, boolean complete, @Nullable Timer timer) {
        this.context = context;
        this.complete = complete;
        this.timer = timer;
        BossBarBuilder bossBarBuilder = new BossBarBuilder();
        if(hasTimer()) {
            bossBarBuilder.then(new TimerBossBarPart(context, timer)).then(new GoalNameBossBarPart(context,
                                                                                                   getNameInResourceBundle()
            ));
        }
        this.bossBarHelper = new BossBarHelper(context, bossBarBuilder.getParts());
    }

    public abstract void onStart();

    public void onComplete() {
        setComplete(true);
    }

    protected final void notifyManager(ChallengeManager.GoalCompletion goalCompletion) {
        context.challengeManager().onGoalCompleted(goalCompletion);
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public boolean hasTimer() {
        return timer != null;
    }

    @Nullable
    public Timer getTimer() {
        return timer;
    }

    @Nullable
    public BossBarHelper getBossBarHelper() {
        return bossBarHelper;
    }

    @Override
    public ResourceBundle getSpecificBundle() {
        return context.resourceBundleContext().goalResourceBundle();
    }
}
