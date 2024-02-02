package wand555.github.io.challenges.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.ResourceBundleContext;
import wand555.github.io.challenges.generated.*;
import wand555.github.io.challenges.goals.Collect;
import wand555.github.io.challenges.goals.Goal;
import wand555.github.io.challenges.goals.MobGoal;
import wand555.github.io.challenges.punishments.HealthPunishment;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.punishments.RandomEffectPunishment;
import wand555.github.io.challenges.rules.NoBlockBreakRule;
import wand555.github.io.challenges.rules.PunishableRule;
import wand555.github.io.challenges.rules.Rule;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModelMapper {

    public static final Predicate<Material> VALID_BLOCKS = material -> material.isBlock() && !material.isLegacy() && !material.isAir() && material != Material.BARRIER;

    private Challenges plugin;
    private JsonNode schemaRoot;

    private ResourceBundleContext resourceBundleContext;

    private ChallengeManager challengeManager;

    private Context context;

    public ModelMapper(Context context) {
        this.plugin = context.plugin();
        this.schemaRoot = schemaRoot;
        this.resourceBundleContext = resourceBundleContext;
        this.challengeManager = context.challengeManager();
        this.context = context;
    }

    public static void map2ModelClasses(Context context, ChallengesSchema json) {

        List<Rule> rules = mapToRules(context, json.getRules().getEnabledRules());
        List<Punishment> globalPunishments = mapToPunishments(context, json.getRules().getEnabledGlobalPunishments());
        if(!globalPunishments.isEmpty()) {
            rules.stream()
                    .filter(rule -> rule instanceof PunishableRule)
                    .map(rule -> (PunishableRule) rule)
                    .forEach(punishableRule -> {
                        punishableRule.setPunishments(globalPunishments);
                    });
        }

        List<Goal> goals = mapToGoals(context, json.getGoals());
        context.challengeManager().setRules(rules);
        context.challengeManager().setGoals(goals);
    }

    private static List<Goal> mapToGoals(Context context, GoalsConfig goalsConfig) {
        List<Goal> goals = new ArrayList<>();
        if(goalsConfig == null) {
            return goals;
        }
        if(goalsConfig.getMobGoal() != null) {
            MobGoalConfig mobGoalConfig = goalsConfig.getMobGoal();
            goals.add(new MobGoal(
                    context,
                    str2Collectable(mobGoalConfig.getMobs().getAdditionalProperties(), EntityType.class)
            ));
        }
        return goals;
    }

    private static <T extends Enum<T>> Map<T, Collect> str2Collectable(Map<String, CollectableDataConfig> collectables, Class<T> enumType) {
        List<String> failedToMap = new ArrayList<>();
        Map<T, Collect> mapped = collectables.entrySet().stream()
                .map(entry -> {
                    try {
                        T matched = Enum.valueOf(enumType, entry.getKey());
                        Collect collect = new Collect(entry.getValue().getAmountNeeded(), entry.getValue().getCurrentAmount());
                        return Map.entry(matched, collect);
                    } catch (IllegalArgumentException | NullPointerException e) {
                        failedToMap.add(entry.getKey());
                        // return anything as it does not matter because we will error out in the
                        // next step anyway, because we encountered an error during the string -> enum conversion
                        return Map.entry((T)EntityType.BAT, new Collect(0));
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if(!failedToMap.isEmpty()) {
            throw new RuntimeException(String.format("Failed to map material string(s) to valid enum: %s", String.join(",", failedToMap)));
        }
        return mapped;
    }

    private static List<Rule> mapToRules(@NotNull Context context, EnabledRules enabledRulesConfig) {
        List<Rule> rules = new ArrayList<>();
        if(enabledRulesConfig.getNoBlockBreak() != null) {
            NoBlockBreakRuleConfig noBlockBreakRuleConfig = enabledRulesConfig.getNoBlockBreak();
            rules.add(new NoBlockBreakRule(context, noBlockBreakRuleConfig));
        }
        if(enabledRulesConfig.getNoBlockPlace() != null) {
            NoBlockPlaceRuleConfig noBlockPlaceRuleConfig = enabledRulesConfig.getNoBlockPlace();
        }


        return rules;
    }

    @Nullable
    public static Material str2Mat(@NotNull String matAsString, @NotNull Predicate<Material> additionalConstraints) {
        Material matched = Material.matchMaterial(matAsString);
        if(matched == null || !additionalConstraints.test(matched)) {
            throw new RuntimeException();
        }
        return matched;
    }

    @NotNull
    public static Collection<Material> str2Mat(@NotNull Collection<String> matsAsStrings, @NotNull Predicate<Material> additionalConstraints) {
        List<String> failedToMap = new ArrayList<>();
        List<Material> mapped = matsAsStrings.stream()
                .map(s -> {
                    try {
                        return str2Mat(s, additionalConstraints);
                    } catch (RuntimeException e) {
                        failedToMap.add(s);
                        return null;
                    }
                })
                .toList();
        if(!failedToMap.isEmpty()) {
            throw new RuntimeException(String.format("Failed to map material string(s) to valid Material enum (or they are not valid at this point): %s", String.join(", ", failedToMap)));
        }
        return mapped;
    }

    public static List<Punishment> mapToPunishments(@NotNull Context context, @Nullable PunishmentsConfig punishmentsConfig) {
        List<Punishment> punishments = new ArrayList<>();
        if(punishmentsConfig == null) {
            return punishments;
        }
        if(punishmentsConfig.getEndPunishment() != null) {

        }
        if(punishmentsConfig.getHealthPunishment() != null) {
            HealthPunishmentConfig healthPunishmentConfig = punishmentsConfig.getHealthPunishment();
            punishments.add(new HealthPunishment(context, healthPunishmentConfig));
        }
        if(punishmentsConfig.getRandomEffectPunishment() != null) {
            RandomEffectPunishmentConfig randomEffectPunishmentConfig = punishmentsConfig.getRandomEffectPunishment();
            punishments.add(new RandomEffectPunishment(context, randomEffectPunishmentConfig));
        }
        return punishments;
    }
}
