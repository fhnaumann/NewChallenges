package wand555.github.io.challenges.goals;

import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;

public abstract class Goal {

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
