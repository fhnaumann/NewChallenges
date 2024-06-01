package wand555.github.io.challenges.criteria.goals.factory;

import org.bukkit.Material;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.criteria.goals.itemgoal.ItemGoal;
import wand555.github.io.challenges.criteria.goals.itemgoal.ItemGoalCollectedInventory;
import wand555.github.io.challenges.criteria.goals.itemgoal.ItemGoalMessageHelper;
import wand555.github.io.challenges.generated.ItemGoalConfig;

public class ItemGoalFactory implements GoalFactory<ItemGoalConfig> {
    @Override
    public ItemGoal createGoal(Context context, ItemGoalConfig config) {
        GoalCollector<Material> goalCollector = new GoalCollector<>(context, config.getItems(), Material.class, config.isFixedOrder(), config.isShuffled());
        return new ItemGoal(
                context,
                config,
                goalCollector,
                new ItemGoalMessageHelper(context),
                new ItemGoalCollectedInventory(context, config.getItems(), Material.class),
                config.getGoalTimer() != null ? new Timer(config.getGoalTimer()) : null
        );
    }
}
