package wand555.github.io.challenges.criteria.goals;

import org.bukkit.entity.Player;

public interface CollectAmountableGoal<T> extends Goal {

    default void collectableCollected(Player collector, T collected) {
        if(isNewCollected(collector, collected)) {
            newCollectableCollected(collector, collected);
        }
        if(determineComplete()) {
            setComplete(true);
            onComplete();
        }
    }

    public boolean isNewCollected(Player collector, T collected);

    public void newCollectableCollected(Player collector, T collected);

}
