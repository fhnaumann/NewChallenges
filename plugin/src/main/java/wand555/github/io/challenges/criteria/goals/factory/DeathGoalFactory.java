package wand555.github.io.challenges.criteria.goals.factory;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.criteria.goals.deathgoal.DeathGoal;
import wand555.github.io.challenges.criteria.goals.deathgoal.DeathGoalCollectedInventory;
import wand555.github.io.challenges.criteria.goals.deathgoal.DeathGoalMessageHelper;
import wand555.github.io.challenges.generated.DeathGoalConfig;
import wand555.github.io.challenges.mapping.DeathMessage;

public class DeathGoalFactory implements GoalFactory<DeathGoalConfig> {
    @Override
    public BaseGoal createGoal(Context context, DeathGoalConfig config) {
        GoalCollector<DeathMessage> goalCollector = new GoalCollector<>(context,
                                                                        config.getDeathMessages(),
                                                                        DeathMessage.class,
                                                                        config.isFixedOrder(),
                                                                        config.isShuffled()
        );
        return new DeathGoal(
                context,
                config,
                goalCollector,
                new DeathGoalMessageHelper(context),
                new DeathGoalCollectedInventory(context, config.getDeathMessages(), DeathMessage.class),
                config.getGoalTimer() != null ? new Timer(config.getGoalTimer()) : null
        );
    }
}
