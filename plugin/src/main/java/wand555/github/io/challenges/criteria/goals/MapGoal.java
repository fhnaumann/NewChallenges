package wand555.github.io.challenges.criteria.goals;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.criteria.goals.bossbar.*;
import wand555.github.io.challenges.exceptions.UnskippableException;
import wand555.github.io.challenges.generated.CompletionConfig;
import wand555.github.io.challenges.generated.ContributorsConfig;
import wand555.github.io.challenges.inventory.progress.CollectedInventory;
import wand555.github.io.challenges.teams.Team;
import wand555.github.io.challenges.types.Data;
import wand555.github.io.challenges.types.Type;

import java.util.Map;

/**
 * Any goal where there may be multiple "mini" goals to complete. For example, this includes potentially many mobs, items, etc.
 * In contrast, something like "collect 10 XP levels" is not a map goal, because it is a singular goal that may be reached.
 *
 * @param <K>
 *         The underlying enum in the data object (BlockBreakData -> Material, MobData -> EntityType, ...)
 * @param <D>
 *         Any data object (BlockBreakData, MobData, ItemData, ...)
 */
public abstract class MapGoal<D extends Data<K>, K extends Keyed> extends BaseGoal implements Triggable<D>, Skippable, Progressable, BossBarDisplay {
    protected final GoalCollector<K> goalCollector;
    protected final GoalMessageHelper<D, K> messageHelper;
    protected final CollectedInventory<D, K> collectedInventory;

    public MapGoal(Context context, boolean complete, GoalCollector<K> goalCollector, GoalMessageHelper<D, K> messageHelper, CollectedInventory<D, K> collectedInventory, Timer timer) {
        super(context, complete, timer);
        this.goalCollector = goalCollector;
        BossBarBuilder bossBarBuilder = new BossBarBuilder();
        if(isFixedOrder()) {
            bossBarBuilder.then(new FixedOrderBossBarPart<>(context, constructGoalInformation(), goalCollector));
        } else {
            bossBarBuilder.then(new TotalCollectablesBossBarPart(context, getNameInResourceBundle(), goalCollector));
        }
        if(hasTimer()) {
            bossBarBuilder.then(new TimerBossBarPart(context, timer));
        }
        this.bossBarHelper = new BossBarHelper(context, bossBarBuilder.getParts());
        this.messageHelper = messageHelper;
        this.collectedInventory = collectedInventory;
    }

    protected abstract BossBarPart.GoalInformation<K> constructGoalInformation();

    @Override
    public boolean determineComplete() {
        return goalCollector.isComplete();
    }

    @Override
    public void onStart(Team team) {
        showBossBar(team.getAllOnlinePlayers());
    }

    @Override
    public void onEnd(Team team) {
        removeBossBar(team.getAllOnlinePlayers());
    }

    @Override
    public void onPlayerJoinTeamInOnGoingChallenge(Player player) {
        showBossBar(player);
    }

    @Override
    public void onPlayerLeaveTeamInOnGoingChallenge(Player player) {
        removeBossBar(player);
    }

    @Override
    public void onComplete(Player lastCompletionStepProvidedBy) {
        super.onComplete(lastCompletionStepProvidedBy);
        messageHelper.sendAllReachedAction();

        /*
        Should be handled when onEnd is called
        if(bossBarHelper.getBossBar() != null) {
            removeBossBar(Team.getTeamPlayerIn(context, lastCompletionStepProvidedBy.getUniqueId()).getAllOnlinePlayers());
        }*/

        notifyGoalCompleted(lastCompletionStepProvidedBy, hasTimer()
                      ? GoalCompletion.TIMER_BEATEN
                      : GoalCompletion.COMPLETED);
    }

    @Override
    public TriggerCheck<D> triggerCheck() {
        return data -> {
            if(!Type.sameTeamCheck(context, this, data.player())) {
                return false;
            }
            if(isComplete()) {
                return false;
            }
            if(hasTimer() && Team.getTeamPlayerIn(context, data.player().getUniqueId()).getCurrentOrder() != getTimer().getOrder()) {
                // the goal has a timer and its order number is bigger than the current order number, meaning
                // this goal is yet "active". It only becomes active if preceding goals are completed and
                // the order number is increases.
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
            } else {
                messageHelper.sendSingleStepAction(data, updatedCollect);
            }

            // update collected inventory:
            // The map of Collect instances are different objects that hold the same information. We have just updated our collect,
            // hence we need to update the same collect there.
            collectedInventory.update(data.mainDataInvolved(), updatedCollect);

            if(determineComplete()) {
                onComplete(data.player());
            }
            bossBarHelper.updateBossBar();
        };
    }

    protected Collect updateCollect(D data) {
        return goalCollector.getToCollect().computeIfPresent(data.mainDataInvolved(),
                                                             (material, collect) -> updateCollect(collect, data)
        );
    }

    protected Collect updateCollect(Collect collect, D data) {
        collect.setCurrentAmount(Math.min(collect.getCurrentAmount() + data.amount(), collect.getAmountNeeded()));

        updateContributorsIn(collect.getCompletionConfig(), data.player(), data.amount());
        if(collect.isComplete()) {
            // set the completion time if the collect is now complete
            collect.getCompletionConfig().setWhenCollectedSeconds((int) context.challengeManager().getTime());
        }
        return collect;
    }

    protected void updateContributorsIn(CompletionConfig completionConfig, Player by, int amount) {
        if(completionConfig == null) {
            completionConfig = new CompletionConfig();
        }
        if(completionConfig.getContributors() == null) {
            completionConfig.setContributors(new ContributorsConfig());
        }
        completionConfig.getContributors().getAdditionalProperties().merge(by.getName(), amount, Integer::sum);
    }

    @Override
    public void onSkip(Player player) throws UnskippableException {
        if(!goalCollector.isFixedOrder()) {
            throw new UnskippableException();
        }
        //goalCollector.getCurrentlyToCollect().getValue().setCurrentAmount(goalCollector.getCurrentlyToCollect().getValue().getAmountNeeded());
        trigger().actOnTriggered(createSkipData(goalCollector.getCurrentlyToCollect(), player));
        if(determineComplete()) {
            // there are no other collects left to complete -> skipping the active collect completes the goal
            onComplete(player);
        } else {
            bossBarHelper.updateBossBar();
        }
    }

    protected final boolean hasSomethingToCollect() {
        return !goalCollector.getToCollect().isEmpty();
    }

    @Override
    public void onProgressStatus(Player player) {
        collectedInventory.show(player);
    }

    protected abstract D createSkipData(Map.Entry<K, Collect> toSkip, Player player);

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
