package wand555.github.io.challenges.criteria.goals.deathgoal;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.types.death.DeathData;
import wand555.github.io.challenges.mapping.DeathMessage;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.Map;

public class DeathGoalMessageHelper extends GoalMessageHelper<DeathData, DeathMessage> {
    public DeathGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return DeathGoal.NAME_IN_RB;
    }

    @Override
    public Map<String, Component> additionalBossBarPlaceholders(DeathMessage data) {
        return Map.of(
                "death_type", ResourcePackHelper.getDeathMessageUnicodeMapping(data.toEnum())
        );
    }

    @Override
    protected Map<String, Component> additionalStepPlaceholders(DeathData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "death_type", Component.translatable(data.deathMessage().getMessageWithDummyData())
        );
    }
}
