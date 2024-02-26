package wand555.github.io.challenges.validation;

import org.bukkit.Material;
import wand555.github.io.challenges.generated.ChallengesSchema;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.mapping.MaterialJSON;

import java.util.List;

public class ItemGoalValidator extends CollectableValidator<MaterialJSON, Material> {

    public ItemGoalValidator(List<MaterialJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected List<CollectableEntryConfig> getCollectables(ChallengesSchema challengesSchema) {
        return challengesSchema.getGoals().getItemGoal().getItems();
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return "goals/itemGoal/items";
    }
}
