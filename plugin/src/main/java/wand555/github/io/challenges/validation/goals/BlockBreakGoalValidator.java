package wand555.github.io.challenges.validation.goals;

import org.bukkit.Material;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.validation.CodeableValidator;
import wand555.github.io.challenges.validation.TypeValidatorHelper;
import wand555.github.io.challenges.validation.types.BlockBreakTypeValidator;

import java.util.List;
import java.util.logging.Logger;

public class BlockBreakGoalValidator extends BlockBreakTypeValidator {
    public BlockBreakGoalValidator(List<MaterialJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return ModelMapper.collectables2Codes(challengesSchema.getGoals().getBlockBreakGoal().getBroken());
    }


    @Override
    protected String getPathToCurrentCollectables() {
        return "goals/blockBreakGoal/broken";
    }
}
