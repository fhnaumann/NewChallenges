package wand555.github.io.challenges.criteria.goals.bossbar;

import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalCollector;

import java.util.Map;

public class FixedOrderBossBarPart<K extends Keyed> extends BossBarPart<K> {

    private final GoalCollector<K> goalCollector;

    public FixedOrderBossBarPart(Context context, GoalInformation<K> goalInformation, GoalCollector<K> goalCollector) {
        super(context, goalInformation);
        this.goalCollector = goalCollector;
    }

    @Override
    public Component buildPart() {
        K data = goalCollector.getCurrentlyToCollect().getKey();
        Collect collect = goalCollector.getCurrentlyToCollect().getValue();
        return ComponentUtil.formatBossBarMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "%s.bossbar.fixedOrder.message".formatted(goalInformation.goalNameInResourceBundle()),
                Map.of(
                        "amount", Component.text(collect.getCurrentAmount()),
                        "total_amount", Component.text(collect.getAmountNeeded())
                ),
                goalInformation.additionalPlaceholderInformation().additionalPlaceholders(data)
        );
    }
}
