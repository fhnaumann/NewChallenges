package wand555.github.io.challenges.punishments;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.GameMode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.ResourceBundleContext;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoalMessageHelper;
import wand555.github.io.challenges.generated.EndPunishmentConfig;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class EndPunishmentTest {

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock causer;
    private PlayerMock other;

    private static Context context;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.punishmentResourceBundle()).thenReturn(CriteriaUtil.loadPunishmentResourceBundle());
        DataSourceContext dataSourceContext = mock(DataSourceContext.class);
        ChallengeManager manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);

        context = mock(Context.class);
        when(context.dataSourceContext()).thenReturn(dataSourceContext);
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        when(context.challengeManager()).thenReturn(manager);
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        causer = server.addPlayer();
        other = server.addPlayer();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testCauserEndPunishment() {
        EndPunishmentConfig endPunishmentConfig = new EndPunishmentConfig(EndPunishmentConfig.Affects.CAUSER);
        EndPunishment endPunishment = new EndPunishment(context, endPunishmentConfig);
        endPunishment.enforcePunishment(causer);
        verify(context.challengeManager(), never()).endChallenge(true);
        causer.assertGameMode(GameMode.SPECTATOR);
        other.assertGameMode(GameMode.SURVIVAL);
    }

    @Test
    public void testAllEndPunishment() {
        EndPunishmentConfig endPunishmentConfig = new EndPunishmentConfig(EndPunishmentConfig.Affects.ALL);
        EndPunishment endPunishment = new EndPunishment(context, endPunishmentConfig);
        endPunishment.enforcePunishment(causer);
        verify(context.challengeManager()).endChallenge(true);
    }
}
