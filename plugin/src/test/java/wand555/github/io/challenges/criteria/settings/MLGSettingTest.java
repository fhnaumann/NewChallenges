package wand555.github.io.challenges.criteria.settings;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.generated.MLGSettingConfig;
import wand555.github.io.challenges.mlg.MLGHandler;
import wand555.github.io.challenges.offline_temp.OfflinePlayerData;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MLGSettingTest {
    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private Context context;
    private ChallengeManager manager;

    private MLGHandler mlgHandler;
    private MLGSetting mlgSetting;
    private WorldMock mlgWorld;

    private static final int TIME_TO_FIRST_MLG = 90;
    private static final long TICKS_TO_FIRST_MLG = TIME_TO_FIRST_MLG * 20L;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        World defaultWorld = server.addSimpleWorld("world");
        player = server.addPlayer();
        player.setLocation(new Location(defaultWorld, 0, 0, 0));
        mlgWorld = server.addSimpleWorld(ConfigValues.MLG_WORLD.getValueOrDefault(plugin));
        context = mock(Context.class);
        when(context.plugin()).thenReturn(plugin);
        manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);
        when(context.challengeManager()).thenReturn(manager);
        Random randomMock = mock(Random.class);
        when(randomMock.nextInt(anyInt(), anyInt())).thenReturn(TIME_TO_FIRST_MLG);
        when(context.random()).thenReturn(randomMock);
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.punishmentResourceBundle()).thenReturn(CriteriaUtil.loadPunishmentResourceBundle());
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        mlgHandler = spy(new MLGHandler(context, mock(OfflinePlayerData.class)));
        mlgSetting = new MLGSetting(context, new MLGSettingConfig(30, 120, 60), mlgHandler);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testMLGWorldCreatedIfNotAlreadyExistWhenStarted() {
        mlgSetting.onStart();
        assertNotNull(Bukkit.getWorld(ConfigValues.MLG_WORLD.getValueOrDefault(plugin)));
    }

    @Test
    public void testTimerIsScheduledWhenStarted() {
        mlgSetting.onStart();
        assertTrue(Bukkit.getScheduler().isQueued(mlgSetting.getTaskID()));
    }

    @Test
    public void testTimerIsCancelledWhenPaused() {
        mlgSetting.onStart();
        server.getScheduler().performOneTick();
        mlgSetting.onPause();
        server.getScheduler().performOneTick();
        assertFalse(Bukkit.getScheduler().isQueued(mlgSetting.getTaskID()));
    }

    @Test
    public void testTimerIsScheduledWhenResumed() {
        mlgSetting.onResume();
        assertTrue(Bukkit.getScheduler().isQueued(mlgSetting.getTaskID()));
    }

    @Test
    public void testTimerIsCancelledWhenEnded() {
        mlgSetting.onStart();
        server.getScheduler().performOneTick();
        mlgSetting.onEnd();
        server.getScheduler().performOneTick();
        assertFalse(Bukkit.getScheduler().isQueued(mlgSetting.getTaskID()));
    }

    @Test
    public void testMLGScenarioBeginsAfterTime() {
        mlgSetting.onStart();
        server.getScheduler().performTicks(TICKS_TO_FIRST_MLG);
        verify(mlgHandler).newMLGScenarioFor(eq(player), eq(30), any());
    }

    @Test
    public void testNewTimerIsScheduledAfterMLGWasCompleted() {
        mlgSetting.onStart();
        server.getScheduler().performTicks(TICKS_TO_FIRST_MLG);
        MLGHandlerTest.simulateMLGSuccess(server, mlgWorld, player);
        server.getScheduler().performOneTick();
        assertTrue(Bukkit.getScheduler().isQueued(mlgSetting.getTaskID()));
    }

    @Test
    public void testNewTimerIsNotScheduledAfterMLGWasFailedAndChallengeEnded() {
        mlgSetting.onStart();
        server.getScheduler().performTicks(TICKS_TO_FIRST_MLG);
        MLGHandlerTest.simulateMLGFailHitGround(server, player);
        server.getScheduler().performOneTick();
        assertFalse(Bukkit.getScheduler().isQueued(mlgSetting.getTaskID()));
    }

}
