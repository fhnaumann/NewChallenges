package wand555.github.io.challenges.criteria.goals.factory;

import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoal;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoalCollectedInventory;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoalMessageHelper;
import wand555.github.io.challenges.generated.MobGoalConfig;

public class MobGoalFactory implements GoalFactory<MobGoalConfig> {
    @Override
    public MobGoal createGoal(Context context, MobGoalConfig config) {
        GoalCollector<EntityType> goalCollector = new GoalCollector<>(context,
                                                                      config.getMobs(),
                                                                      EntityType.class,
                                                                      config.isFixedOrder(),
                                                                      config.isShuffled()
        );
        return new MobGoal(
                context,
                config,
                goalCollector,
                new MobGoalMessageHelper(context),
                new MobGoalCollectedInventory(context, config.getMobs(), EntityType.class),
                config.getGoalTimer() != null ? new Timer(config.getGoalTimer()) : null
        );
    }
}
