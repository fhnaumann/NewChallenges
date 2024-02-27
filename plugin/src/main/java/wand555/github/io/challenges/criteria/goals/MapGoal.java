package wand555.github.io.challenges.criteria.goals;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.types.item.ItemData;

import java.util.Collections;
import java.util.List;

/**
 * Any goal where there may be multiple "mini" goals to complete. For example, this includes potentially many mobs, items, etc.
 * In constrast, something like "collect 10 XP levels" is not a map goal, because it is a singular goal that may be reached.
 * @param <T>
 * @param <S>
 */
public abstract class MapGoal<T extends Enum<T>, S> extends BaseGoal implements Triggable<S> {

    protected final GoalCollector<T> goalCollector;
    protected final GoalMessageHelper<S> messageHelper;
    protected final boolean fixedOrder;

    public MapGoal(Context context, boolean complete, boolean fixedOrder, boolean shuffled, List<CollectableEntryConfig> collectables, Class<T> enumType, GoalMessageHelper<S> messageHelper) {
        super(context, complete);
        if(fixedOrder && !shuffled) {
            Collections.shuffle(collectables);
        }
        this.goalCollector = new GoalCollector<>(context, collectables, enumType);
        this.messageHelper = messageHelper;
        this.fixedOrder = fixedOrder;
    }

    @Override
    public boolean determineComplete() {
        return goalCollector.isComplete();
    }

    @Override
    public void onComplete() {
        setComplete(true);
        messageHelper.sendAllReachedAction();
        notifyManager();
    }

    protected abstract T getComparingDataContent(S data);

    @Override
    public TriggerCheck<S> triggerCheck() {
        return data -> {
            if(isComplete()) {
                return false;
            }
            Collect toCollect = goalCollector.getToCollect().get(getComparingDataContent(data));
            if(toCollect == null || toCollect.isComplete()) {
                return false;
            }
            if(fixedOrder) {
                return goalCollector.getCurrentlyToCollect().getKey() == getComparingDataContent(data);
            }
            return true;
        };
    }

    @Override
    public Trigger<S> trigger() {
        return data -> {

            Collect updatedCollect = updateCollect(data);
            if(updatedCollect.isComplete()) {
                messageHelper.sendSingleReachedAction(data, updatedCollect);
            }
            else {
                messageHelper.sendSingleStepAction(data, updatedCollect);
            }

            if(determineComplete()) {
                onComplete();
            }
        };
    }

    protected Collect updateCollect(S data) {
        return goalCollector.getToCollect().computeIfPresent(getComparingDataContent(data), (material, collect) -> {
            // default behaviour is to add exactly 1 to the collect
            // subclasses may override this behaviour
            collect.setCurrentAmount(collect.getCurrentAmount()+1);
            return collect;
        });
    }
}
