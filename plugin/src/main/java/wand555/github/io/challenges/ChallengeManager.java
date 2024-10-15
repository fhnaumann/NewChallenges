package wand555.github.io.challenges;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.criteria.Criteria;
import wand555.github.io.challenges.criteria.Loadable;
import wand555.github.io.challenges.criteria.goals.*;
import wand555.github.io.challenges.criteria.rules.Rule;
import wand555.github.io.challenges.criteria.settings.BaseSetting;
import wand555.github.io.challenges.exceptions.UnskippableException;
import wand555.github.io.challenges.generated.ChallengeMetadata;
import wand555.github.io.challenges.generated.MCEventAlias;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.punishments.InteractionManager;
import wand555.github.io.challenges.teams.Team;
import wand555.github.io.challenges.validation.BossBarShower;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Stream;

public class ChallengeManager implements StatusInfo {

    private @NotNull Context context;

    private @NotNull List<Rule> rules;
    private @NotNull List<Punishment> globalPunishments;
    private @NotNull List<BaseGoal> goals;

    private @NotNull List<BaseSetting> settings;

    private @NotNull ChallengeMetadata challengeMetadata;

    private @NotNull List<Team> teams;

    private boolean valid;

    private GameState gameState;

    private TimerRunnable timerRunnable;

    private BossBarShower bossBarShower;

    public ChallengeManager() {
        gameState = GameState.SETUP;
    }

    public void setContext(@NotNull Context context) {
        this.context = context;
    }

    public void setBossBarShower(BossBarShower bossBarShower) {
        this.bossBarShower = bossBarShower;
    }

    public void start() {
        if(rules.isEmpty()) {
            //throw new RuntimeException("No rules specified!");
        }
        if(goals.isEmpty()) {
            //throw new RuntimeException("No goals specified!");
        }
        gameState = GameState.RUNNING;

        // clear any title and subtitle messages from when the challenge was loaded
        Bukkit.getOnlinePlayers().forEach(Audience::clearTitle);

        Component toSend = ComponentUtil.formatChallengesPrefixChatMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenge.start.chat"
        );
        context.plugin().getServer().broadcast(toSend);

        if(hasTeams()) {
            getTeams().forEach(team -> team.getGoals().forEach(baseGoal -> baseGoal.onStart(team)));
        }
        else {
            goals.forEach(baseGoal -> baseGoal.onStart(Team.ALL_TEAM));
        }

        //goals.stream().filter(baseGoal -> !baseGoal.hasTimer() || baseGoal.getTimer().getOrder() == getCurrentOrder()).forEach(BaseGoal::onStart);
        //goals.stream().filter(goal -> goal instanceof BossBarDisplay).forEach(goal -> ((BossBarDisplay) goal).showBossBar(context.plugin().getServer().getOnlinePlayers()));

        settings.forEach(baseSetting -> baseSetting.onStart(Team.ALL_TEAM));

        context.liveService().eventProvider().sendEvent(0, MCEventAlias.EventType.START, null);
    }


    public void pause() {
        // abort everything when paused
        Bukkit.getOnlinePlayers().forEach(player -> {
            InteractionManager.removeUnableToInteract(context, player, true);
        });
        gameState = GameState.PAUSED;
        allCriterias().forEach(Criteria::onPause);
        context.liveService().eventProvider().sendEvent(0, MCEventAlias.EventType.PAUSE, null);
    }

    public void resume() {
        if(gameState == GameState.ENDED) {
            // BossBars were removed when gameState was set to ENDED
            teams.forEach(team -> {
                team.getGoals().stream()
                     .filter(BossBarDisplay.class::isInstance)
                     .filter(baseGoal -> baseGoal.hasTimer() && baseGoal.getTimer().getOrder() == team.getCurrentOrder())
                     .map(BossBarDisplay.class::cast)
                     .forEach(bossBarDisplay -> bossBarDisplay.showBossBar(team.getAllOnlinePlayers()));
            });

        }
        gameState = GameState.RUNNING;
        allCriterias().forEach(Criteria::onResume);
        context.liveService().eventProvider().sendEvent(0, MCEventAlias.EventType.RESUME, null);
    }

    public void onProgress(Player player, Progressable progressable) {
        progressable.onProgressStatus(player);
    }

    public void onSkip(Player player) {
        List<Skippable> skippables = goals.stream().filter(Skippable.class::isInstance).map(Skippable.class::cast).toList();
        if(skippables.isEmpty()) {
            player.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().commandsResourceBundle(),
                    "skip.empty.message"
            ));
            return;
        }
        if(skippables.size() > 1) {
            player.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().commandsResourceBundle(),
                    "skip.toomany.message"
            ));
            return;
        }
        try {
            skippables.get(0).onSkip(player);
            player.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().commandsResourceBundle(),
                    "skip.success.message"
            ));
        } catch(UnskippableException e) {
            player.sendMessage(ComponentUtil.formatChallengesPrefixChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().commandsResourceBundle(),
                    "skip.unskippable.message"
            ));
        }
    }

    public boolean isRunning() {
        return gameState == GameState.RUNNING;
    }

    public boolean isPaused() {
        return gameState == GameState.PAUSED;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean isSetup() {
        return gameState == GameState.SETUP;
    }

    public boolean isInChallenge(UUID uuid) {
        return true;
    }

    public @NotNull List<Rule> getRules() {
        return rules;
    }

    public void setRules(@NotNull List<Rule> rules) {
        this.rules = rules;
    }

    public @NotNull List<Punishment> getGlobalPunishments() {
        return globalPunishments;
    }

    public void setGlobalPunishments(@NotNull List<Punishment> globalPunishments) {
        this.globalPunishments = globalPunishments;
    }

    public void failChallengeFor(Team team) {
        if(team == Team.ALL_TEAM) {
            endChallenge(false);
        }
        else {
            team.getAllOnlinePlayers().forEach(player -> {
                InteractionManager.removeUnableToInteract(context, player, true);

                player.setGameMode(GameMode.SPECTATOR);
                player.getActivePotionEffects().clear();
            });

            team.getGoals().forEach(baseGoal -> baseGoal.onEnd(team));
        }
    }

    public void endChallenge(boolean success, Team winnerTeam) {
        context.plugin().getServer().getOnlinePlayers().forEach(player -> {
            // The player might be somewhere they shouldn't be when the challenge is ended.
            // In that case, behave as if the thing they are busy with (ongoing MLG, ...) is completed.
            // Currently, this may be the case in two scenarios:
            // 1. The player is within an ongoing MLG from a punishment.
            // 2. The player is within an ongoing MLG from a goal.
            InteractionManager.removeUnableToInteract(context, player, true);

            player.setGameMode(GameMode.SPECTATOR);
            player.getActivePotionEffects().clear();
        });
        gameState = GameState.ENDED;
        context.liveService().eventProvider().sendEvent(0, MCEventAlias.EventType.END, null);
        if(success) {
            if(winnerTeam != null) {
                Component toSend = ComponentUtil.formatChallengesPrefixChatMessage(
                        context.plugin(),
                        context.resourceBundleContext().miscResourceBundle(),
                        "challenge.complete.team.won.chat",
                        Map.of("teamname", Component.text(winnerTeam.getTeamName()))
                );
                context.plugin().getServer().broadcast(toSend);
            }
            else {
                Component toSend = ComponentUtil.formatChallengesPrefixChatMessage(
                        context.plugin(),
                        context.resourceBundleContext().miscResourceBundle(),
                        "challenge.complete.beaten.chat"
                );
                context.plugin().getServer().broadcast(toSend);
            }

        } else {
            Component toSend = ComponentUtil.formatChallengesPrefixChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenge.complete.failed.chat"
            );
            context.plugin().getServer().broadcast(toSend);
        }

        // TODO: potentially not needed?
        getTeams().forEach(team -> {
            team.getGoals().stream()
                      .filter(BossBarDisplay.class::isInstance)
                      .map(BossBarDisplay.class::cast)
                      .forEach(bossBarDisplay -> bossBarDisplay.removeBossBar(Bukkit.getOnlinePlayers()));
        });


        // goals were handled separately earlier
        allCriterias().stream().filter(criteria -> !(criteria instanceof Goal)).forEach(Criteria::onEnd);

        // remove active file, so it is not automatically loaded when the server restarts from now on
        context.offlineTempData().addAndSave("fileNameBeingPlayed", null);
    }

    public void endChallenge(boolean success) {
        endChallenge(success, null);
    }

    public @NotNull List<BaseGoal> getGoals() {
        return goals;
    }

    public void setGoals(@NotNull List<BaseGoal> goals) {
        this.goals = goals;
        // goals may have time limits -> set current order to minimum order value that exists in the goals
        if(Team.getGlobalCurrentOrder() == -1) {
            Team.setCurrentOrderIfNotYetSetToMinOrderValueThatExistsIn(Team.ALL_TEAM);
        }

    }

    public int getTime() {
        // Shouldn't have made Context a singleton...
        // Some challenge data (progress) require the current time
        return (int) (timerRunnable != null ? timerRunnable.getTimer() : 0L);
    }

    public void setTimerRunnable(TimerRunnable timerRunnable) {
        this.timerRunnable = timerRunnable;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void shutdownRunnables() {
        if(timerRunnable != null) {
            timerRunnable.shutdown();
        }
    }

    @Override
    public Component getCurrentStatus() {
        Component empty = Component.empty();
        Component ruleComponent = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().ruleResourceBundle(),
                "statusinfo.name",
                false
        ).append(ComponentUtil.COLON).appendNewline();

        for(Rule rule : rules) {
            ruleComponent = ruleComponent.appendSpace().appendSpace().append(rule.getCurrentStatus()).appendNewline();
        }
        Component goalComponent = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "statusinfo.name",
                false
        ).append(ComponentUtil.COLON).appendNewline();
        for(BaseGoal goal : goals) {
            goalComponent = goalComponent.appendSpace().appendSpace().append(goal.getCurrentStatus()).appendNewline();
        }
        Component total = ruleComponent.append(goalComponent);
        context.plugin().getLogger().info(MiniMessage.miniMessage().serialize(total));
        return total.append(Component.text("\uE000"));
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public @NotNull ChallengeMetadata getChallengeMetadata() {
        return challengeMetadata;
    }

    public void setChallengeMetadata(@NotNull ChallengeMetadata challengeMetadata) {
        this.challengeMetadata = challengeMetadata;
    }

    public @NotNull List<BaseSetting> getSettings() {
        return settings;
    }

    public void setSettings(@NotNull List<BaseSetting> settings) {
        this.settings = settings;
    }

    public @NotNull List<Team> getTeams() {
        return teams;
    }

    public void setTeams(@NotNull List<Team> teams) {
        this.teams = teams;
    }

    public boolean hasTeams() {
        return !teams.isEmpty();
    }

    public void unload() {
        getTeams().forEach(team -> {
            team.getGoals().forEach(Loadable::unload);
        });
        if(getRules() != null) {
            getRules().forEach(Loadable::unload);
        }
    }

    public Collection<Criteria> allCriterias() {
        return Stream.of(rules, globalPunishments, goals, settings)
                     .flatMap(Collection::stream)
                     .filter(Objects::nonNull)
                     .map(Criteria.class::cast)
                     .toList();
    }

    public boolean canTakeEffect(Context context, Player player) {
        // TODO: replace every trigger call with a check to this method
        return isRunning() && (!hasTeams() || (hasTeams() && Team.getTeamPlayerIn(context, player.getUniqueId()) != Team.ALL_TEAM));
    }

    public enum GameState {
        SETUP,
        RUNNING,
        PAUSED,
        ENDED
    }

}
