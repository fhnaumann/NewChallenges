package wand555.github.io.challenges.validation.rules;

import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.mapping.EntityTypeJSON;
import wand555.github.io.challenges.validation.TypeValidatorHelper;
import wand555.github.io.challenges.validation.types.MobTypeValidator;

import java.util.List;

public class NoMobKillRuleValidator extends MobTypeValidator {
    public NoMobKillRuleValidator(List<EntityTypeJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return challengesSchema.getRules().getEnabledRules().getNoMobKill().getExemptions();
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return TypeValidatorHelper.rulePath("noMobKillRule/exemptions");
    }
}
