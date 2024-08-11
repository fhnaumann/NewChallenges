package wand555.github.io.challenges.validation.goals;

import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.validation.TypeValidatorHelper;
import wand555.github.io.challenges.validation.types.BlockPlaceTypeValidator;

import java.util.List;

public class BlockPlaceGoalValidator extends BlockPlaceTypeValidator {

    private final int teamIdx;

    public BlockPlaceGoalValidator(List<MaterialJSON> dataSource, int teamIdx) {
        super(dataSource);
        this.teamIdx = teamIdx;
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return TypeValidatorHelper.codes(challengesSchema, teamIdx, goalsConfig -> goalsConfig.getBlockPlaceGoal().getPlaced());
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return TypeValidatorHelper.goalPath("blockPlaceGoal/placed", teamIdx);
    }
}
