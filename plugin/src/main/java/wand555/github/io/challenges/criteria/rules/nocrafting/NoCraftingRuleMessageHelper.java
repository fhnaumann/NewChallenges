package wand555.github.io.challenges.criteria.rules.nocrafting;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.rules.RuleMessageHelper;
import wand555.github.io.challenges.types.crafting.CraftingData;

import java.util.Map;

public class NoCraftingRuleMessageHelper extends RuleMessageHelper<CraftingData> {
    public NoCraftingRuleMessageHelper(Context context) {
        super(context);
    }

    @Override
    public Map<String, Component> additionalViolationPlaceholders(CraftingData data) {
        return null;
    }

    @Override
    protected String getRuleNameInResourceBundle() {
        return null;
    }
}
