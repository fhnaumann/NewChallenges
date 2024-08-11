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

    private final int teamIdx;

    public BlockBreakGoalValidator(List<MaterialJSON> dataSource, int teamIdx) {
        super(dataSource);
        this.teamIdx = teamIdx;
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return TypeValidatorHelper.codes(challengesSchema, teamIdx, goalsConfig -> goalsConfig.getBlockBreakGoal().getBroken());
    }


    @Override
    protected String getPathToCurrentCollectables() {
        return TypeValidatorHelper.goalPath("blockBreakGoal/broken", teamIdx);
    }
}
