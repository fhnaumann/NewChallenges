package wand555.github.io.challenges.mapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.ResourceBundleContext;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoal;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoalMessageHelper;
import wand555.github.io.challenges.criteria.goals.itemgoal.ItemGoal;
import wand555.github.io.challenges.criteria.goals.itemgoal.ItemGoalMessageHelper;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoalMessageHelper;
import wand555.github.io.challenges.criteria.rules.noblockbreak.NoBlockBreakMessageHelper;
import wand555.github.io.challenges.criteria.rules.noitem.NoItemRule;
import wand555.github.io.challenges.criteria.rules.noitem.NoItemRuleMessageHelper;
import wand555.github.io.challenges.criteria.rules.nomobkill.NoMobKillRule;
import wand555.github.io.challenges.criteria.rules.nomobkill.NoMobKillRuleMessageHelper;
import wand555.github.io.challenges.generated.*;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoal;
import wand555.github.io.challenges.punishments.HealthPunishment;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.punishments.RandomEffectPunishment;
import wand555.github.io.challenges.criteria.rules.noblockbreak.NoBlockBreakRule;
import wand555.github.io.challenges.criteria.rules.PunishableRule;
import wand555.github.io.challenges.criteria.rules.Rule;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModelMapper {

    public static final Predicate<MaterialJSON> VALID_ITEMS = MaterialJSON::isItem;
    public static final Predicate<MaterialJSON> VALID_BLOCKS = MaterialJSON::isBlock;

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

        List<Rule> rules = mapToRules(context, json.getRules() != null ? json.getRules().getEnabledRules() : new EnabledRules());
        List<Punishment> globalPunishments = mapToPunishments(context, json.getRules() != null ? json.getRules().getEnabledGlobalPunishments() : new PunishmentsConfig());
        if(!globalPunishments.isEmpty()) {
            rules.stream()
                    .filter(rule -> rule instanceof PunishableRule)
                    .map(rule -> (PunishableRule) rule)
                    .forEach(punishableRule -> {
                        punishableRule.setPunishments(globalPunishments);
                    });
        }

        List<BaseGoal> goals = mapToGoals(context, json.getGoals());
        context.challengeManager().setRules(rules);
        context.challengeManager().setGoals(goals);
    }

    private static List<BaseGoal> mapToGoals(Context context, GoalsConfig goalsConfig) {
        List<BaseGoal> goals = new ArrayList<>();
        if(goalsConfig == null) {
            return goals;
        }
        if(goalsConfig.getMobGoal() != null) {
            MobGoalConfig mobGoalConfig = goalsConfig.getMobGoal();
            goals.add(new MobGoal(
                    context,
                    mobGoalConfig,
                    new MobGoalMessageHelper(context)
            ));
        }
        if(goalsConfig.getItemGoal() != null) {
            goals.add(new ItemGoal(context, goalsConfig.getItemGoal(), new ItemGoalMessageHelper(context)));
        }
        if(goalsConfig.getBlockbreakGoal() != null) {
            goals.add(new BlockBreakGoal(context, goalsConfig.getBlockbreakGoal(), new BlockBreakGoalMessageHelper(context)));
        }
        return goals;
    }

    public static <K extends Keyed> LinkedHashMap<K, Collect> str2Collectable(List<CollectableEntryConfig> collectables, DataSourceContext dataSourceContext, Class<K> keyedType) {
        if(keyedType == Material.class) {
            return collectables.stream().map(collectableEntryConfig -> {
                K enumInstance = (K) DataSourceJSON.fromCode(dataSourceContext.materialJSONList(), collectableEntryConfig.getCollectableName()).toEnum();
                Collect collect = new Collect(collectableEntryConfig.getCollectableData().getAmountNeeded(), collectableEntryConfig.getCollectableData().getCurrentAmount());
                return Map.entry(enumInstance, collect);
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (collect, collect2) -> collect, LinkedHashMap::new));
        }
        else if(keyedType == EntityType.class) {
            return collectables.stream().map(collectableEntryConfig -> {
                K enumInstance = (K) DataSourceJSON.fromCode(dataSourceContext.entityTypeJSONList(), collectableEntryConfig.getCollectableName()).toEnum();
                Collect collect = new Collect(collectableEntryConfig.getCollectableData().getAmountNeeded(), collectableEntryConfig.getCollectableData().getCurrentAmount());
                return Map.entry(enumInstance, collect);
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (collect, collect2) -> collect, LinkedHashMap::new));
        }
        else {
            throw new RuntimeException("Unknown how to handle '%s' when mapping to collectable.".formatted(keyedType));
        }
    }

    public static <T> String enum2Code(List<MaterialJSON> materialJSONS, T codeAsMaterial) {
        // TODO: implement for other enums, not just material
        return materialJSONS.stream().filter(materialJSON -> Material.matchMaterial(materialJSON.code()) == codeAsMaterial).findFirst().orElseThrow().code();
    }

    private static List<Rule> mapToRules(@NotNull Context context, EnabledRules enabledRulesConfig) {
        List<Rule> rules = new ArrayList<>();
        if(enabledRulesConfig == null) {
            return rules;
        }
        if(enabledRulesConfig.getNoBlockBreak() != null) {
            NoBlockBreakRuleConfig noBlockBreakRuleConfig = enabledRulesConfig.getNoBlockBreak();
            rules.add(new NoBlockBreakRule(context, noBlockBreakRuleConfig, new NoBlockBreakMessageHelper(context)));
        }
        if(enabledRulesConfig.getNoBlockPlace() != null) {
            NoBlockPlaceRuleConfig noBlockPlaceRuleConfig = enabledRulesConfig.getNoBlockPlace();
        }
        if(enabledRulesConfig.getNoMobKill() != null) {
            rules.add(new NoMobKillRule(context, enabledRulesConfig.getNoMobKill(), new NoMobKillRuleMessageHelper(context)));
        }
        if(enabledRulesConfig.getNoItem() != null) {
            rules.add(new NoItemRule(context, enabledRulesConfig.getNoItem(), new NoItemRuleMessageHelper(context)));
        }


        return rules;
    }

    @Deprecated
    @Nullable
    public static Material str2Mat(@NotNull String matAsString, @NotNull Predicate<Material> additionalConstraints) {
        Material matched = Material.matchMaterial(matAsString);
        if(matched == null || !additionalConstraints.test(matched)) {
            throw new RuntimeException();
        }
        return matched;
    }

    @Deprecated
    @NotNull
    public static Collection<Material> str2Mat(@NotNull Collection<String> matsAsCodes, @NotNull Predicate<Material> additionalConstraints) {
        List<String> failedToMap = new ArrayList<>();
        List<Material> mapped = matsAsCodes.stream()
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



    @NotNull
    public static Collection<Material> str2Materials(@NotNull Collection<MaterialJSON> dataSource, @NotNull Collection<String> matsAsCodes) {
        return matsAsCodes.stream()
                .map(code -> str2Material(dataSource, code))
                .map(MaterialJSON::toEnum)
                .toList();
    }

    public static MaterialJSON str2Material(@NotNull Collection<MaterialJSON> dataSource, @NotNull String matAsCode) {
        return dataSource.stream().filter(matJSON -> matJSON.getCode().equals(matAsCode)).findFirst().orElseThrow();
    }

    @NotNull
    public static Collection<EntityType> str2EntityType(@NotNull Collection<EntityTypeJSON> dataSource, @NotNull Collection<String> entityTypesAsCodes) {
        return entityTypesAsCodes.stream()
                .map(code -> str2EntityType(dataSource, code))
                .map(EntityTypeJSON::toEnum)
                .toList();
    }

    public static EntityTypeJSON str2EntityType(@NotNull Collection<EntityTypeJSON> dataSource, @NotNull String entityTypeAsCode) {
        return dataSource.stream().filter(entityTypeJSON -> entityTypeJSON.getCode().equals(entityTypeAsCode)).findFirst().orElseThrow();
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
