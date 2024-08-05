package wand555.github.io.challenges.teams;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.GlobalGoalTimerTest;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.GoalCompletion;
import wand555.github.io.challenges.generated.TeamConfig;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TeamTest {

    private Context context;
    private ChallengeManager manager;

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player, other;

    @BeforeAll
    public static void setUpIOData() throws IOException {

    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        context = mock(Context.class);
        manager = mock(ChallengeManager.class);
        when(context.challengeManager()).thenReturn(manager);
        when(manager.getGoals()).thenReturn(List.of());
        when(context.plugin()).thenReturn(plugin);
        player = server.addPlayer();
        other = server.addPlayer();

        Team.initAllTeam(context, -1);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testIfTeamsExistPlayerIsInSpecificTeam() {
        List<Team> fakeTeams = fakeTeams(List.of(GlobalGoalTimerTest.blockBreakGoalMock(1)), new TeamStub("1", List.of(player), 1));
        when(manager.getTeams()).thenReturn(fakeTeams);
        Team actual = Team.getTeamPlayerIn(context, player.getUniqueId());
        assertEquals(fakeTeams.get(0), actual);
    }

    @Test
    public void testIfNoTeamsExistPlayerIsInAllTeam() {
        when(manager.getTeams()).thenReturn(List.of());
        Team actual = Team.getTeamPlayerIn(context, player.getUniqueId());
        assertEquals(Team.ALL_TEAM, actual);
    }

    @Test
    public void testCurrentOrderIsSetToMinimumValueFromGoalsInSpecificTeam() {
        List<Team> fakeTeams = fakeTeams(List.of(GlobalGoalTimerTest.blockBreakGoalMock(1), GlobalGoalTimerTest.itemGoalMock(2)), new TeamStub("1", List.of(player), 1));
        when(manager.getTeams()).thenReturn(fakeTeams);
        Team.setCurrentOrderIfNotYetSetToMinOrderValueThatExistsIn(fakeTeams.getFirst());
        assertEquals(1, fakeTeams.getFirst().getCurrentOrder());
    }

    @Test
    @DisplayName("When a goal is completed, the current order should not increment if there any goals that are not completed with the current order number")
    public void testOnGoalCompleteTimerBeatenNotAllGoalsWithCurrentOrderCompleteCurrentOrderNotIncremented() {
        BaseGoal blockBreak1 = GlobalGoalTimerTest.blockBreakGoalMock(1);
        BaseGoal item1 = GlobalGoalTimerTest.itemGoalMock(1);
        List<Team> fakeTeams = fakeTeams(List.of(blockBreak1, item1, GlobalGoalTimerTest.itemGoalMock(2)), new TeamStub("1", List.of(player), 1));
        when(manager.getTeams()).thenReturn(fakeTeams);

        when(blockBreak1.isComplete()).thenReturn(true);
        when(item1.isComplete()).thenReturn(false);

        Team.onGoalComplete(context, player, GoalCompletion.TIMER_BEATEN);

        assertEquals(1, fakeTeams.getFirst().getCurrentOrder());
    }

    @Test
    public void testOnGoalCompleteTimerBeatenAllGoalsWithCurrentOrderCompleteCurrentOrderIsIncremented() {
        BaseGoal blockBreak1 = GlobalGoalTimerTest.blockBreakGoalMock(1);
        BaseGoal item1 = GlobalGoalTimerTest.itemGoalMock(1);
        List<Team> fakeTeams = fakeTeams(List.of(blockBreak1, item1, GlobalGoalTimerTest.itemGoalMock(2)), new TeamStub("1", List.of(player), 1));
        when(manager.getTeams()).thenReturn(fakeTeams);

        when(blockBreak1.isComplete()).thenReturn(true);
        when(item1.isComplete()).thenReturn(true);

        Team.onGoalComplete(context, player, GoalCompletion.TIMER_BEATEN);

        assertEquals(2, fakeTeams.getFirst().getCurrentOrder());
    }

    @Test
    public void testOnGoalCompleteTimerBeatenAllGoalsWithCurrentOrderCompleteNextGoalsAreStarted() {
        BaseGoal blockBreak1 = GlobalGoalTimerTest.blockBreakGoalMock(1);
        BaseGoal item1 = GlobalGoalTimerTest.itemGoalMock(1);
        BaseGoal item2 = GlobalGoalTimerTest.itemGoalMock(2);
        List<Team> fakeTeams = fakeTeams(List.of(blockBreak1, item1, item2), new TeamStub("1", List.of(player), 1));
        when(manager.getTeams()).thenReturn(fakeTeams);

        when(blockBreak1.isComplete()).thenReturn(true);
        when(item1.isComplete()).thenReturn(true);

        Team.onGoalComplete(context, player, GoalCompletion.TIMER_BEATEN);

        verify(item2).onStart(fakeTeams.getFirst());
    }

    @Test
    public void testOnGoalCompleteAllGoalsCompleteChallengeIsEnded() {
        BaseGoal blockBreak1 = GlobalGoalTimerTest.blockBreakGoalMock(1);
        BaseGoal item1 = GlobalGoalTimerTest.itemGoalMock(1);
        List<Team> fakeTeams = fakeTeams(List.of(blockBreak1, item1), new TeamStub("1", List.of(player), 1));
        when(manager.getTeams()).thenReturn(fakeTeams);

        when(blockBreak1.isComplete()).thenReturn(true);
        when(item1.isComplete()).thenReturn(true);

        Team.onGoalComplete(context, player, GoalCompletion.TIMER_BEATEN);

        verify(manager).endChallenge(true, fakeTeams.getFirst());
    }

    public static List<Team> fakeTeams(List<BaseGoal> goals, TeamStub... teams) {
        return Arrays.stream(teams)
                .map(teamStub -> mockTeam(teamStub.teamName, goals, teamStub.players, teamStub.currentOrder))
                .toList();
    }

    public static Team mockTeam(String teamName, List<BaseGoal> goals, List<Player> players, int currentOrder) {
        return new Team(teamName, goals, players, currentOrder);
    }

    public record TeamStub(String teamName, List<Player> players, int currentOrder) {}
}
