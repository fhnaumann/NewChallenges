package wand555.github.io.challenges.criteria.goals.blockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.MapGoalBossBarHelper;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.Map;

public class BlockBreakGoalBossBarHelper extends MapGoalBossBarHelper<Material> {

    public BlockBreakGoalBossBarHelper(Context context, GoalCollector<Material> goalCollector) {
        super(context, goalCollector);
    }

    @Override
    protected Map<String, Component> additionalFixedOrderBossBarPlaceholders(Material data) {
        return Map.of(
                "block", ResourcePackHelper.getMaterialUnicodeMapping(data)
        );
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return "blockbreakgoal";
    }
}
