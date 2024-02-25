package wand555.github.io.challenges.criteria.goals.mobgoal;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.types.mob.MobData;
import wand555.github.io.challenges.utils.ActionHelper;

import java.util.Map;

public class MobGoalMessageHelper extends GoalMessageHelper<MobData> {
    public MobGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return "mobgoal";
    }

    @Override
    protected Map<String, Component> additionalPlaceholders(MobData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "entity", ComponentUtil.translate(data.entityInteractedWith())
        );
    }
}
