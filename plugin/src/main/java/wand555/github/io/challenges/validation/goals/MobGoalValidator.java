package wand555.github.io.challenges.validation.goals;

import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.EntityTypeJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.validation.CodeableValidator;
import wand555.github.io.challenges.validation.ModelValidator;
import wand555.github.io.challenges.validation.TypeValidatorHelper;
import wand555.github.io.challenges.validation.types.MobTypeValidator;

import java.util.List;

public class MobGoalValidator extends MobTypeValidator {

    private final int teamIdx;

    public MobGoalValidator(List<EntityTypeJSON> dataSource, int teamIdx) {
        super(dataSource);
        this.teamIdx = teamIdx;
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return TypeValidatorHelper.codes(challengesSchema, teamIdx, goalsConfig -> goalsConfig.getMobGoal().getMobs());
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return TypeValidatorHelper.goalPath("mobGoal/mobs", teamIdx);
    }
}
