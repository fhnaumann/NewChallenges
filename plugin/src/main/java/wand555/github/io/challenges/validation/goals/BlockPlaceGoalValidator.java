package wand555.github.io.challenges.validation.goals;

import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.validation.types.BlockPlaceTypeValidator;

import java.util.List;

public class BlockPlaceGoalValidator extends BlockPlaceTypeValidator {

    public BlockPlaceGoalValidator(List<MaterialJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return ModelMapper.collectables2Codes(challengesSchema.getGoals().getBlockPlaceGoal().getPlaced());
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return "goals/blockPlaceGoal/placed";
    }
}
