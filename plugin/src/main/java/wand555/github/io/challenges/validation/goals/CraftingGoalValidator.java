package wand555.github.io.challenges.validation.goals;

import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.validation.TypeValidatorHelper;
import wand555.github.io.challenges.validation.types.CraftingTypeValidator;

import java.util.List;

public class CraftingGoalValidator extends CraftingTypeValidator {

    private final int teamIdx;

    public CraftingGoalValidator(List<CraftingTypeJSON> dataSource, int teamIdx) {
        super(dataSource);
        this.teamIdx = teamIdx;
    }

    @Override
    protected boolean additionalCodeConstraints(CraftingTypeJSON dataSourceElement) {
        return true;
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return TypeValidatorHelper.codes(challengesSchema, teamIdx, goalsConfig -> goalsConfig.getCraftingGoal().getCrafted());
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return TypeValidatorHelper.goalPath("craftingGoal/crafted", teamIdx);
    }
}
