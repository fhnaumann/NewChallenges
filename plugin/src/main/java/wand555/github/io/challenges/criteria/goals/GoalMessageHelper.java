package wand555.github.io.challenges.criteria.goals;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.MessageHelper;
import wand555.github.io.challenges.utils.ActionHelper;
import wand555.github.io.challenges.utils.CollectionUtil;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 *
 * @param <T> Any data object (BlockBreakData, MobData, ItemData, ...)
 * @param <E> The underlying enum in the data object (BlockBreakData -> Material, MobData -> EntityType, ...)
 */
public abstract class GoalMessageHelper<T,E extends Enum<E>> extends MessageHelper {
    public GoalMessageHelper(Context context) {
        super(context);
    }

    public Component formatBossBarComponent(E data, Collect collect) {
        return ComponentUtil.formatBossBarMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "%s.bossbar.message".formatted(getGoalNameInResourceBundle()),
                Map.of(
                        "amount", Component.text(collect.getCurrentAmount()),
                        "total_amount", Component.text(collect.getAmountNeeded())
                ),
                additionalBossBarPlaceholders(data)
        );
    }

    protected abstract String getGoalNameInResourceBundle();

    protected abstract Map<String, Component> additionalBossBarPlaceholders(E data);

    protected abstract Map<String, Component> additionalStepPlaceholders(T data);

    public void sendSingleStepAction(T data, Collect collect) {
        Map<String, Component> commonPlaceholder = Map.of(
                "amount", Component.text(collect.getCurrentAmount()),
                "total_amount", Component.text(collect.getAmountNeeded())
        );
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                getGoalBundle(),
                "%s.single.step.message".formatted(getGoalNameInResourceBundle()),
                CollectionUtil.combine(commonPlaceholder, additionalStepPlaceholders(data))
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
                additionalStepPlaceholders(data)
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
