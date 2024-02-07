package wand555.github.io.challenges.types;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;

public abstract class Type<T> {

    protected final Context context;

    protected final TriggerCheck<T> triggerCheck;
    protected final Trigger<T> whenTriggered;

    public Type(Context context, TriggerCheck<T> triggerCheck, Trigger<T> whenTriggered) {
        this.context = context;
        this.triggerCheck = triggerCheck;
        this.whenTriggered = whenTriggered;
    }

    protected void triggerIfCheckPasses(T data) {
        if(triggerCheck.applies(data)) {
            whenTriggered.actOnTriggered(data);
        }
    }
}
