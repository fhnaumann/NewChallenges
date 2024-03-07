package wand555.github.io.challenges.criteria.rules.nomobkill;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.rules.RuleMessageHelper;
import wand555.github.io.challenges.types.mob.MobData;

import java.util.Map;

public class NoMobKillRuleMessageHelper extends RuleMessageHelper<MobData> {
    public NoMobKillRuleMessageHelper(Context context) {
        super(context);
    }

    @Override
    public Map<String, Component> additionalViolationPlaceholders(MobData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "entity", Component.translatable(data.entityInteractedWith())
        );
    }

    @Override
    protected String getRuleNameInResourceBundle() {
        return "nomobkill";
    }
}
