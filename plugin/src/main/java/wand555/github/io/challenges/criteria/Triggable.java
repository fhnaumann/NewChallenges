package wand555.github.io.challenges.criteria;

import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;

public interface Triggable<T> {

    public TriggerCheck<T> triggerCheck();

    public Trigger<T> trigger();
}
