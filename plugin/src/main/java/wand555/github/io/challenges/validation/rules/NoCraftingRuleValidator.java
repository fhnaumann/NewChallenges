package wand555.github.io.challenges.validation.rules;

import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.validation.TypeValidatorHelper;
import wand555.github.io.challenges.validation.types.CraftingTypeValidator;

import java.util.List;

public class NoCraftingRuleValidator extends CraftingTypeValidator {
    public NoCraftingRuleValidator(List<CraftingTypeJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean additionalCodeConstraints(CraftingTypeJSON dataSourceElement) {
        return true;
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return challengesSchema.getRules().getEnabledRules().getNoCrafting().getExemptions();
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return TypeValidatorHelper.rulePath("noCraftingRule/exemptions");
    }
}
