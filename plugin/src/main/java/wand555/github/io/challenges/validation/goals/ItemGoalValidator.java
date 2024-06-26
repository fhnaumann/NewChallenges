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

    public ItemGoalValidator(List<MaterialJSON> dataSource) {
        super(dataSource);
    }

    @Override
    protected List<String> getCodes(Model challengesSchema) {
        return ModelMapper.collectables2Codes(challengesSchema.getGoals().getItemGoal().getItems());
    }

    @Override
    protected String getPathToCurrentCollectables() {
        return "goals/itemGoal/items";
    }
}
