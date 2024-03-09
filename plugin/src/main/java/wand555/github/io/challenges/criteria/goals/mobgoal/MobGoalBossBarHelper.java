package wand555.github.io.challenges.criteria.goals.mobgoal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.MapGoalBossBarHelper;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.Map;

public class MobGoalBossBarHelper extends MapGoalBossBarHelper<EntityType> {
    public MobGoalBossBarHelper(Context context, GoalCollector<EntityType> goalCollector) {
        super(context, goalCollector);
    }

    @Override
    protected Map<String, Component> additionalFixedOrderBossBarPlaceholders(EntityType data) {
        return Map.of(
                "entity", ResourcePackHelper.getEntityTypeUnicodeMapping(data)
        );
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return "mobgoal";
    }
}
