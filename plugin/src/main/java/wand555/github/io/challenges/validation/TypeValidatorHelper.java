package wand555.github.io.challenges.validation;

import wand555.github.io.challenges.mapping.MaterialJSON;

public class TypeValidatorHelper {

    public static String goalPath(String goalName) {
        return "goals/%s".formatted(goalName);
    }

    public static String rulePath(String ruleName) {
        return "rules/enabledRules/%s".formatted(ruleName);
    }
}
