package wand555.github.io.challenges.goals;

import wand555.github.io.challenges.*;
import wand555.github.io.challenges.generated.GoalsConfig;

public abstract class Goal implements JSONConfigGroup<GoalsConfig>, StatusInfo {

    protected final Context context;
    private boolean complete;

    public Goal(Context context) {
        this(context, false);
    }

    public Goal(Context context, boolean complete) {
        this.context = context;
        this.complete = complete;
    }

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
