package wand555.github.io.challenges.criteria.goals;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.MessageHelper;

public abstract class GoalMessageHelper<T> extends MessageHelper {
    public GoalMessageHelper(Context context) {
        super(context);
    }

    public abstract void sendSingleStepAction(T data, Collect collect);
    public abstract void sendSingleReachedAction(T data, Collect collect);
    public abstract void sendAllReachedAction();

}
