package wand555.github.io.challenges.punishments;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.ResourceBundleContext;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.generated.RandomEffectPunishmentConfig;
import wand555.github.io.challenges.teams.Team;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class RandomEffectPunishmentTest {

    private static Context context;

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock causer, other;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        context = mock(Context.class);
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.punishmentResourceBundle()).thenReturn(CriteriaUtil.loadPunishmentResourceBundle());
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        when(context.schemaRoot()).thenReturn(CriteriaUtil.loadJSONSchemaStreamAsJSONNode());
        Random random = mock(Random.class);
        when(context.random()).thenReturn(random);
        ChallengeManager manager = mock(ChallengeManager.class);
        when(context.challengeManager()).thenReturn(manager);
        when(manager.getGoals()).thenReturn(List.of());
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        causer = server.addPlayer();
        other = server.addPlayer();

        Team.initAllTeam(context, -1);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testCauserFixedNumberOfRandomEffectPunishment() {
        RandomEffectPunishmentConfig randomEffectPunishmentConfig = new RandomEffectPunishmentConfig(
                RandomEffectPunishmentConfig.Affects.CAUSER,
                1,
                false
        );
        RandomEffectPunishment randomEffectPunishment = new RandomEffectPunishment(context,
                                                                                   randomEffectPunishmentConfig
        );
        randomEffectPunishment.enforcePunishment(causer.getUniqueId());
        // expect the causer to have one random effect
        assertEquals(1, causer.getActivePotionEffects().size());
        assertTrue(other.getActivePotionEffects().isEmpty());
    }

    @Test
    public void testCauserRandomNumberOfRandomEffectPunishment() {
        when(context.random().nextInt(anyInt(), anyInt())).thenReturn(2); // effect amount
        RandomEffectPunishmentConfig randomEffectPunishmentConfig = new RandomEffectPunishmentConfig(
                RandomEffectPunishmentConfig.Affects.CAUSER,
                0,
                true
        );
        RandomEffectPunishment randomEffectPunishment = new RandomEffectPunishment(context,
                                                                                   randomEffectPunishmentConfig
        );
        randomEffectPunishment.enforcePunishment(causer.getUniqueId());
        assertEquals(2, causer.getActivePotionEffects().size());
        assertEquals(0, other.getActivePotionEffects().size());
        //assertEquals(causer.getActivePotionEffects().stream().findFirst().get(), other.getActivePotionEffects().stream().findFirst().get());
    }

    @Test
    public void testAllFixedNumberOfRandomEffectPunishment() {
        RandomEffectPunishmentConfig randomEffectPunishmentConfig = new RandomEffectPunishmentConfig(
                RandomEffectPunishmentConfig.Affects.ALL,
                3,
                false
        );
        RandomEffectPunishment randomEffectPunishment = new RandomEffectPunishment(context,
                                                                                   randomEffectPunishmentConfig
        );
        randomEffectPunishment.enforcePunishment(causer.getUniqueId());
        assertEquals(3, causer.getActivePotionEffects().size());
        assertEquals(3, other.getActivePotionEffects().size());
        assertEquals(causer.getActivePotionEffects(), other.getActivePotionEffects());
    }

    @Test
    public void testAllRandomNumberOfRandomEffectPunishment() {
        when(context.random().nextInt(anyInt(), anyInt())).thenReturn(2); // effect amount
        RandomEffectPunishmentConfig randomEffectPunishmentConfig = new RandomEffectPunishmentConfig(
                RandomEffectPunishmentConfig.Affects.ALL,
                0,
                true
        );
        RandomEffectPunishment randomEffectPunishment = new RandomEffectPunishment(context,
                                                                                   randomEffectPunishmentConfig
        );
        randomEffectPunishment.enforcePunishment(causer.getUniqueId());
        assertEquals(2, causer.getActivePotionEffects().size());
        assertEquals(2, other.getActivePotionEffects().size());
        assertEquals(causer.getActivePotionEffects(), other.getActivePotionEffects());
    }

}
