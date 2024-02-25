package wand555.github.io.challenges.criteria.goals;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.MessageHelper;
import wand555.github.io.challenges.utils.ActionHelper;
import wand555.github.io.challenges.utils.CollectionUtil;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GoalMessageHelper<T> extends MessageHelper {
    public GoalMessageHelper(Context context) {
        super(context);
    }

    protected abstract String getGoalNameInResourceBundle();

    protected abstract Map<String, Component> additionalPlaceholders(T data);

    public void sendSingleStepAction(T data, Collect collect) {
        Map<String, Component> commonPlaceholder = Map.of(
                "amount", Component.text(collect.getCurrentAmount()),
                "total_amount", Component.text(collect.getAmountNeeded())
        );
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                getGoalBundle(),
                "%s.single.step.message".formatted(getGoalNameInResourceBundle()),
                CollectionUtil.combine(commonPlaceholder, additionalPlaceholders(data))
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                getGoalBundle(),
                "%s.single.step.sound".formatted(getGoalNameInResourceBundle())
        );
    }

    public void sendSingleReachedAction(T data, Collect collect) {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                getGoalBundle(),
                "%s.single.reached.message".formatted(getGoalNameInResourceBundle()),
                additionalPlaceholders(data)
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                getGoalBundle(),
                "%s.single.reached.sound".formatted(getGoalNameInResourceBundle())
        );
    }
    public void sendAllReachedAction() {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "%s.all.reached.message".formatted(getGoalNameInResourceBundle())
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                getGoalBundle(),
                "%s.all.reached.sound".formatted(getGoalNameInResourceBundle())
        );
    }

    protected ResourceBundle getGoalBundle() {
        return context.resourceBundleContext().goalResourceBundle();
    }

}
