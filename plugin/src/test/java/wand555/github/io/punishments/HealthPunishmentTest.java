package wand555.github.io.punishments;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.attribute.Attribute;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.ResourceBundleContext;
import wand555.github.io.challenges.generated.HealthPunishmentConfig;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.punishments.HealthPunishment;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.rules.PunishableRule;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class HealthPunishmentTest {

    private static ResourceBundleContext resourceBundleContext;
    private static JsonNode schemaRoot;

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;
    private Context context;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("punishments", Locale.ENGLISH, UTF8ResourceBundleControl.get());
        resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.punishmentResourceBundle()).thenReturn(bundle);
        schemaRoot = new ObjectMapper().readTree(Challenges.class.getResource("/test-output-schema.json"));
    }

    @BeforeEach
    public void setUp() throws IOException {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer();
        ChallengeManager challengeManager = new ChallengeManager();
        context = new Context(plugin, resourceBundleContext, schemaRoot, challengeManager);
        challengeManager.setContext(context);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testHealthPunishmentSchemaAccess() {
        HealthPunishmentConfig healthPunishmentConfigMock = mock(HealthPunishmentConfig.class);
        when(healthPunishmentConfigMock.getAffects()).thenReturn(HealthPunishmentConfig.Affects.CAUSER);
        when(healthPunishmentConfigMock.getHeartsLost()).thenReturn(1);
        when(healthPunishmentConfigMock.getRandomizeHeartsLost()).thenReturn(false);
        assertDoesNotThrow(() -> new HealthPunishment(context, healthPunishmentConfigMock));
    }

    @Test
    public void testCauserHeartsLostHealthPunishment() {
        HealthPunishmentConfig healthPunishmentConfigMock = mock(HealthPunishmentConfig.class);
        int heartsLost = 5;
        when(healthPunishmentConfigMock.getAffects()).thenReturn(HealthPunishmentConfig.Affects.CAUSER);
        when(healthPunishmentConfigMock.getHeartsLost()).thenReturn(heartsLost);
        when(healthPunishmentConfigMock.getRandomizeHeartsLost()).thenReturn(false);
        HealthPunishment heartsLostPunishment = new HealthPunishment(context, healthPunishmentConfigMock);
        heartsLostPunishment.enforcePunishment(player);
        assertEquals(player.getMaxHealth()-heartsLost, player.getHealth(), 1e-3);

        when(healthPunishmentConfigMock.getHeartsLost()).thenReturn(1);
        when(healthPunishmentConfigMock.getRandomizeHeartsLost()).thenReturn(true);
        HealthPunishment randomHeartLostPunishment = new HealthPunishment(context, healthPunishmentConfigMock);
        randomHeartLostPunishment.enforcePunishment(player);
        // only test that the player lost any health
        assertNotEquals(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue(), player.getHealth(), 1e-3);
    }

    @Test
    public void testAllHeartsLostHealthPunishment() {
        PlayerMock secondPlayer = server.addPlayer();
        PlayerMock thirdPlayer = server.addPlayer();

        HealthPunishmentConfig healthPunishmentConfigMock = mock(HealthPunishmentConfig.class);
        int heartsLost = 5;
        when(healthPunishmentConfigMock.getAffects()).thenReturn(HealthPunishmentConfig.Affects.ALL);
        when(healthPunishmentConfigMock.getHeartsLost()).thenReturn(heartsLost);
        when(healthPunishmentConfigMock.getRandomizeHeartsLost()).thenReturn(false);

        HealthPunishment heartsLostPunishment = new HealthPunishment(context, healthPunishmentConfigMock);
        heartsLostPunishment.enforcePunishment(player);
        assertEquals(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()-heartsLost, player.getHealth(), 1e-3);
        assertEquals(secondPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()-heartsLost, secondPlayer.getHealth(), 1e-3);
        assertEquals(thirdPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()-heartsLost, thirdPlayer.getHealth(), 1e-3);


        when(healthPunishmentConfigMock.getHeartsLost()).thenReturn(1);
        HealthPunishment randomHeartLostPunishment = new HealthPunishment(context, healthPunishmentConfigMock);
        randomHeartLostPunishment.enforcePunishment(player);
        // only test that the players lost any health
        assertNotEquals(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue(), player.getHealth(), 1e-3);
        assertNotEquals(secondPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue(), secondPlayer.getHealth(), 1e-3);
        assertNotEquals(thirdPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue(), thirdPlayer.getHealth(), 1e-3);
    }

    /**
     * Tests if an empty health punishment json object is correctly parsed into {@link HealthPunishment}.
     * Punishments cannot exist on their own, they have to be within a rule.
     * Therefore, a single {@link wand555.github.io.challenges.rules.NoBlockBreakRule} is used so the health punishment may exist.
     *
     * @throws JsonProcessingException never
     */
    @Test
    public void testEmptyHealthPunishmentJSON2Model() throws JsonProcessingException {
/*        String emptyHealthPunishment =
                """
                {
                  "rules": {
                    "enabledRules": {
                      "noBlockBreak": {
                        "punishments": {
                          "healthPunishment": {
                           
                          }
                        }
                      }
                    }
                  }
                }
                """;
        HealthPunishmentConfig defaultHealthPunishmentValues = new HealthPunishmentConfig();
        HealthPunishment healthPunishment = new HealthPunishment(context, Punishment.Affects.fromJSONString(defaultHealthPunishmentValues.getAffects().value()), defaultHealthPunishmentValues.getHeartsLost(), defaultHealthPunishmentValues.getRandomizeHeartsLost());
        //assertDoesNotThrow(() -> mapper.map2ModelClasses(emptyHealthPunishment));
        //ChallengeManager challengeManager = mapper.map2ModelClasses(emptyHealthPunishment);
        //assertEquals(healthPunishment, ((PunishableRule)challengeManager.getRules().get(0)).getPunishments().get(0));*/
    }

    @Test
    public void testComplexHealthPunishmentJSON2Model() throws JsonProcessingException {
        String complexHealthPunishment =
                """
                {
                  "rules": {
                    "enabledRules": {
                      "noBlockBreak": {
                        "punishments": {
                          "healthPunishment": {
                            "affects": "Causer",
                            "heartsLost": 5,
                            "randomizeHeartsLost": false
                          }
                        }
                      }
                    }
                  }
                }
                """;
        //HealthPunishment healthPunishment = new HealthPunishment(context, Punishment.Affects.CAUSER, 5, false);
        //assertDoesNotThrow(() -> mapper.map2ModelClasses(complexHealthPunishment));
        //ChallengeManager challengeManager = mapper.map2ModelClasses(complexHealthPunishment);
        //HealthPunishment actual = (HealthPunishment) ((PunishableRule)challengeManager.getRules().get(0)).getPunishments().get(0);
        //assertEquals(healthPunishment, actual);
    }

    @Test
    public void testHealthPunishmentModel2JSON() {
/*        System.out.println("context is: " + context);
        HealthPunishment healthPunishment = new HealthPunishment(context, Punishment.Affects.ALL, 5, false);
        HealthPunishmentConfig generatedHealthPunishment = healthPunishment.toGeneratedJSONClass();
        assertEquals(new HealthPunishmentConfig(HealthPunishmentConfig.Affects.ALL, 5, false), generatedHealthPunishment);
    */
    }
}
