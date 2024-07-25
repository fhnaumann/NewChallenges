package wand555.github.io.challenges.criteria.rules.noblockplace;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.rules.RuleMessageHelper;
import wand555.github.io.challenges.types.blockplace.BlockPlaceData;

import java.util.Map;

public class NoBlockPlaceRuleMessageHelper extends RuleMessageHelper<BlockPlaceData> {
    public NoBlockPlaceRuleMessageHelper(Context context) {
        super(context);
    }

    @Override
    public Map<String, Component> additionalViolationPlaceholders(BlockPlaceData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "block", Component.translatable(data.placed())
        );
    }

    @Override
    protected String getRuleNameInResourceBundle() {
        return "noblockplace";
    }
}
