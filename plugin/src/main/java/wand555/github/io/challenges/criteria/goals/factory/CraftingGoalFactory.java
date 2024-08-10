package wand555.github.io.challenges.criteria.goals.factory;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.criteria.goals.craftingoal.CraftingGoal;
import wand555.github.io.challenges.criteria.goals.craftingoal.CraftingGoalCollectedInventory;
import wand555.github.io.challenges.criteria.goals.craftingoal.CraftingGoalMessageHelper;
import wand555.github.io.challenges.generated.CraftingGoalConfig;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;

public class CraftingGoalFactory implements GoalFactory<CraftingGoalConfig> {
    @Override
    public BaseGoal createGoal(Context context, CraftingGoalConfig config) {
        GoalCollector<CraftingTypeJSON> goalCollector = new GoalCollector<>(
                context,
                config.getCrafted(),
                CraftingTypeJSON.class,
                config.isFixedOrder(),
                config.isShuffled()
        );
        return new CraftingGoal(
                context,
                config,
                goalCollector,
                new CraftingGoalMessageHelper(context),
                new CraftingGoalCollectedInventory(context, config.getCrafted(), CraftingTypeJSON.class),
                config.getGoalTimer() != null ? new Timer(config.getGoalTimer()) : null
        );
    }
}
