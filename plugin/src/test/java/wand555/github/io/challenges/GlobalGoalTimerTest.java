package wand555.github.io.challenges;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.GameMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.GoalCompletion;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoal;
import wand555.github.io.challenges.criteria.goals.itemgoal.ItemGoal;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoal;
import wand555.github.io.challenges.generated.GoalTimer;
import wand555.github.io.challenges.live.EventProvider;
import wand555.github.io.challenges.live.LiveService;
import wand555.github.io.challenges.offline_temp.OfflineTempData;
import wand555.github.io.challenges.teams.Team;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalGoalTimerTest {

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private Context context;
    private ChallengeManager manager;

    @BeforeEach
    public void setUp() {
        context = mock(Context.class);
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.miscResourceBundle()).thenReturn(CriteriaUtil.loadMiscResourceBundle());
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        LiveService liveService = mock(LiveService.class);
        when(liveService.eventProvider()).thenReturn(mock(EventProvider.class));
        when(context.liveService()).thenReturn(liveService);
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        when(context.offlineTempData()).thenReturn(new OfflineTempData(plugin));
        player = server.addPlayer("dummy");

        Team.initAllTeam(context, 1);

        manager = spy(new ChallengeManager());
        manager.setGoals(List.of(blockBreakGoalMock(1)));
        manager.setSettings(List.of());
        manager.setRules(List.of());
        manager.setGlobalPunishments(List.of());
        manager.setContext(context);
        manager.setGameState(ChallengeManager.GameState.RUNNING);
        TimerRunnable timerRunnable = new TimerRunnable(context, 0L);
        timerRunnable.start();
        manager.setTimerRunnable(timerRunnable);
        manager.setTeams(List.of());

        when(context.challengeManager()).thenReturn(manager);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testCurrentOrderNumber() {
        assertEquals(1, Team.getGlobalCurrentOrder());
        manager.setGoals(List.of(blockBreakGoalMock(1), itemGoalMock(2)));
        assertEquals(1, Team.getGlobalCurrentOrder());
    }

    @Test
    public void testEndChallengeSuccess() {
        manager.endChallenge(true);
        server.getOnlinePlayers().forEach(playerMock -> {
            playerMock.assertGameMode(GameMode.SPECTATOR);
            assertTrue(playerMock.getBossBars().isEmpty());
        });
        assertEquals(ChallengeManager.GameState.ENDED, manager.getGameState());
    }

    @Test
    public void testGoalWithNoTimeLimitNoCurrentNumberChange() {
        BlockBreakGoal firstGoal = blockBreakGoalMock(1);
        ItemGoal firstGoal2 = itemGoalMock(1);
        BlockBreakGoal secondGoal = blockBreakGoalMock(2);
        MobGoal noTimerGoal = mock(MobGoal.class);
        when(noTimerGoal.hasTimer()).thenReturn(false);
        when(noTimerGoal.isComplete()).thenReturn(true);
        manager.setGoals(List.of(firstGoal, firstGoal2, secondGoal, noTimerGoal));
        Team.onGoalComplete(context, player, GoalCompletion.COMPLETED);
        assertEquals(1, Team.getGlobalCurrentOrder());
    }



    public static BlockBreakGoal blockBreakGoalMock(int order) {
        BlockBreakGoal blockBreakGoal = mock(BlockBreakGoal.class);
        when(blockBreakGoal.getTimer()).thenReturn(new Timer(new GoalTimer(180, 180, order, -1, -1)));
        when(blockBreakGoal.hasTimer()).thenReturn(true);
        when(blockBreakGoal.isComplete()).thenReturn(true);
        return blockBreakGoal;
    }

    public static ItemGoal itemGoalMock(int order) {
        ItemGoal itemGoal = mock(ItemGoal.class);
        when(itemGoal.getTimer()).thenReturn(new Timer(new GoalTimer(180, 180, order, -1, -1)));
        when(itemGoal.hasTimer()).thenReturn(true);
        when(itemGoal.isComplete()).thenReturn(true);
        return itemGoal;
    }
}
