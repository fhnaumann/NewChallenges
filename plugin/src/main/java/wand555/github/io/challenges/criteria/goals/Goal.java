package wand555.github.io.challenges.criteria.goals;

import org.bukkit.entity.Player;
import wand555.github.io.challenges.ResourceBundleNarrowable;
import wand555.github.io.challenges.criteria.Criteria;
import wand555.github.io.challenges.criteria.Loadable;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarHelper;

public interface Goal extends Criteria, ResourceBundleNarrowable, Loadable {

    public boolean isComplete();

    public void setComplete(boolean complete);

    public boolean determineComplete();

    @Override
    default void onStart() {
        throw new RuntimeException("Criteria may not be called without a team!");
    }

    @Override
    default void onEnd() {
        throw new RuntimeException("Criteria may not be called without a team!");
    }

    public abstract void onComplete(Player lastCompletionStepProvidedBy);

    public boolean hasTimer();

    public Timer getTimer();

    public BossBarHelper getBossBarHelper();
}
