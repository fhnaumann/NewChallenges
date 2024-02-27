package wand555.github.io.challenges.criteria.goals.mobgoal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.types.mob.MobData;
import wand555.github.io.challenges.utils.ActionHelper;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.Map;

public class MobGoalMessageHelper extends GoalMessageHelper<MobData, EntityType> {
    public MobGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return "mobgoal";
    }

    @Override
    protected Map<String, Component> additionalBossBarPlaceholders(EntityType data) {
        return Map.of(
                "entity", ResourcePackHelper.getEntityTypeUnicodeMapping(data)
        );
    }

    @Override
    protected Map<String, Component> additionalStepPlaceholders(MobData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "entity", ComponentUtil.translate(data.entityInteractedWith())
        );
    }
}
