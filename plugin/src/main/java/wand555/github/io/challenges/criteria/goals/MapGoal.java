package wand555.github.io.challenges.criteria.goals;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Any goal where there may be multiple "mini" goals to complete. For example, this includes potentially many mobs, items, etc.
 * In constrast, something like "collect 10 XP levels" is not a map goal, because it is a singular goal that may be reached.
 * @param <T>
 * @param <S>
 */
public abstract class MapGoal<T extends Enum<T>, S> extends BaseGoal implements Triggable<S>, BossBarDisplay<Map.Entry<T, Collect>> {

    protected final GoalCollector<T> goalCollector;
    protected final GoalMessageHelper<S,T> messageHelper;
    protected final boolean fixedOrder;
    private final BossBar bossBar;

    public MapGoal(Context context, boolean complete, boolean fixedOrder, boolean shuffled, List<CollectableEntryConfig> collectables, Class<T> enumType, GoalMessageHelper<S,T> messageHelper) {
        super(context, complete);
        if(fixedOrder && !shuffled) {
            Collections.shuffle(collectables);
        }
        this.goalCollector = new GoalCollector<>(context, collectables, enumType);
        this.messageHelper = messageHelper;
        this.fixedOrder = fixedOrder;
        if(fixedOrder) {
            this.bossBar = createBossBar(goalCollector.getCurrentlyToCollect());
        }
        else {
            this.bossBar = null;
        }
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

        removeBossBar(context.plugin().getServer().getOnlinePlayers());

        notifyManager();
    }

    protected abstract Function<S, T> data2MainElement();

    @Override
    public TriggerCheck<S> triggerCheck() {
        return data -> {
            if(isComplete()) {
                return false;
            }
            Collect toCollect = goalCollector.getToCollect().get(data2MainElement().apply(data));
            if(toCollect == null || toCollect.isComplete()) {
                return false;
            }
            if(fixedOrder) {
                return goalCollector.getCurrentlyToCollect().getKey() == data2MainElement().apply(data);
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
                if(goalCollector.hasNext()) {
                    updateBossBar(goalCollector.next());
                }
            }
            else {
                messageHelper.sendSingleStepAction(data, updatedCollect);
                updateBossBar(goalCollector.getCurrentlyToCollect());
            }

            if(determineComplete()) {
                onComplete();
            }
        };
    }

    protected Collect updateCollect(S data) {
        return goalCollector.getToCollect().computeIfPresent(data2MainElement().apply(data), (material, collect) -> {
            // default behaviour is to add exactly 1 to the collect
            // subclasses may override this behaviour
            collect.setCurrentAmount(collect.getCurrentAmount()+1);
            return collect;
        });
    }

    @Override
    public BossBar createBossBar(Map.Entry<T, Collect> data) {
        if(fixedOrder) {
            Component formattedBossBarComponent = messageHelper.formatBossBarComponent(data.getKey(), data.getValue());
            return BossBar.bossBar(formattedBossBarComponent, 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        }
        throw new RuntimeException("Attempted boss bar creation while fixedOrder is false.");
    }

    @Override
    public void updateBossBar(Map.Entry<T, Collect> data) {
        if(!fixedOrder) {
            throw new RuntimeException("Attempted boss bar creation while fixedOrder is false.");
        }
        bossBar.name(messageHelper.formatBossBarComponent(data.getKey(), data.getValue()));
    }

    @Override
    public BossBar getBossBar() {
        return bossBar;
    }

    @Override
    public BossBarPriority getBossBarPriority() {
        return fixedOrder ? BossBarPriority.URGENT : BossBarPriority.INFO;
    }
}
