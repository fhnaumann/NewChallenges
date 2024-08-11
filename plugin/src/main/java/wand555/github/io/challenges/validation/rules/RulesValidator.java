package wand555.github.io.challenges.validation.rules;

import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.files.ProgressListener;
import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.validation.ModelValidator;
import wand555.github.io.challenges.validation.ValidationResult;

import java.util.ArrayList;
import java.util.List;

public class RulesValidator extends ModelValidator {

    private final DataSourceContext dataSourceContext;

    public RulesValidator(DataSourceContext dataSourceContext) {
        this.dataSourceContext = dataSourceContext;
    }

    @Override
    public ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, Model challengesSchema, ProgressListener progressListener) {
        // TODO: go through each and every rule and call the appropriate validator (if validation is necessary)
        if(challengesSchema.getRules() == null || challengesSchema.getRules().getEnabledRules() == null) {
            return builder;
        }

        List<ModelValidator> rulesValidators = new ArrayList<>();

        EnabledRules enabledRules = challengesSchema.getRules().getEnabledRules();
        if(enabledRules.getNoItem() != null) {
            rulesValidators.add(new NoItemRuleValidator(dataSourceContext.materialJSONList()));
        }
        if(enabledRules.getNoBlockBreak() != null) {
            rulesValidators.add(new BlockBreakRuleValidator(dataSourceContext.materialJSONList()));
        }
        if(enabledRules.getNoMobKill() != null) {
            rulesValidators.add(new NoMobKillRuleValidator(dataSourceContext.entityTypeJSONList()));
        }
        if(enabledRules.getNoCrafting() != null) {
            rulesValidators.add(new NoCraftingRuleValidator(dataSourceContext.craftingTypeJSONList()));
        }

        for(int i=0; i<rulesValidators.size(); i++) {
            ModelValidator ruleValidator = rulesValidators.get(i);
            ruleValidator.performValidation(builder, challengesSchema, progressListener);
            // rule validators make up 40% of loading (arbitrarily defined, just a guess)
            progressListener.onProgress(0.1 + (double) i/rulesValidators.size() * 0.4);
        }
        return builder;
    }
}
