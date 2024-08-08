package wand555.github.io.challenges.validation.rules;

import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.validation.ModelValidator;
import wand555.github.io.challenges.validation.ValidationResult;

public class RulesValidator extends ModelValidator {

    private final DataSourceContext dataSourceContext;

    public RulesValidator(DataSourceContext dataSourceContext) {
        this.dataSourceContext = dataSourceContext;
    }

    @Override
    protected ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, Model challengesSchema) {
        // TODO: go through each and every rule and call the appropriate validator (if validation is necessary)
        if(challengesSchema.getRules() == null || challengesSchema.getRules().getEnabledRules() == null) {
            return builder;
        }
        EnabledRules enabledRules = challengesSchema.getRules().getEnabledRules();
        if(enabledRules.getNoItem() != null) {
            new NoItemRuleValidator(dataSourceContext.materialJSONList()).performValidation(builder, challengesSchema);
        }
        if(enabledRules.getNoBlockBreak() != null) {
            new BlockBreakRuleValidator(dataSourceContext.materialJSONList()).performValidation(builder,
                                                                                                challengesSchema
            );
        }
        if(enabledRules.getNoMobKill() != null) {
            new NoMobKillRuleValidator(dataSourceContext.entityTypeJSONList()).performValidation(builder,
                                                                                                 challengesSchema
            );
        }
        if(enabledRules.getNoCrafting() != null) {
            new NoCraftingRuleValidator(dataSourceContext.craftingTypeJSONList()).performValidation(builder, challengesSchema);
        }
        return builder;
    }
}
