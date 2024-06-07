package wand555.github.io.challenges;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.GameMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakCollectedInventory;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoal;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoalMessageHelper;
import wand555.github.io.challenges.criteria.goals.itemgoal.ItemGoal;
import wand555.github.io.challenges.generated.BlockBreakGoalConfig;
import wand555.github.io.challenges.generated.GoalTimer;

import java.util.List;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChallengeManagerTest {

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
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        player = server.addPlayer("dummy");


        manager = spy(new ChallengeManager());
        manager.setGoals(List.of(blockBreakGoalMock(1)));
        manager.setContext(context);
        manager.setGameState(ChallengeManager.GameState.RUNNING);
        TimerRunnable timerRunnable = new TimerRunnable(context, 0L);
        timerRunnable.start();
        manager.setTimerRunnable(timerRunnable);
    }

    @Test
    public void testCurrentOrderNumber() {
        assertEquals(1, manager.getCurrentOrder());
        manager.setGoals(List.of(blockBreakGoalMock(1), itemGoalMock(2)));
        assertEquals(1, manager.getCurrentOrder());
    }

    @Test
    public void testOnGoalCompletedTimerBeatenOrderNumberIncreaseToNextNumber() {
        BlockBreakGoal blockBreakGoal = blockBreakGoalMock(1);
        ItemGoal itemGoal = itemGoalMock(2);
        when(blockBreakGoal.isComplete()).thenReturn(true);
        manager.setGoals(List.of(blockBreakGoal, itemGoal));
        manager.onGoalCompleted(ChallengeManager.GoalCompletion.TIMER_BEATEN);
        assertEquals(manager.getCurrentOrder(), 2);
        verify(manager, never()).endChallenge(anyBoolean());

        when(itemGoal.isComplete()).thenReturn(true);
        manager.onGoalCompleted(ChallengeManager.GoalCompletion.TIMER_BEATEN);
        verify(manager, times(1)).endChallenge(true);
    }

    @Test
    public void testEndChallengeSuccess() {
        manager.endChallenge(true);
        server.getOnlinePlayers().forEach(playerMock -> {
            playerMock.assertGameMode(GameMode.SPECTATOR);
            assertTrue(playerMock.getBossBars().isEmpty());
        });
        assertEquals(manager.getGameState(), ChallengeManager.GameState.ENDED);

    }

    private static BlockBreakGoal blockBreakGoalMock(int order) {
        BlockBreakGoal blockBreakGoal = mock(BlockBreakGoal.class);
        when(blockBreakGoal.getTimer()).thenReturn(new Timer(new GoalTimer(180, 180, order, -1,-1)));
        when(blockBreakGoal.hasTimer()).thenReturn(true);
        return blockBreakGoal;
    }

    private static ItemGoal itemGoalMock(int order) {
        ItemGoal itemGoal = mock(ItemGoal.class);
        when(itemGoal.getTimer()).thenReturn(new Timer(new GoalTimer(180, 180, order, -1,-1)));
        when(itemGoal.hasTimer()).thenReturn(true);
        return itemGoal;
    }
}
