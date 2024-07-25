package wand555.github.io.challenges.mapping;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Goal;
import wand555.github.io.challenges.criteria.goals.factory.*;
import wand555.github.io.challenges.criteria.rules.Rule;
import wand555.github.io.challenges.criteria.rules.noblockbreak.BlockBreakRule;
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
import wand555.github.io.challenges.punishments.EndPunishment;
import wand555.github.io.challenges.punishments.HealthPunishment;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.punishments.RandomEffectPunishment;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CriteriaMapper {

    public static Criterias mapCriterias(Context context, Model model) {
        List<Rule> rules = model.getRules() != null && model.getRules().getEnabledRules() != null ? mapToRules(context, model.getRules().getEnabledRules()) : new ArrayList<>();
        List<Punishment> globalPunishments = model.getRules() != null ? mapToPunishments(context, model.getRules().getEnabledGlobalPunishments()) : new ArrayList<>();
        List<BaseGoal> goals = model.getGoals() != null ? mapToGoals(context, model.getGoals()) : new ArrayList<>();
        List<BaseSetting> settings = model.getSettings() != null ? mapToSettings(context, model.getSettings()) : new ArrayList<>();
        return new Criterias(rules, globalPunishments, goals, settings);
    }

    private static List<Rule> mapToRules(@NotNull Context context, EnabledRules enabledRulesConfig) {
        List<Rule> rules = new ArrayList<>();
        if(enabledRulesConfig == null) {
            return rules;
        }
        if(enabledRulesConfig.getNoBlockBreak() != null) {
            NoBlockBreakRuleConfig noBlockBreakRuleConfig = enabledRulesConfig.getNoBlockBreak();
            rules.add(new BlockBreakRule(context, noBlockBreakRuleConfig, new BlockBreakRuleMessageHelper(context)));
        }
        if(enabledRulesConfig.getNoBlockPlace() != null) {
            rules.add(new NoBlockPlaceRule(context, enabledRulesConfig.getNoBlockPlace(), new NoBlockPlaceRuleMessageHelper(context)));
        }
        if(enabledRulesConfig.getNoMobKill() != null) {
            rules.add(new NoMobKillRule(context, enabledRulesConfig.getNoMobKill(), new NoMobKillRuleMessageHelper(context)));
        }
        if(enabledRulesConfig.getNoItem() != null) {
            rules.add(new NoItemRule(context, enabledRulesConfig.getNoItem(), new NoItemRuleMessageHelper(context)));
        }
        if(enabledRulesConfig.getNoDeath() != null) {
            rules.add(new NoDeathRule(context, enabledRulesConfig.getNoDeath(), new NoDeathRuleMessageHelper(context)));
        }
        return rules;
    }

    private static List<BaseGoal> mapToGoals(Context context, GoalsConfig goalsConfig) {
        List<BaseGoal> goals = new ArrayList<>();
        if(goalsConfig == null) {
            return goals;
        }
        if(goalsConfig.getMobGoal() != null) {
            MobGoalConfig mobGoalConfig = goalsConfig.getMobGoal();
            goals.add(new MobGoalFactory().createGoal(context, mobGoalConfig));
        }
        if(goalsConfig.getItemGoal() != null) {
            goals.add(new ItemGoalFactory().createGoal(context, goalsConfig.getItemGoal()));
        }
        if(goalsConfig.getBlockBreakGoal() != null) {
            goals.add(new BlockBreakGoalFactory().createGoal(context, goalsConfig.getBlockBreakGoal()));
        }
        if(goalsConfig.getBlockPlaceGoal() != null) {
            goals.add(new BlockPlaceGoalFactory().createGoal(context, goalsConfig.getBlockPlaceGoal()));
        }
        if(goalsConfig.getDeathGoal() != null) {
            goals.add(new DeathGoalFactory().createGoal(context, goalsConfig.getDeathGoal()));
        }
        return goals;
    }

    private static List<BaseSetting> mapToSettings(Context context, SettingsConfig settingsConfig) {
        List<BaseSetting> settings = new ArrayList<>();
        if(settingsConfig.getCustomHealthSetting() != null) {
            settings.add(new CustomHealthSetting(context, settingsConfig.getCustomHealthSetting()));
        }
        if(settingsConfig.getUltraHardcoreSetting() != null) {
            settings.add(new UltraHardcoreSetting(context, settingsConfig.getUltraHardcoreSetting()));
        }
        return settings;
    }

    public static List<Punishment> mapToPunishments(@NotNull Context context, @Nullable PunishmentsConfig punishmentsConfig) {
        List<Punishment> punishments = new ArrayList<>();
        if(punishmentsConfig == null) {
            return punishments;
        }
        if(punishmentsConfig.getEndPunishment() != null) {
            punishments.add(new EndPunishment(context, punishmentsConfig.getEndPunishment()));
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

    public record Criterias(List<Rule> rules, List<Punishment> globalPunishments, List<BaseGoal> goals, List<BaseSetting> settings) {}
}
