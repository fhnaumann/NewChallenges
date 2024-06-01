package wand555.github.io.challenges.criteria.goals.bossbar;

import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.GoalCollector;

import java.util.Map;

public class TotalCollectablesBossBarPart extends BossBarPart<Object> {

    private final GoalCollector<?> goalCollector;

    public TotalCollectablesBossBarPart(Context context, String goalNameInResourceBundle, GoalCollector<?> goalCollector) {
        super(context, new GoalInformation<>(goalNameInResourceBundle, new NoAdditionalPlaceholderInformation()));
        this.goalCollector = goalCollector;
    }

    @Override
    public Component buildPart() {
        GoalCollector.CollectionStatus collectionStatus = goalCollector.getTotalCollectionStatus();
        return ComponentUtil.formatBossBarMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "%s.bossbar.totalCollectables.message".formatted(goalInformation.goalNameInResourceBundle()),
                Map.of(
                        "amount", Component.text(collectionStatus.collectablesStillToComplete()),
                        "total_amount", Component.text(collectionStatus.allCollectables())
                ),
                Map.of()
        );
    }
}
