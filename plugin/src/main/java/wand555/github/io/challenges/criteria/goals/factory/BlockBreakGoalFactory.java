package wand555.github.io.challenges.criteria.goals.factory;

import org.bukkit.Material;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Goal;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakCollectedInventory;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoal;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoalBossBarHelper;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoalMessageHelper;
import wand555.github.io.challenges.generated.BlockBreakGoalConfig;

public class BlockBreakGoalFactory implements GoalFactory<BlockBreakGoalConfig> {

    @Override
    public BlockBreakGoal createGoal(Context context, BlockBreakGoalConfig config) {
        GoalCollector<Material> goalCollector = new GoalCollector<>(context, config.getBroken(), Material.class, config.isFixedOrder(), config.isShuffled());
        return new BlockBreakGoal(
                context,
                config,
                goalCollector,
                new BlockBreakGoalMessageHelper(context),
                new BlockBreakGoalBossBarHelper(context, goalCollector),
                new BlockBreakCollectedInventory(context, config.getBroken(), Material.class)
                );
    }
}
