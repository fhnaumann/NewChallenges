package wand555.github.io.challenges.criteria.goals;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Keyed;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.exceptions.UnskippableException;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.types.Data;

/**
 * Any goal where there may be multiple "mini" goals to complete. For example, this includes potentially many mobs, items, etc.
 * In contrast, something like "collect 10 XP levels" is not a map goal, because it is a singular goal that may be reached.
 * @param <K> The underlying enum in the data object (BlockBreakData -> Material, MobData -> EntityType, ...)
 * @param <D> Any data object (BlockBreakData, MobData, ItemData, ...)
 */
public abstract class MapGoal<D extends Data<K>, K extends Keyed> extends BaseGoal implements Triggable<D>, Skippable, BossBarDisplay {

    protected final GoalCollector<K> goalCollector;
    private final MapGoalBossBarHelper<K> bossBarHelper;
    protected final GoalMessageHelper<D, K> messageHelper;
    protected final CollectedInventory<D, K> collectedInventory;

    public MapGoal(Context context, boolean complete, GoalCollector<K> goalCollector, GoalMessageHelper<D, K> messageHelper, MapGoalBossBarHelper<K> bossBarHelper, CollectedInventory<D, K> collectedInventory) {
        super(context, complete);
        this.goalCollector = goalCollector;
        this.bossBarHelper = bossBarHelper;
        this.messageHelper = messageHelper;
        this.collectedInventory = collectedInventory;
    }

    @Override
    public boolean determineComplete() {
        return goalCollector.isComplete();
    }

    @Override
    public void onStart() {
        showBossBar(context.plugin().getServer().getOnlinePlayers());
    }

    @Override
    public void onComplete() {
        setComplete(true);
        messageHelper.sendAllReachedAction();

        if(bossBarHelper.getBossBar() != null) {
            removeBossBar(context.plugin().getServer().getOnlinePlayers());
        }

        notifyManager();
    }

    @Override
    public TriggerCheck<D> triggerCheck() {
        return data -> {
            if(isComplete()) {
                return false;
            }
            Collect toCollect = goalCollector.getToCollect().get(data.mainDataInvolved());
            if(toCollect == null || toCollect.isComplete()) {
                return false;
            }
            if(goalCollector.isFixedOrder()) {
                return goalCollector.getCurrentlyToCollect().getKey() == data.mainDataInvolved();
            }
            return true;
        };
    }

    @Override
    public Trigger<D> trigger() {
        return data -> {
            Collect updatedCollect = updateCollect(data);
            if(updatedCollect.isComplete()) {
                messageHelper.sendSingleReachedAction(data, updatedCollect);
                if(goalCollector.hasNext()) {
                    // move currentlyToSelect to the next object
                    goalCollector.next();
                }
            }
            else {
                messageHelper.sendSingleStepAction(data, updatedCollect);
            }

            // update collected inventory
            collectedInventory.addOrUpdate(data.mainDataInvolved(), updatedCollect);

            if(determineComplete()) {
                onComplete();
            }
            bossBarHelper.updateBossBar();
        };
    }

    protected Collect updateCollect(D data) {
        return goalCollector.getToCollect().computeIfPresent(data.mainDataInvolved(), (material, collect) -> {
            // default behaviour is to add exactly 1 to the collect
            // subclasses may override this behaviour
            collect.setCurrentAmount(collect.getCurrentAmount()+1);
            return collect;
        });
    }

    @Override
    public void onSkip() throws UnskippableException {
        if(!goalCollector.isFixedOrder()) {
            throw new UnskippableException();
        }
        goalCollector.getCurrentlyToCollect().getValue().setCurrentAmount(goalCollector.getCurrentlyToCollect().getValue().getAmountNeeded());
        if(determineComplete()) {
            // there are no other collects left to complete -> skipping the active collect completes the goal
            onComplete();
        }
        else {
            bossBarHelper.updateBossBar();
        }

    }

    @Override
    public BossBar getBossBar() {
        return bossBarHelper.getBossBar();
    }

    public GoalCollector<K> getGoalCollector() {
        return goalCollector;
    }

    public boolean isFixedOrder() {
        return goalCollector.isFixedOrder();
    }
}
