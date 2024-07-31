package wand555.github.io.challenges.criteria.goals.bossbar;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.Context;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class BossBarPart<T> {

    protected final Context context;
    protected final GoalInformation<T> goalInformation;

    public BossBarPart(Context context, GoalInformation<T> goalInformation) {
        this.context = context;
        this.goalInformation = goalInformation;
    }

    public abstract Component buildPart();

    public static record GoalInformation<T>(
            String goalNameInResourceBundle, AdditionalPlaceholderInformation<T> additionalPlaceholderInformation
    ) {

    }

    public static interface AdditionalPlaceholderInformation<T> {
        Map<String, Component> additionalPlaceholders(T data);
    }

    public static class NoAdditionalPlaceholderInformation implements AdditionalPlaceholderInformation<Object> {

        @Override
        public Map<String, Component> additionalPlaceholders(Object data) {
            return Map.of();
        }
    }
}
