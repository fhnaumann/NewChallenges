package wand555.github.io.challenges.validation.rules;

import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.validation.TypeValidatorHelper;
import wand555.github.io.challenges.validation.types.ItemTypeValidator;

import java.util.List;

public class NoItemRuleValidator extends ItemTypeValidator {
    public NoItemRuleValidator(List<MaterialJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return challengesSchema.getRules().getEnabledRules().getNoItem().getExemptions();
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return TypeValidatorHelper.rulePath("noItemRule/exemptions");
    }
}
