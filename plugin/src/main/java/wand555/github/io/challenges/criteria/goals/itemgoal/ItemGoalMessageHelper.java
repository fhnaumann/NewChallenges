package wand555.github.io.challenges.criteria.goals.itemgoal;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.types.item.ItemData;
import wand555.github.io.challenges.utils.ActionHelper;

import java.util.Map;

public class ItemGoalMessageHelper extends GoalMessageHelper<ItemData> {
    public ItemGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    public void sendSingleStepAction(ItemData data, Collect collect) {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "itemgoal.single.step.message",
                Map.of(
                        "player", Component.text(data.player().getName()),
                        "item", ComponentUtil.translate(data.itemStackInteractedWith().getType()),
                        "amount", Component.text(collect.getCurrentAmount()),
                        "total_amount", Component.text(collect.getAmountNeeded())
                )
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                context.resourceBundleContext().goalResourceBundle(),
                "itemgoal.single.step.sound"
        );
    }

    @Override
    public void sendSingleReachedAction(ItemData data, Collect collect) {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "itemgoal.single.reached.message",
                Map.of(
                        "item", ComponentUtil.translate(data.itemStackInteractedWith().getType())
                )
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                context.resourceBundleContext().goalResourceBundle(),
                "itemgoal.single.reached.sound"
        );
    }

    @Override
    public void sendAllReachedAction() {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "itemgoal.all.reached.message"
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                context.resourceBundleContext().goalResourceBundle(),
                "itemgoal.all.reached.sound"
        );
    }
}
