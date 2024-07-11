package wand555.github.io.challenges.criteria.goals.deathgoal;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.types.death.DeathData;
import wand555.github.io.challenges.types.death.DeathMessage;

import java.util.Map;

public class DeathGoalMessageHelper extends GoalMessageHelper<DeathData, DeathMessage> {
    public DeathGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return null;
    }

    @Override
    protected Map<String, Component> additionalBossBarPlaceholders(DeathMessage data) {
        return null;
    }

    @Override
    protected Map<String, Component> additionalStepPlaceholders(DeathData data) {
        return null;
    }
}
