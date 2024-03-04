package wand555.github.io.challenges.validation;

import org.bukkit.Material;
import wand555.github.io.challenges.generated.ChallengesSchema;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.MaterialJSON;

import java.util.List;

public class BlockBreakValidator extends CodeableValidator<MaterialJSON, Material> {
    public BlockBreakValidator(List<MaterialJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected ValidationResult.ValidationResultBuilder performAdditionalValidation(ValidationResult.ValidationResultBuilder builder, ChallengesSchema schema) {
        return null;
    }

    @Override
    protected boolean additionalCodeConstraints(MaterialJSON dataSourceElement) {
        return dataSourceElement.isBlock();
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
