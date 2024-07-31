package wand555.github.io.challenges;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Goal;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoal;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarHelper;
import wand555.github.io.challenges.generated.GoalTimer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TimerRunnableGoalTimerTest {

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private Context context;
    private ChallengeManager manager;
    private TimerRunnable timerRunnable;

    private BaseGoal goalFirstOrder1 = blockBreakGoalMock(1);
    private BaseGoal goalFirstOrder2 = blockBreakGoalMock(1);
    private BaseGoal goalSecondOrder1 = blockBreakGoalMock(2);
    private BaseGoal goalNoOrder = blockBreakGoalNoTimerMock();

    @BeforeEach
    public void setUp() {
        context = mock(Context.class);
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        plugin.urlReminder = null;
        player = server.addPlayer("dummy");


        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.miscResourceBundle()).thenReturn(CriteriaUtil.loadMiscResourceBundle());
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        manager = mock(ChallengeManager.class);
        when(manager.getGoals()).thenReturn(fakeGoals());
        when(manager.getCurrentOrder()).thenReturn(1);
        when(manager.isRunning()).thenReturn(true);

        when(context.challengeManager()).thenReturn(manager);

        timerRunnable = new TimerRunnable(context, 0L);
        timerRunnable.start();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testDecrementTimeInTimer() {
        when(manager.goalsWithSameOrderNumber()).thenReturn(List.of(goalFirstOrder1, goalFirstOrder2));
        server.getScheduler().performTicks(20);

        assertEquals(179, goalFirstOrder1.getTimer().getTime());
        assertEquals(179, goalFirstOrder2.getTimer().getTime());

    }

    @Test
    public void testNoDecrementTimeInTimerWithDifferentOrderNumber() {
        when(manager.goalsWithSameOrderNumber()).thenReturn(List.of(goalFirstOrder1, goalFirstOrder2));
        server.getScheduler().performTicks(20);
        assertEquals(180, goalSecondOrder1.getTimer().getTime());
    }

    @Test
    public void testUpdateBossBarEverySecond() {
        when(manager.goalsWithSameOrderNumber()).thenReturn(List.of(goalFirstOrder1, goalFirstOrder2));
        server.getScheduler().performTicks(20);

        verify(goalFirstOrder1.getBossBarHelper(), times(1)).updateBossBar();
        verify(goalFirstOrder2.getBossBarHelper(), times(1)).updateBossBar();
    }

    @Test
    public void testFailedChallengeDueTimeLimit() {
        when(manager.goalsWithSameOrderNumber()).thenReturn(List.of(goalFirstOrder1, goalFirstOrder2));
        when(goalFirstOrder1.getTimer()).thenReturn(new Timer(new GoalTimer(180, 180, 1, 180, 0)));
        server.getScheduler().performTicks(20);

        verify(manager, times(1)).endChallenge(false);
    }

    private List<BaseGoal> fakeGoals() {
        return List.of(goalFirstOrder1, goalFirstOrder2, goalSecondOrder1, goalNoOrder);
    }

    private static BlockBreakGoal blockBreakGoalMock(int order) {
        BlockBreakGoal blockBreakGoal = mock(BlockBreakGoal.class);
        when(blockBreakGoal.getTimer()).thenReturn(new Timer(new GoalTimer(180, 180, order, -1, -1)));
        when(blockBreakGoal.hasTimer()).thenReturn(true);
        when(blockBreakGoal.isComplete()).thenReturn(false);
        BossBarHelper bossBarHelper = mock(BossBarHelper.class);
        when(blockBreakGoal.getBossBarHelper()).thenReturn(bossBarHelper);
        return blockBreakGoal;
    }

    private static BlockBreakGoal blockBreakGoalNoTimerMock() {
        BlockBreakGoal blockBreakGoal = mock(BlockBreakGoal.class);
        when(blockBreakGoal.hasTimer()).thenReturn(false);
        when(blockBreakGoal.isComplete()).thenReturn(true);
        return blockBreakGoal;
    }
}
