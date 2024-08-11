package wand555.github.io.challenges.validation.goals;

import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.mapping.DeathMessage;
import wand555.github.io.challenges.validation.TypeValidatorHelper;
import wand555.github.io.challenges.validation.types.DeathTypeValidator;

import java.util.List;

public class DeathGoalValidator extends DeathTypeValidator {

    private final int teamIdx;

    public DeathGoalValidator(List<DeathMessage> dataSource, int teamIdx) {
        super(dataSource);
        this.teamIdx = teamIdx;
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return TypeValidatorHelper.codes(challengesSchema, teamIdx, goalsConfig -> goalsConfig.getDeathGoal().getDeathMessages());
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return TypeValidatorHelper.goalPath("deathGoal/deathMessages", teamIdx);
    }
}
