package wand555.github.io.challenges.validation.goals;

import org.bukkit.Material;
import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.validation.CodeableValidator;
import wand555.github.io.challenges.validation.TypeValidatorHelper;
import wand555.github.io.challenges.validation.types.ItemTypeValidator;

import java.util.List;

public class ItemGoalValidator extends ItemTypeValidator {

    private final int teamIdx;

    public ItemGoalValidator(List<MaterialJSON> dataSource, int teamIdx) {
        super(dataSource);
        this.teamIdx = teamIdx;
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return TypeValidatorHelper.codes(challengesSchema, teamIdx, goalsConfig -> goalsConfig.getItemGoal().getItems());
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return TypeValidatorHelper.goalPath("itemGoal/items");
    }
}
