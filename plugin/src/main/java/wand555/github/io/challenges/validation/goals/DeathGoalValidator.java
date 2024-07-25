package wand555.github.io.challenges.validation.goals;

import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.death.DeathMessage;
import wand555.github.io.challenges.validation.types.DeathTypeValidator;

import java.util.List;

public class DeathGoalValidator extends DeathTypeValidator {
    public DeathGoalValidator(List<DeathMessage> dataSource) {
        super(dataSource);
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return ModelMapper.collectables2Codes(challengesSchema.getGoals().getDeathGoal().getDeathMessages());
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return "goals/deathGoal/deathMessages";
    }
}
