package wand555.github.io.challenges.criteria.goals.factory;

import org.bukkit.Material;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Goal;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.criteria.goals.blockplace.BlockPlaceGoal;
import wand555.github.io.challenges.criteria.goals.blockplace.BlockPlaceGoalCollectedInventory;
import wand555.github.io.challenges.criteria.goals.blockplace.BlockPlaceGoalMessageHelper;
import wand555.github.io.challenges.generated.BlockPlaceGoalConfig;

public class BlockPlaceGoalFactory implements GoalFactory<BlockPlaceGoalConfig> {

    @Override
    public BaseGoal createGoal(Context context, BlockPlaceGoalConfig config) {
        GoalCollector<Material> goalCollector = new GoalCollector<>(context,
                                                                    config.getPlaced(),
                                                                    Material.class,
                                                                    config.isFixedOrder(),
                                                                    config.isShuffled()
        );
        return new BlockPlaceGoal(
                context,
                config,
                goalCollector,
                new BlockPlaceGoalMessageHelper(context),
                new BlockPlaceGoalCollectedInventory(context, config.getPlaced(), Material.class),
                config.getGoalTimer() != null ? new Timer(config.getGoalTimer()) : null
        );
    }
}
