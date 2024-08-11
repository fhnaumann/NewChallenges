package wand555.github.io.challenges.validation;

import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.validation.goals.GoalsValidator;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class TypeValidatorHelper {

    public static String goalPath(String goalName) {
        return goalPath(goalName, -1);
    }

    public static String rulePath(String ruleName) {
        return "rules/enabledRules/%s".formatted(ruleName);
    }

    public static List<String> codes(Model model, int teamIdx, Function<GoalsConfig, List<CollectableEntryConfig>> collectables) {
        if(teamIdx == -1) {
            return ModelMapper.collectables2Codes(collectables.apply(model.getGoals()));
        } else {
            return ModelMapper.collectables2Codes(collectables.apply(model.getTeams().get(teamIdx).getGoals()));
        }
    }

    public static String goalPath(String goalName, int teamIdx) {
        String path = "";
        if(teamIdx == -1) {
            path = "goals/%s".formatted(goalName);
        } else {
            path = "teams[%s]/".formatted(teamIdx) + path;
        }
        return path;
    }
}
