package wand555.github.io.challenges.criteria.goals;

import wand555.github.io.challenges.*;
import wand555.github.io.challenges.generated.GoalsConfig;

public abstract class BaseGoal implements Goal, JSONConfigGroup<GoalsConfig>, StatusInfo {

    protected final Context context;
    private boolean complete;

    public BaseGoal(Context context) {
        this(context, false);
    }

    public BaseGoal(Context context, boolean complete) {
        this.context = context;
        this.complete = complete;
    }

    public abstract void onStart();

    public abstract void onComplete();

    protected final void notifyManager() {
        context.challengeManager().onGoalCompleted();
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
