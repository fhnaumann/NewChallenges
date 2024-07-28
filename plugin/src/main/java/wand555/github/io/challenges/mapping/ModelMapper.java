package wand555.github.io.challenges.mapping;

import com.fasterxml.jackson.databind.JsonNode;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoal;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoalMessageHelper;
import wand555.github.io.challenges.criteria.goals.blockplace.BlockPlaceGoal;
import wand555.github.io.challenges.criteria.goals.factory.*;
import wand555.github.io.challenges.criteria.goals.itemgoal.ItemGoal;
import wand555.github.io.challenges.criteria.goals.itemgoal.ItemGoalMessageHelper;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoalMessageHelper;
import wand555.github.io.challenges.criteria.rules.noblockbreak.BlockBreakRuleMessageHelper;
import wand555.github.io.challenges.criteria.rules.noblockplace.NoBlockPlaceRule;
import wand555.github.io.challenges.criteria.rules.noblockplace.NoBlockPlaceRuleMessageHelper;
import wand555.github.io.challenges.criteria.rules.nodeath.NoDeathRule;
import wand555.github.io.challenges.criteria.rules.nodeath.NoDeathRuleMessageHelper;
import wand555.github.io.challenges.criteria.rules.noitem.NoItemRule;
import wand555.github.io.challenges.criteria.rules.noitem.NoItemRuleMessageHelper;
import wand555.github.io.challenges.criteria.rules.nomobkill.NoMobKillRule;
import wand555.github.io.challenges.criteria.rules.nomobkill.NoMobKillRuleMessageHelper;
import wand555.github.io.challenges.criteria.settings.BaseSetting;
import wand555.github.io.challenges.criteria.settings.CustomHealthSetting;
import wand555.github.io.challenges.criteria.settings.UltraHardcoreSetting;
import wand555.github.io.challenges.generated.*;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoal;
import wand555.github.io.challenges.punishments.EndPunishment;
import wand555.github.io.challenges.punishments.HealthPunishment;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.punishments.RandomEffectPunishment;
import wand555.github.io.challenges.criteria.rules.noblockbreak.BlockBreakRule;
import wand555.github.io.challenges.criteria.rules.PunishableRule;
import wand555.github.io.challenges.criteria.rules.Rule;
import wand555.github.io.challenges.types.death.DeathMessage;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
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
        this.challengeManager = context.challengeManager();
        this.context = context;
    }

    public static void map2ModelClasses(Context context, Model json) {
        CriteriaMapper.Criterias criterias = CriteriaMapper.mapCriterias(context, json);

        TimerRunnable timerRunnable = new TimerRunnable(context, json.getTimer());
        // assume that if the data contains a time larger than 0, then the challenge had been previously played and was interrupted before finishing
        // it is important to set the gamestate BEFORE starting the runnable, because the runnable will increment the timer when the gamestate is running
        context.challengeManager().setGameState(timerRunnable.getTimer() > 0L ? ChallengeManager.GameState.PAUSED : ChallengeManager.GameState.SETUP);

        context.challengeManager().setTimerRunnable(timerRunnable);
        timerRunnable.start();

        context.challengeManager().setChallengeMetadata(json.getMetadata());
        context.challengeManager().setGlobalPunishments(criterias.globalPunishments());
        context.challengeManager().setRules(criterias.rules());
        context.challengeManager().setGoals(criterias.goals());
        context.challengeManager().setSettings(criterias.settings());
    }

    public static <K extends Keyed> LinkedHashMap<K, Collect> str2Collectable(List<CollectableEntryConfig> collectables, DataSourceContext dataSourceContext, Class<K> keyedType) {
        if(keyedType == Material.class) {
            return collectables.stream().map(collectableEntryConfig -> {
                K enumInstance = (K) DataSourceJSON.fromCode(dataSourceContext.materialJSONList(), collectableEntryConfig.getCollectableName()).toEnum();
                CollectableDataConfig collectableDataConfig = NullHelper.notNullOrDefault(collectableEntryConfig.getCollectableData(), CollectableDataConfig.class);
                Collect collect = new Collect(collectableDataConfig);
                return Map.entry(enumInstance, collect);
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (collect, collect2) -> collect, LinkedHashMap::new));
        }
        else if(keyedType == EntityType.class) {
            return collectables.stream().map(collectableEntryConfig -> {
                K enumInstance = (K) DataSourceJSON.fromCode(dataSourceContext.entityTypeJSONList(), collectableEntryConfig.getCollectableName()).toEnum();
                CollectableDataConfig collectableDataConfig = NullHelper.notNullOrDefault(collectableEntryConfig.getCollectableData(), CollectableDataConfig.class);
                Collect collect = new Collect(collectableDataConfig);
                return Map.entry(enumInstance, collect);
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (collect, collect2) -> collect, LinkedHashMap::new));
        }
        else if(keyedType == DeathMessage.class) {
            return collectables.stream().map(collectableEntryConfig -> {
                K enumInstance = (K) DataSourceJSON.fromCode(dataSourceContext.deathMessageList(), collectableEntryConfig.getCollectableName()).toEnum();
                CollectableDataConfig collectableDataConfig = NullHelper.notNullOrDefault(collectableEntryConfig.getCollectableData(), CollectableDataConfig.class);
                Collect collect = new Collect(collectableDataConfig);
                return Map.entry(enumInstance, collect);
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (collect, collect2) -> collect, LinkedHashMap::new));
        }
        else {
            throw new RuntimeException("Unknown how to handle '%s' when mapping to collectable.".formatted(keyedType));
        }
    }

    public static List<String> collectables2Codes(List<CollectableEntryConfig> collectables) {
        return collectables.stream().map(CollectableEntryConfig::getCollectableName).toList();
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
}
