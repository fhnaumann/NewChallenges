package wand555.github.io.challenges.punishments;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalMatchers;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.generated.MLGPunishmentConfig;
import wand555.github.io.challenges.mlg.MLGHandler;
import wand555.github.io.challenges.offline_temp.OfflinePlayerData;

import static org.mockito.Mockito.*;

public class MLGPunishmentTest {

    private static ResourceBundleContext resourceBundleContext;

    private Context context;
    private MLGHandler mlgHandler;
    private ChallengeManager challengeManager;
    private ServerMock server;
    private Challenges plugin;
    private PlayerMock causer;
    private PlayerMock otherPlayer;
    private World mlgWorld;

    @BeforeAll
    public static void setUpIOData() {
        resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.punishmentResourceBundle()).thenReturn(CriteriaUtil.loadPunishmentResourceBundle());
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        context = mock(Context.class);
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        when(context.plugin()).thenReturn(plugin);
        challengeManager = mock(ChallengeManager.class);
        when(context.challengeManager()).thenReturn(challengeManager);
        causer = server.addPlayer();
        otherPlayer = server.addPlayer();
        mlgWorld = server.addSimpleWorld("mlgWorld");
        causer.setLocation(new Location(mlgWorld, 0, 0, 0));
        otherPlayer.setLocation(new Location(mlgWorld, 0, 0, 0));
        mlgHandler = spy(new MLGHandler(context, mock(OfflinePlayerData.class)));

    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testCauserIsPunishmentStartedToMLGWorld() {
        MLGPunishmentConfig config = new MLGPunishmentConfig(MLGPunishmentConfig.Affects.CAUSER, 50);
        MLGPunishment mlgPunishment = new MLGPunishment(context, config, mlgHandler);
        mlgPunishment.enforcePunishment(causer);
        verify(mlgHandler).newMLGScenarioFor(eq(causer), eq(50), any());
    }

    @Test
    public void testOtherPlayerIsNotPunishedWhenAffectedIsCauser() {
        MLGPunishmentConfig config = new MLGPunishmentConfig(MLGPunishmentConfig.Affects.CAUSER, 50);
        MLGPunishment mlgPunishment = new MLGPunishment(context, config, mlgHandler);
        mlgPunishment.enforcePunishment(causer);
        verify(mlgHandler, never()).newMLGScenarioFor(eq(otherPlayer), anyInt(), any());
    }

    @Test
    public void testAllPlayersArePunishedWhenAffectedIsAll() {
        MLGPunishmentConfig config = new MLGPunishmentConfig(MLGPunishmentConfig.Affects.ALL, 50);
        MLGPunishment mlgPunishment = new MLGPunishment(context, config, mlgHandler);
        mlgPunishment.enforcePunishment(causer);
        verify(mlgHandler).newMLGScenarioFor(eq(causer), eq(50), any());
        verify(mlgHandler).newMLGScenarioFor(eq(otherPlayer), eq(50), any());
    }

    @Test
    public void testCauserChallengeOverWhenMLGFailed() {
        MLGPunishmentConfig config = new MLGPunishmentConfig(MLGPunishmentConfig.Affects.CAUSER, 50);
        MLGPunishment mlgPunishment = new MLGPunishment(context, config, mlgHandler);
        mlgPunishment.enforcePunishment(causer);
        MLGHandlerTest.simulateMLGFailHitGround(server, causer);
        causer.assertGameMode(GameMode.SPECTATOR);
    }

    @Test
    public void testAllChallengeOverWhenAnyMLGFailed() {
        MLGPunishmentConfig config = new MLGPunishmentConfig(MLGPunishmentConfig.Affects.ALL, 50);
        MLGPunishment mlgPunishment = new MLGPunishment(context, config, mlgHandler);
        mlgPunishment.enforcePunishment(causer);
        MLGHandlerTest.simulateMLGFailHitGround(server, causer);
        MLGHandlerTest.simulateMLGSuccess(server, mlgWorld, otherPlayer);
        server.getScheduler().performOneTick();
        verify(challengeManager).endChallenge(eq(false));
    }

    @Test
    public void testAllChallengeOverNotUntilAllMLGsComplete() {
        MLGPunishmentConfig config = new MLGPunishmentConfig(MLGPunishmentConfig.Affects.ALL, 50);
        MLGPunishment mlgPunishment = new MLGPunishment(context, config, mlgHandler);
        mlgPunishment.enforcePunishment(causer);
        MLGHandlerTest.simulateMLGFailHitGround(server, causer);
        verify(challengeManager, never()).endChallenge(anyBoolean());
    }
}
