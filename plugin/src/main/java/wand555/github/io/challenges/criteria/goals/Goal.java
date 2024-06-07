package wand555.github.io.challenges.criteria.goals;

import wand555.github.io.challenges.ResourceBundleNarrowable;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarHelper;

public interface Goal extends ResourceBundleNarrowable {

    public boolean isComplete();

    public void setComplete(boolean complete);

    public boolean determineComplete();

    public abstract void onComplete();

    public boolean hasTimer();

    public Timer getTimer();

    public BossBarHelper getBossBarHelper();
}
