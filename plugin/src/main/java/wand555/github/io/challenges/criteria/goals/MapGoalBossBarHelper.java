package wand555.github.io.challenges.criteria.goals;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;

import java.util.Map;

public abstract class MapGoalBossBarHelper<K extends Keyed> extends BossBarHelper {

    // true: BossBar shows the collectable that is currently to collect (e.g. "2/5 amount for collectable")
    // false: BossBar shows the status of all collectables (e.g. "3/9 collectables completed")

    private final GoalCollector<K> goalCollector;

    public MapGoalBossBarHelper(Context context, GoalCollector<K> goalCollector) {
        super(context);
        this.goalCollector = goalCollector;
        if(goalCollector.isFixedOrder()) {
            this.bossBar = createFixedOrderBossBar(goalCollector.getCurrentlyToCollect());
        }
        else {
            this.bossBar = createTotalCollectablesBossBar(goalCollector.getTotalCollectionStatus());
        }
    }

    private BossBar createFixedOrderBossBar(Map.Entry<K, Collect> data) {
        Component formattedBossBarComponent = formatFixedOrderBossBarComponent(data.getKey(), data.getValue());
        return BossBar.bossBar(formattedBossBarComponent, 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    private BossBar createTotalCollectablesBossBar(GoalCollector.CollectionStatus collectionStatus) {
        Component formattedBossBarComponent = formatTotalCollectablesBossBarComponent(collectionStatus);
        return BossBar.bossBar(formattedBossBarComponent, 1f, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    @Override
    public void updateBossBar() {
        if(goalCollector.isFixedOrder() && goalCollector.hasNext()) {
            bossBar.name(formatFixedOrderBossBarComponent(goalCollector.getCurrentlyToCollect().getKey(), goalCollector.getCurrentlyToCollect().getValue()));
        }
        else {
            bossBar.name(formatTotalCollectablesBossBarComponent(goalCollector.getTotalCollectionStatus()));
        }
    }

    private Component formatFixedOrderBossBarComponent(K data, Collect collect) {
        return ComponentUtil.formatBossBarMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "%s.bossbar.fixedOrder.message".formatted(getGoalNameInResourceBundle()),
                Map.of(
                        "amount", Component.text(collect.getCurrentAmount()),
                        "total_amount", Component.text(collect.getAmountNeeded())
                ),
                additionalFixedOrderBossBarPlaceholders(data)
        );
    }

    private Component formatTotalCollectablesBossBarComponent(GoalCollector.CollectionStatus collectionStatus) {
        return ComponentUtil.formatBossBarMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "%s.bossbar.totalCollectables.message".formatted(getGoalNameInResourceBundle()),
                Map.of(
                        "amount", Component.text(collectionStatus.collectablesStillToComplete()),
                        "total_amount", Component.text(collectionStatus.allCollectables())
                ),
                Map.of()
        );
    }

    protected abstract Map<String, Component> additionalFixedOrderBossBarPlaceholders(K data);

    protected abstract String getGoalNameInResourceBundle();
}
