package wand555.github.io.challenges.teams;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.VisibleForTesting;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Goal;
import wand555.github.io.challenges.criteria.goals.GoalCompletion;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.TeamConfig;
import wand555.github.io.challenges.mapping.CriteriaMapper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Team implements Storable<TeamConfig> {

    public static Team ALL_TEAM = null;

    private final Context context;
    private final String teamName;
    private final List<UUID> players;
    private final List<BaseGoal> goals;
    private int currentOrder;


    public Team(Context context, TeamConfig config) {
        this.context = context;
        this.teamName = config.getTeamName();
        this.players = new ArrayList<>(config.getPlayerUUIDs().stream().map(UUID::fromString).toList());
        this.goals = CriteriaMapper.mapToGoals(context, config.getGoals());
        this.currentOrder = config.getCurrentOrder();
        if(currentOrder == -1) {
            Team.setCurrentOrderIfNotYetSetToMinOrderValueThatExistsIn(this);
        }
    }

    // mocking things messed up some methods in this class and I don't know why, that's why this test-only constructor exists
    @VisibleForTesting
    public Team(String teamName, List<BaseGoal> goals, List<Player> players, int currentOrder) {
        this.context = null;
        this.teamName = teamName;
        this.goals = goals;
        this.players = players.stream().map(Entity::getUniqueId).toList();
        this.currentOrder = currentOrder;
    }

    private Team(Context context, int globalCurrentOrder) {
        this.context = context;
        this.teamName = "";
        this.players = null;
        this.goals = null;
        // In the case that this is ALL_TEAM, the value will be set when the goals are set in the challenges manager
        this.currentOrder = globalCurrentOrder;

    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam(getTeamName()).addPlayer(player);
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam(getTeamName()).removePlayer(player);
    }

    public String getTeamName() {
        return teamName;
    }

    public boolean isInTeam(UUID uuid) {
        return getPlayers().contains(uuid);
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public List<Player> getAllOnlinePlayers() {
        return getPlayers().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).toList();
    }

    public List<BaseGoal> getGoals() {
        return goals;
    }

    public static Team getTeamPlayerIn(Context context, UUID uuid) {
        return context.challengeManager().getTeams().stream().filter(team -> team.isInTeam(uuid)).findFirst().orElse(
                ALL_TEAM);
    }

    public static void onGoalComplete(Context context, Player lastCompletionStepProvidedBy, GoalCompletion goalCompletion) {
        Team team = Team.getTeamPlayerIn(context, lastCompletionStepProvidedBy.getUniqueId());

        if(goalCompletion == GoalCompletion.TIMER_BEATEN && team.allGoalsWithOrderCurrentNumberComplete()) {
            team.getGoals().stream().filter(baseGoal -> baseGoal.hasTimer() && baseGoal.getTimer().getOrder() == team.getCurrentOrder()).forEach(baseGoal -> baseGoal.onEnd(team));

            int nextOrderNumber = team.nextOrderNumber();
            // Will be -1 if there is no next order number, because all goals are now complete.
            // In that case the challenge will be ended a few lines below anyway.
            team.setCurrentOrder(nextOrderNumber);
            // initialize goals that now "start"
            team.getGoals().stream().filter(baseGoal -> baseGoal.hasTimer() && baseGoal.getTimer().getOrder() == team.getCurrentOrder()).forEach(
                    baseGoal -> baseGoal.onStart(team));
        }
        if(team.allGoalsCompleted()) {
            context.challengeManager().endChallenge(true, team != ALL_TEAM ? team : null);
        }
    }

    private boolean allGoalsWithOrderCurrentNumberComplete() {
        return goalsWithSameOrderNumber(getGoals(), getCurrentOrder()).stream().allMatch(Goal::isComplete);
    }

    private boolean allGoalsCompleted() {
        return getGoals().stream().allMatch(BaseGoal::isComplete);
    }

    private static List<Goal> goalsWithSameOrderNumber(List<BaseGoal> goals, int currentOrder) {
        return goals.stream()
                         .filter(BaseGoal::hasTimer)
                         .filter(baseGoal -> baseGoal.getTimer().getOrder() == currentOrder)
                         .map(Goal.class::cast)
                         .toList();
    }

    public static Map<Team, List<Goal>> goalsWithSameOrderNumberAcrossAllTeams(ChallengeManager manager) {
        return manager.getTeams()
                      .stream()
                      .collect(Collectors.toMap(Function.identity(), team -> Team.goalsWithSameOrderNumber(team.getGoals(), team.getCurrentOrder())));
    }

    private int nextOrderNumber() {
        return getGoals().stream()
                         .filter(BaseGoal::hasTimer)
                         .mapToInt(baseGoal -> baseGoal.getTimer().getOrder())
                         .filter(value -> value > getCurrentOrder())
                         .min()
                         .orElse(-1);
    }

    @Override
    public TeamConfig toGeneratedJSONClass() {
        GoalsConfig goalsConfig = new GoalsConfig();
        getGoals().forEach(goal -> goal.addToGeneratedConfig(goalsConfig));
        return new TeamConfig(getCurrentOrder(), goalsConfig, players.stream().map(UUID::toString).toList(), teamName);
    }

    public int getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(int currentOrder) {
        this.currentOrder = currentOrder;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        Team team = (Team) o;
        return Objects.equals(teamName, team.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamName);
    }

    public static void initAllTeam(Context context, int globalCurrentOrder) {
        ALL_TEAM = new Team(context, globalCurrentOrder) {

            @Override
            public boolean isInTeam(UUID uuid) {
                return Bukkit.getPlayer(uuid) != null;
            }

            @Override
            public List<UUID> getPlayers() {
                return Bukkit.getOnlinePlayers().stream().map(Entity::getUniqueId).toList();
            }

            @Override
            public List<BaseGoal> getGoals() {
                return context.challengeManager().getGoals();
            }

            @Override
            public void addPlayer(Player player) {
                throw new RuntimeException("Cannot add players to ALL_TEAM");
            }

            @Override
            public void removePlayer(Player player) {
                throw new RuntimeException("Cannot remove players from ALL_TEAM");
            }
        };
    }

    public static void setCurrentOrderIfNotYetSetToMinOrderValueThatExistsIn(Team team) {
        team.getGoals().stream().filter(BaseGoal::hasTimer).mapToInt(baseGoal -> baseGoal.getTimer().getOrder()).min().ifPresentOrElse(
                team::setCurrentOrder,
                () -> team.setCurrentOrder(-1)
        );
    }

    public static int getGlobalCurrentOrder() {
        Preconditions.checkNotNull(ALL_TEAM,
                                   "Cannot access global current order before any team has been initialized."
        );
        return ALL_TEAM.getCurrentOrder();
    }

    public static void setGlobalCurrentOrder(int value) {
        Preconditions.checkNotNull(ALL_TEAM,
                                   "Cannot access global current order before any team has been initialized."
        );
        ALL_TEAM.setCurrentOrder(value);
    }
}
