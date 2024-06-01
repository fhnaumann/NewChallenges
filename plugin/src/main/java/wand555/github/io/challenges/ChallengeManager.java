package wand555.github.io.challenges;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Goal;
import wand555.github.io.challenges.criteria.goals.Progressable;
import wand555.github.io.challenges.criteria.goals.Skippable;
import wand555.github.io.challenges.criteria.rules.Rule;
import wand555.github.io.challenges.exceptions.UnskippableException;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.validation.BossBarShower;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class ChallengeManager implements StatusInfo {

    private @NotNull Context context;

    private @NotNull List<Rule> rules;
    private @NotNull List<Punishment> globalPunishments;
    private @NotNull List<BaseGoal> goals;

    private boolean valid;

    private GameState gameState;

    private TimerRunnable timerRunnable;

    private BossBarShower bossBarShower;

    private int currentOrder;

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

        Component toSend = ComponentUtil.formatChallengesPrefixChatMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenge.start.chat"
        );
        context.plugin().getServer().broadcast(toSend);
        goals.forEach(BaseGoal::onStart);
        //goals.stream().filter(goal -> goal instanceof BossBarDisplay).forEach(goal -> ((BossBarDisplay) goal).showBossBar(context.plugin().getServer().getOnlinePlayers()));

        timerRunnable = new TimerRunnable(context);
        timerRunnable.start();
    }

    public void pause() {
        gameState = GameState.PAUSED;
    }

    public void resume() {
        if(gameState == GameState.ENDED) {
            // BossBars were removed when gameState was set to ENDED
            goals.stream()
                    .filter(BossBarDisplay.class::isInstance)
                    .map(BossBarDisplay.class::cast)
                    .forEach(bossBarDisplay -> bossBarDisplay.showBossBar(Bukkit.getOnlinePlayers()));
        }
        gameState = GameState.RUNNING;
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
        } catch (UnskippableException e) {
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

    public List<Punishment> getGlobalPunishments() {
        return globalPunishments;
    }

    public void setGlobalPunishments(List<Punishment> globalPunishments) {
        this.globalPunishments = globalPunishments;
    }

    public void onGoalCompleted() {
        if(!isRunning()) {
            throw new RuntimeException("Goal completed while challenge is not running!");
        }
        if(allGoalsCompleted()) {
            endChallenge(true);
        }
    }

    public void endChallenge(boolean success) {
        context.plugin().getServer().getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.SPECTATOR));
        gameState = GameState.ENDED;
        if(success) {
            Component toSend = ComponentUtil.formatChallengesPrefixChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenge.complete.beaten.chat"
            );
            context.plugin().getServer().broadcast(toSend);
        }
        else {
            Component toSend = ComponentUtil.formatChallengesPrefixChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenge.complete.failed.chat"
            );
            context.plugin().getServer().broadcast(toSend);
        }

        goals.stream()
                .filter(BossBarDisplay.class::isInstance)
                .map(BossBarDisplay.class::cast)
                .forEach(bossBarDisplay -> bossBarDisplay.removeBossBar(Bukkit.getOnlinePlayers()));
    }

    public boolean allGoalsCompleted() {
        return goals.stream().allMatch(BaseGoal::isComplete);
    }

    public @NotNull List<BaseGoal> getGoals() {
        return goals;
    }

    public void setGoals(@NotNull List<BaseGoal> goals) {
        this.goals = goals;
        // goals may have time limits -> set current order to minimum order value that exists in the goals
        goals.stream().filter(BaseGoal::hasTimer).mapToInt(baseGoal -> baseGoal.getTimer().getOrder()).min().ifPresent(this::setCurrentOrder);
    }

    public long getTime() {
        // Shouldn't have made Context a singleton...
        // Some challenge data (progress) require the current time
        return timerRunnable != null ? timerRunnable.getTimer() : 0L;
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

        for (Rule rule : rules) {
            ruleComponent = ruleComponent.appendSpace().appendSpace().append(rule.getCurrentStatus()).appendNewline();
        }
        Component goalComponent = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "statusinfo.name",
                false
        ).append(ComponentUtil.COLON).appendNewline();
        for (BaseGoal goal : goals) {
            goalComponent = goalComponent.appendSpace().appendSpace().append(goal.getCurrentStatus()).appendNewline();
        }
        Component total = ruleComponent.append(goalComponent);
        context.plugin().getLogger().info(MiniMessage.miniMessage().serialize(total));
        return total.append(Component.text("\uE000"));
    }

    public List<Goal> goalsWithSameOrderNumber() {
        return goals.stream()
                .filter(BaseGoal::hasTimer)
                .filter(baseGoal -> baseGoal.getTimer().getOrder() == currentOrder)
                .map(Goal.class::cast)
                .toList();
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(int currentOrder) {
        this.currentOrder = currentOrder;
    }

    public enum GameState {
        SETUP, RUNNING, PAUSED, ENDED
    }
}
