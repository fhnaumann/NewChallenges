package wand555.github.io.challenges.criteria.goals.bossbar;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;

import java.util.Map;

public class GoalNameBossBarPart extends BossBarPart<Object> {
    public GoalNameBossBarPart(Context context, String goalNameInResourceBundle) {
        super(context, new GoalInformation<>(goalNameInResourceBundle, new NoAdditionalPlaceholderInformation()));
    }

    @Override
    public Component buildPart() {
        return Component.text("(").append(ComponentUtil.formatBossBarMessage(context.plugin(), context.resourceBundleContext().goalResourceBundle(), "%s.name".formatted(goalInformation.goalNameInResourceBundle()), Map.of(), Map.of())).append(Component.text(")"));
    }
}
