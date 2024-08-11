package wand555.github.io.challenges.validation.goals;

import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.files.ProgressListener;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.validation.ModelValidator;
import wand555.github.io.challenges.validation.ValidationResult;

import java.util.ArrayList;
import java.util.List;

public class GoalsValidator extends ModelValidator {

    private final DataSourceContext dataSourceContext;

    public GoalsValidator(DataSourceContext dataSourceContext) {
        this.dataSourceContext = dataSourceContext;
    }

    @Override
    public ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, Model challengesSchema, ProgressListener progressListener) {
        // TODO: go through each and every goal and call the appropriate validator (if validation is necessary)
        if(challengesSchema.getGoals() == null && challengesSchema.getTeams() == null) {
            return builder;
        }
        if(challengesSchema.getGoals() != null) {
            validateAllGoalsIn(challengesSchema.getGoals(), -1, challengesSchema, builder, progressListener);
        }
        if(challengesSchema.getTeams() != null) {
            for(int i = 0; i < challengesSchema.getTeams().size(); i++) {
                validateAllGoalsIn(challengesSchema.getTeams().get(i).getGoals(),
                                   i,
                                   challengesSchema,
                                   builder,
                                   progressListener
                );
            }
        }
        return builder;
    }

    private ValidationResult.ValidationResultBuilder validateAllGoalsIn(GoalsConfig goalsConfig, int teamIdx, Model challengesSchema, ValidationResult.ValidationResultBuilder builder, ProgressListener progressListener) {
        List<ModelValidator> goalValidators = new ArrayList<>();

        if(goalsConfig.getItemGoal() != null) {
            goalValidators.add(new ItemGoalValidator(dataSourceContext.materialJSONList(), teamIdx));
        }
        if(goalsConfig.getBlockBreakGoal() != null) {
            goalValidators.add(new BlockBreakGoalValidator(dataSourceContext.materialJSONList(), teamIdx));
        }
        if(goalsConfig.getBlockPlaceGoal() != null) {
            goalValidators.add(new BlockPlaceGoalValidator(dataSourceContext.materialJSONList(), teamIdx));
        }
        if(goalsConfig.getMobGoal() != null) {
            goalValidators.add(new MobGoalValidator(dataSourceContext.entityTypeJSONList(), teamIdx));
        }
        if(goalsConfig.getDeathGoal() != null) {
            goalValidators.add(new DeathGoalValidator(dataSourceContext.deathMessageList(), teamIdx));
        }
        if(goalsConfig.getCraftingGoal() != null) {
            goalValidators.add(new CraftingGoalValidator(dataSourceContext.craftingTypeJSONList(), teamIdx));
        }
        for(int i = 0; i < goalValidators.size(); i++) {
            ModelValidator goalValidator = goalValidators.get(i);
            goalValidator.performValidation(builder, challengesSchema, progressListener);
            // goal validators make up 40% of loading (arbitrarily defined, just a guess)
            double teamProgressFactor = (double) teamIdx / challengesSchema.getTeams().size();
            progressListener.onProgress(0.5 + (double) i / goalValidators.size() * teamProgressFactor * 0.4);
        }

        return builder;
    }

}
