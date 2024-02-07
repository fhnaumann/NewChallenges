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
    public void sendSingleStepAction(MobData data, Collect collect) {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "mobgoal.single.step.message",
                Map.of(
                        "player", Component.text(data.player().getName()),
                        "entity", ComponentUtil.translate(data.entityInteractedWith()),
                        "amount", Component.text(collect.getCurrentAmount()),
                        "total_amount", Component.text(collect.getAmountNeeded())
                )
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                context.resourceBundleContext().goalResourceBundle(),
                "mobgoal.single.step.sound"
        );
    }

    @Override
    public void sendSingleReachedAction(MobData data, Collect collect) {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "mobgoal.single.reached.message",
                Map.of(
                        "entity", ComponentUtil.translate(data.entityInteractedWith())
                )
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                context.resourceBundleContext().goalResourceBundle(),
                "mobgoal.single.reached.sound"
        );
    }

    @Override
    public void sendAllReachedAction() {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "mobgoal.all.reached.message"
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                context.resourceBundleContext().goalResourceBundle(),
                "mobgoal.all.reached.sound"
        );
    }
}
