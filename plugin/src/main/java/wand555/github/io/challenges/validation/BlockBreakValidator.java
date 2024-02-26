package wand555.github.io.challenges.validation;

import org.bukkit.Material;
import wand555.github.io.challenges.generated.ChallengesSchema;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.MaterialJSON;

import java.util.List;

public class BlockBreakValidator extends CollectableValidator<MaterialJSON, Material> {
    public BlockBreakValidator(List<MaterialJSON> dataSource) {
        super(dataSource, MaterialJSON::isBlock);
    }

    @Override
    protected List<CollectableEntryConfig> getCollectables(ChallengesSchema challengesSchema) {
        return challengesSchema.getGoals().getBlockbreakGoal().getBroken();
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return "goals/blockBreakGoal/broken";
    }
}
