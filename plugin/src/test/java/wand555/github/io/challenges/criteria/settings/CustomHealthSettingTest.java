package wand555.github.io.challenges.criteria.settings;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.generated.CustomHealthSettingConfig;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomHealthSettingTest {

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private Context context;
    private ChallengeManager manager;
    private CustomHealthSetting customHealthSetting30;
    private final int hearts = 30;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        context = mock(Context.class);
        when(context.plugin()).thenReturn(plugin);
        manager = mock(ChallengeManager.class);

        when(context.challengeManager()).thenReturn(manager);
        player = server.addPlayer("dummy");

        CustomHealthSettingConfig config = new CustomHealthSettingConfig(hearts);
        customHealthSetting30 = new CustomHealthSetting(context, config);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testCustomHealthNotAppliedInSetup() {
        when(manager.isSetup()).thenReturn(true);
        assertNotEquals(hearts, player.getMaxHealth());
    }

    @Test
    public void testCustomHealthAppliedWhenStarted() {
        when(manager.isSetup()).thenReturn(false);
        customHealthSetting30.onStart();
        assertEquals(hearts, getMaxHealth(player));
    }

    @Test
    public void testCustomHealthRemainSameWhenPaused() {
        when(manager.isPaused()).thenReturn(true);
        when(manager.getGameState()).thenReturn(ChallengeManager.GameState.PAUSED);
        customHealthSetting30.onStart();
        assertEquals(hearts, getMaxHealth(player));
    }

    @Test
    public void testCustomHealthAppliedAllPlayers() {
        when(manager.isSetup()).thenReturn(true); // adding players triggers join event

        Player player2 = server.addPlayer();
        Player player3 = server.addPlayer();

        when(manager.isSetup()).thenReturn(false);
        customHealthSetting30.onStart();
        assertEquals(hearts, getMaxHealth(player));
        assertEquals(hearts, getMaxHealth(player2));
        assertEquals(hearts, getMaxHealth(player3));
    }

    @Test
    public void testCustomHealthAppliedWhenPlayerJoinsRunningChallenge() {
        when(manager.isSetup()).thenReturn(false);
        when(manager.isRunning()).thenReturn(true);
        Player player2 = server.addPlayer();
        assertEquals(hearts, getMaxHealth(player2));
    }

    @Test
    public void testCustomHealthAppliedWhenPlayerJoinsPausedChallenge() {
        when(manager.isSetup()).thenReturn(false);
        when(manager.isPaused()).thenReturn(true);
        Player player2 = server.addPlayer();
        assertEquals(hearts, getMaxHealth(player2));
    }

    @Test
    public void testCustomHealthNotAppliedWhenPlayerJoinsInSetup() {
        when(manager.isSetup()).thenReturn(true);
        Player player2 = server.addPlayer();
        assertNotEquals(hearts, getMaxHealth(player2));
    }

    private static double getMaxHealth(Player player) {
        return Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
    }
}
