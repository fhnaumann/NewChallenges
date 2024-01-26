package wand555.github.io.challenges;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import wand555.github.io.challenges.goals.Goal;
import wand555.github.io.challenges.rules.Rule;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class ChallengeManager {

    private static ChallengeManager instance;

    private @NotNull List<Rule> rules;
    private @NotNull List<Goal> goals;

    private GameState gameState;


    public void start() {
        if(rules.isEmpty()) {
            throw new RuntimeException("No rules specified!");
        }
        if(goals.isEmpty()) {
            throw new RuntimeException("No goals specified!");
        }
        gameState = GameState.RUNNING;
        Challenges.getPlugin(Challenges.class).getLogger().info("starting");
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
        Challenges.getPlugin(Challenges.class).getLogger().info("goal completed triggered");
        if(!isRunning()) {
            throw new RuntimeException("Goal completed while challenge is not running!");
        }
        if(allGoalsCompleted()) {
            endChallenge();
        }
    }

    private void endChallenge() {
        Challenges.getPlugin(Challenges.class).getLogger().info("ending");
        Bukkit.getOnlinePlayers().forEach(player -> player.setGameMode(GameMode.SPECTATOR));
    }

    public boolean allGoalsCompleted() {
        return goals.stream().allMatch(Goal::isComplete);
    }

    public @NotNull List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(@NotNull List<Goal> goals) {
        this.goals = goals;
    }

    public static ChallengeManager getInstance() {
        if(instance == null) {
            instance = new ChallengeManager();
        }
        return instance;
    }

    private enum GameState {
        SETUP, RUNNING, PAUSED, ENDED
    }
}
