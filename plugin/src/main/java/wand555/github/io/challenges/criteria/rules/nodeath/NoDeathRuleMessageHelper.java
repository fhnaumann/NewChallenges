package wand555.github.io.challenges.criteria.rules.nodeath;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.rules.RuleMessageHelper;
import wand555.github.io.challenges.types.death.DeathData;

import java.util.Map;

public class NoDeathRuleMessageHelper extends RuleMessageHelper<DeathData> {
    public NoDeathRuleMessageHelper(Context context) {
        super(context);
    }

    @Override
    public Map<String, Component> additionalViolationPlaceholders(DeathData data) {
        return Map.of(
                "player", Component.text(data.player().getName())
        );
    }

    @Override
    protected String getRuleNameInResourceBundle() {
        return "nodeath";
    }
}
