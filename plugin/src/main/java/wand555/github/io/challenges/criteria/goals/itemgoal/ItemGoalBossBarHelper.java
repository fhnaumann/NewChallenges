package wand555.github.io.challenges.criteria.goals.itemgoal;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.MapGoalBossBarHelper;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.Map;

public class ItemGoalBossBarHelper extends MapGoalBossBarHelper<Material> {
    public ItemGoalBossBarHelper(Context context, GoalCollector<Material> goalCollector) {
        super(context, goalCollector);
    }

    @Override
    protected Map<String, Component> additionalFixedOrderBossBarPlaceholders(Material data) {
        return Map.of(
                "item", ResourcePackHelper.getMaterialUnicodeMapping(data)
        );
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return "itemgoal";
    }
}
