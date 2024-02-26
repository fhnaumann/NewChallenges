package wand555.github.io.challenges.validation;

import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.generated.ChallengesSchema;

public class GoalsValidator extends ModelValidator {

    private final DataSourceContext dataSourceContext;

    public GoalsValidator(DataSourceContext dataSourceContext) {
        this.dataSourceContext = dataSourceContext;
    }

    @Override
    protected ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, ChallengesSchema challengesSchema) {
        // TODO: go through each and every goal and call the appropriate validator (if validation is necessary)
        if(challengesSchema.getGoals() == null) {
            return builder;
        }
        if(challengesSchema.getGoals().getItemGoal() != null) {
            new ItemGoalValidator(dataSourceContext.materialJSONList()).performValidation(builder, challengesSchema);
        }
        if(challengesSchema.getGoals().getBlockbreakGoal() != null) {
            new BlockBreakValidator(dataSourceContext.materialJSONList()).performValidation(builder, challengesSchema);
        }
        if(challengesSchema.getGoals().getMobGoal() != null) {
            new MobGoalValidator(dataSourceContext.entityTypeJSONList()).performValidation(builder, challengesSchema);
        }
        return builder;
    }

}
