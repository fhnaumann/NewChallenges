package wand555.github.io.challenges.mapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
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
import wand555.github.io.challenges.rules.NoBlockPlaceRule;
import wand555.github.io.challenges.rules.PunishableRule;
import wand555.github.io.challenges.rules.Rule;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModelMapper {

    private static final Predicate<Material> VALID_BLOCKS = material -> material.isBlock() && !material.isLegacy() && !material.isAir() && material != Material.BARRIER;

    private Challenges plugin;
    private JsonNode schemaRoot;

    private ResourceBundleContext resourceBundleContext;

    private ChallengeManager challengeManager;

    public ModelMapper(Challenges plugin, ResourceBundleContext resourceBundleContext, JsonNode schemaRoot) {
        this.plugin = plugin;
        this.schemaRoot = schemaRoot;
        this.resourceBundleContext = resourceBundleContext;
        this.challengeManager = new ChallengeManager();
    }

    public ChallengeManager map2ModelClasses(TestOutputSchema json) throws JsonProcessingException {

        List<Rule> rules = mapToRules(json.getRules().getEnabledRules());
        List<Punishment> globalPunishments = mapToPunishments(json.getRules().getEnabledGlobalPunishments());
        if(!globalPunishments.isEmpty()) {
            rules.stream()
                    .filter(rule -> rule instanceof PunishableRule)
                    .map(rule -> (PunishableRule) rule)
                    .forEach(punishableRule -> {
                        punishableRule.setPunishments(globalPunishments);
                    });
        }

        List<Goal> goals = mapToGoals(json.getGoals());
        challengeManager.setRules(rules);
        challengeManager.setGoals(goals);
        return challengeManager;
    }

    private List<Goal> mapToGoals(GoalsConfig goalsConfig) {
        List<Goal> goals = new ArrayList<>();
        if(goalsConfig == null) {
            return goals;
        }
        if(goalsConfig.getMobGoal() != null) {
            MobGoalConfig mobGoalConfig = goalsConfig.getMobGoal();
            goals.add(new MobGoal(
                    new Context(plugin, null, schemaRoot, challengeManager),
                    str2Collectable(mobGoalConfig.getMobs().getAdditionalProperties(), EntityType.class)
            ));
        }
        return goals;
    }

    private <T extends Enum<T>> Map<T, Collect> str2Collectable(Map<String, CollectableDataConfig> collectables, Class<T> enumType) {
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

    private List<Rule> mapToRules(EnabledRules enabledRulesConfig) {
        List<Rule> rules = new ArrayList<>();
        if(enabledRulesConfig.getNoBlockBreak() != null) {
            NoBlockBreakRuleConfig noBlockBreakRuleConfig = enabledRulesConfig.getNoBlockBreak();
            rules.add(new NoBlockBreakRule(
                    plugin,
                    resourceBundleContext.ruleResourceBundle(),
                    mapToPunishments(noBlockBreakRuleConfig.getPunishments()),
                    new HashSet<>(str2Mat(noBlockBreakRuleConfig.getExemptions(), VALID_BLOCKS))
                    )
            );
        }
        if(enabledRulesConfig.getNoBlockPlace() != null) {
            NoBlockPlaceRuleConfig noBlockPlaceRuleConfig = enabledRulesConfig.getNoBlockPlace();
            rules.add(new NoBlockPlaceRule(
                    plugin,
                    resourceBundleContext.ruleResourceBundle(),
                    mapToPunishments(noBlockPlaceRuleConfig.getPunishments()),
                    new HashSet<>(str2Mat(noBlockPlaceRuleConfig.getExemptions(), VALID_BLOCKS))
                    )
            );
        }


        return rules;
    }

    private Collection<Material> str2Mat(Collection<String> matsAsStrings, Predicate<Material> additionalConstraints) {
        List<String> failedToMap = new ArrayList<>();
        List<Material> mapped = matsAsStrings.stream()
                .map(s -> {
                    Material matched = Material.matchMaterial(s);
                    if(matched == null || !additionalConstraints.test(matched)) {
                        failedToMap.add(s);
                    }
                    return matched;
                })
                .toList();
        if(!failedToMap.isEmpty()) {
            throw new RuntimeException(String.format("Failed to map material string(s) to valid Material enum (or they are not valid at this point): %s", String.join(", ", failedToMap)));
        }
        return mapped;
    }

    private List<Punishment> mapToPunishments(@Nullable PunishmentsConfig punishmentsConfig) {
        List<Punishment> punishments = new ArrayList<>();
        if(punishmentsConfig == null) {
            return punishments;
        }
        if(punishmentsConfig.getEndPunishment() != null) {

        }
        if(punishmentsConfig.getHealthPunishment() != null) {
            HealthPunishmentConfig healthPunishmentConfig = punishmentsConfig.getHealthPunishment();
            punishments.add(new HealthPunishment(
                    new Context(plugin, null, schemaRoot, challengeManager), //TODO: load bundle
                    Punishment.Affects.fromJSONString(healthPunishmentConfig.getAffects().value()),
                    healthPunishmentConfig.getHeartsLost(),
                    healthPunishmentConfig.getRandomizeHeartsLost()
                    )
            );
        }
        if(punishmentsConfig.getRandomEffectPunishment() != null) {
            RandomEffectPunishmentConfig randomEffectPunishmentConfig = punishmentsConfig.getRandomEffectPunishment();
            punishments.add(new RandomEffectPunishment(
                    Punishment.Affects.fromJSONString(randomEffectPunishmentConfig.getAffects().value()),
                    randomEffectPunishmentConfig.getEffectsAtOnce(),
                    randomEffectPunishmentConfig.getRandomizeEffectsAtOnce()
                    )
            );
        }
        return punishments;
    }
}
