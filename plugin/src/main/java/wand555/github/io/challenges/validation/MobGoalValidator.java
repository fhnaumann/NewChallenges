package wand555.github.io.challenges.validation;

import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.generated.ChallengesSchema;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.EntityTypeJSON;

import java.util.List;

public class MobGoalValidator extends CollectableValidator<EntityTypeJSON, EntityType> {
    public MobGoalValidator(List<EntityTypeJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected List<CollectableEntryConfig> getCollectables(ChallengesSchema challengesSchema) {
        return challengesSchema.getGoals().getMobGoal().getMobs();
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return "goals/mobGoal/mobs";
    }
}
