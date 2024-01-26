package wand555.github.io.punishments;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.generated.HealthPunishmentConfig;
import wand555.github.io.challenges.mapping.Mapper;
import wand555.github.io.challenges.punishments.HealthPunishment;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.rules.PunishableRule;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class HealthPunishmentTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;
    private ResourceBundle bundle;
    private Mapper mapper;
    private Context context;

    @BeforeEach
    public void setUp() throws IOException {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer();
        bundle = ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get());
        JsonNode schemaRoot = new ObjectMapper().readTree(Challenges.class.getResource("/test-output-schema.json"));
        mapper = new Mapper(plugin, bundle, schemaRoot);
        context = new Context(plugin, bundle, schemaRoot);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testHealthPunishmentSchemaAccess() {
        assertDoesNotThrow(() -> new HealthPunishment(context, Punishment.Affects.CAUSER, 1, false));
    }

    @Test
    public void testCauserHeartsLostHealthPunishment() {
        int heartsLost = 5;
        HealthPunishment heartsLostPunishment = new HealthPunishment(context, Punishment.Affects.CAUSER, heartsLost, false);
        heartsLostPunishment.enforcePunishment(player);
        assertEquals(player.getMaxHealth()-heartsLost, player.getHealth(), 1e-3);

        HealthPunishment randomHeartLostPunishment = new HealthPunishment(context, Punishment.Affects.CAUSER, 1, true);
        randomHeartLostPunishment.enforcePunishment(player);
        // only test that the player lost any health
        assertNotEquals(player.getMaxHealth(), player.getHealth(), 1e-3);
    }

    @Test
    public void testAllHeartsLostHealthPunishment() {
        PlayerMock secondPlayer = server.addPlayer();
        PlayerMock thirdPlayer = server.addPlayer();

        int heartsLost = 5;
        HealthPunishment heartsLostPunishment = new HealthPunishment(context, Punishment.Affects.ALL, heartsLost, false);
        heartsLostPunishment.enforcePunishment(player);
        assertEquals(player.getMaxHealth()-heartsLost, player.getHealth(), 1e-3);
        assertEquals(secondPlayer.getMaxHealth()-heartsLost, secondPlayer.getHealth(), 1e-3);
        assertEquals(thirdPlayer.getMaxHealth()-heartsLost, thirdPlayer.getHealth(), 1e-3);

        HealthPunishment randomHeartLostPunishment = new HealthPunishment(context, Punishment.Affects.ALL, 1, true);
        randomHeartLostPunishment.enforcePunishment(player);
        // only test that the players lost any health
        assertNotEquals(player.getMaxHealth(), player.getHealth(), 1e-3);
        assertNotEquals(secondPlayer.getMaxHealth(), secondPlayer.getHealth(), 1e-3);
        assertNotEquals(thirdPlayer.getMaxHealth(), thirdPlayer.getHealth(), 1e-3);
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
        String emptyHealthPunishment =
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
        assertDoesNotThrow(() -> mapper.mapToGeneratedClasses(emptyHealthPunishment));
        ChallengeManager challengeManager = mapper.mapToGeneratedClasses(emptyHealthPunishment);
        assertEquals(healthPunishment, ((PunishableRule)challengeManager.getRules().get(0)).getPunishments().get(0));
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
        HealthPunishment healthPunishment = new HealthPunishment(context, Punishment.Affects.CAUSER, 5, false);
        assertDoesNotThrow(() -> mapper.mapToGeneratedClasses(complexHealthPunishment));
        ChallengeManager challengeManager = mapper.mapToGeneratedClasses(complexHealthPunishment);
        HealthPunishment actual = (HealthPunishment) ((PunishableRule)challengeManager.getRules().get(0)).getPunishments().get(0);
        assertEquals(healthPunishment, actual);
    }

    @Test
    public void testHealthPunishmentModel2JSON() {
        HealthPunishment healthPunishment = new HealthPunishment(context, Punishment.Affects.ALL, 5, false);
        HealthPunishmentConfig generatedHealthPunishment = healthPunishment.toGeneratedJSONClass();
        assertEquals(new HealthPunishmentConfig(HealthPunishmentConfig.Affects.ALL, 5, false), generatedHealthPunishment);
    }
}
