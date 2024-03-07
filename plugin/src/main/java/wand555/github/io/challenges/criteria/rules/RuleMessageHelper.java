package wand555.github.io.challenges.criteria.rules;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.MessageHelper;

import java.util.Map;
import java.util.ResourceBundle;

/**
 *
 * @param <T> Any data object (BlockBreakData, MobData, ItemData, ...)
 */
public abstract class RuleMessageHelper<T> extends MessageHelper {
    public RuleMessageHelper(Context context) {
        super(context);
    }

    public void sendViolationAction(T data) {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                getRuleBundle(),
                "%s.violation".formatted(getRuleNameInResourceBundle()),
                additionalViolationPlaceholders(data)
        );
        context.plugin().getServer().broadcast(toSend);
    }

    public abstract Map<String, Component> additionalViolationPlaceholders(T data);

    protected abstract String getRuleNameInResourceBundle();

    protected ResourceBundle getRuleBundle() {
        return context.resourceBundleContext().ruleResourceBundle();
    }
}
