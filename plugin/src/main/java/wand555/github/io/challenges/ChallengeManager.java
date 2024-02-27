package wand555.github.io.challenges;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.rules.Rule;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class ChallengeManager implements StatusInfo {

    private @NotNull Context context;

    private @NotNull List<Rule> rules;
    private @NotNull List<BaseGoal> goals;

    private GameState gameState;

    private TimerRunnable timerRunnable;


    public ChallengeManager() {
    }

    public void setContext(@NotNull Context context) {
        this.context = context;
    }

    public void start() {
        if(rules.isEmpty()) {
            //throw new RuntimeException("No rules specified!");
        }
        if(goals.isEmpty()) {
            //throw new RuntimeException("No goals specified!");
        }
        gameState = GameState.RUNNING;

        Challenges.getPlugin(Challenges.class).getLogger().info("starting");
        goals.forEach(BaseGoal::onStart);
        //goals.stream().filter(goal -> goal instanceof BossBarDisplay).forEach(goal -> ((BossBarDisplay) goal).showBossBar(context.plugin().getServer().getOnlinePlayers()));

        timerRunnable = new TimerRunnable(context);
        timerRunnable.start();
    }

    public boolean isRunning() {
        return gameState == GameState.RUNNING;
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

    public void onGoalCompleted() {
        if(!isRunning()) {
            throw new RuntimeException("Goal completed while challenge is not running!");
        }
        if(allGoalsCompleted()) {
            endChallenge();
        }
    }

    public void endChallenge() {
        Bukkit.getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.SPECTATOR));
    }

    public boolean allGoalsCompleted() {
        return goals.stream().allMatch(BaseGoal::isComplete);
    }

    public @NotNull List<BaseGoal> getGoals() {
        return goals;
    }

    public void setGoals(@NotNull List<BaseGoal> goals) {
        this.goals = goals;
    }

    public long getTime() {
        return timerRunnable.getTimer();
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

    private enum GameState {
        SETUP, RUNNING, PAUSED, ENDED
    }
}
