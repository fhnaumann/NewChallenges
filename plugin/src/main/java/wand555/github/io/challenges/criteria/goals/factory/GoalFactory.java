package wand555.github.io.challenges.criteria.goals.factory;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Goal;

public interface GoalFactory<T> {

    Goal createGoal(Context context, T config);
}
