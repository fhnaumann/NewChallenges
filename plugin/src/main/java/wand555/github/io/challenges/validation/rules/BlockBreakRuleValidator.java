package wand555.github.io.challenges.validation.rules;

import wand555.github.io.challenges.generated.ChallengesSchema;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.validation.TypeValidatorHelper;
import wand555.github.io.challenges.validation.types.BlockBreakTypeValidator;

import java.util.List;

public class BlockBreakRuleValidator extends BlockBreakTypeValidator {
    public BlockBreakRuleValidator(List<MaterialJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected List<String> getCodes(ChallengesSchema challengesSchema) {
        return challengesSchema.getRules().getEnabledRules().getNoBlockBreak().getExemptions();
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return TypeValidatorHelper.rulePath("noBlockBreakRule/exemptions");
    }
}
