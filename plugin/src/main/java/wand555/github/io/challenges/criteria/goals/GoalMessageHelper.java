package wand555.github.io.challenges.criteria.goals;

import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.MessageHelper;
import wand555.github.io.challenges.types.Data;
import wand555.github.io.challenges.utils.ActionHelper;
import wand555.github.io.challenges.utils.CollectionUtil;

import java.util.Map;
import java.util.ResourceBundle;

/**
 * @param <D>
 *         Any data object (BlockBreakData, MobData, ItemData, ...)
 * @param <K>
 *         The underlying enum in the data object (BlockBreakData -> Material, MobData -> EntityType, ...)
 */
public abstract class GoalMessageHelper<D extends Data<K>, K extends Keyed> extends MessageHelper {
    public GoalMessageHelper(Context context) {
        super(context);
    }

    protected abstract String getGoalNameInResourceBundle();

    public abstract Map<String, Component> additionalBossBarPlaceholders(K data);

    protected abstract Map<String, Component> additionalStepPlaceholders(D data);

    public void sendSingleStepAction(D data, Collect collect) {
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

    public void sendSingleReachedAction(D data, Collect collect) {
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
