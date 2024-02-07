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
import wand555.github.io.challenges.punishments.HealthPunishment;
import wand555.github.io.challenges.validation.ValidationResult;
import wand555.github.io.challenges.validation.Validator;
import wand555.github.io.challenges.validation.Violation;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class HealthPunishmentTest {

    private static ResourceBundleContext resourceBundleContext;
    private static JsonNode schemaRoot;

    private static Validator validator;

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

        validator = new Validator(
                HealthPunishmentTest.class.getResourceAsStream("/challenges_schema.json"),
                new File(HealthPunishmentTest.class.getResource("/constraints.sch").getFile())
        );
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

    @Test
    public void testEmptyHealthPunishmentValidator() throws JsonProcessingException {
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
        ValidationResult result = validator.validate(emptyHealthPunishment);
        assertTrue(result.isValid());
    }

    @Test
    public void testHealthAmount() {
        String validHealthAmountHealthPunishment =
                """
                {
                  "rules": {
                    "enabledRules": {
                      "noBlockBreak": {
                        "punishments": {
                          "healthPunishment": {
                            "heartsLost": 2
                          }
                        }
                      }
                    }
                  }
                }
                """;
        ValidationResult result = validator.validate(validHealthAmountHealthPunishment);
        assertTrue(result.isValid());

        String invalidHealthAmountHealthPunishment =
                """
                {
                  "rules": {
                    "enabledRules": {
                      "noBlockBreak": {
                        "punishments": {
                          "healthPunishment": {
                            "heartsLost": 25
                          }
                        }
                      }
                    }
                  }
                }
                """;
        result = validator.validate(invalidHealthAmountHealthPunishment);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
    }

    @Test
    public void testComplexHealthPunishment1() throws JsonProcessingException {
        String complexHealthPunishment =
                """
                {
                  "rules": {
                    "enabledRules": {
                      "noBlockBreak": {
                        "punishments": {
                          "healthPunishment": {
                            "heartsLost": 2,
                            "randomizeHeartsLost": true
                          }
                        }
                      }
                    }
                  }
                }
                """;
        ValidationResult result = validator.validate(complexHealthPunishment);
        assertTrue(result.isValid());
        assertEquals(1, result.getViolations().size());
        assertEquals(Violation.Level.WARNING, result.getViolations().get(0).getLevel());
    }

    @Test
    public void testComplexHealthPunishment2() {
        String complexHealthPunishment =
                """
                {
                  "rules": {
                    "enabledRules": {
                      "noBlockBreak": {
                        "punishments": {
                          "healthPunishment": {
                            "heartsLost": 25,
                            "randomizeHeartsLost": true,
                            "affects": "Causer"
                          }
                        }
                      }
                    }
                  }
                }
                """;
        ValidationResult result = validator.validate(complexHealthPunishment);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        assertEquals(1, result.getViolations().stream().filter(violation -> violation.getLevel() == Violation.Level.ERROR).count());
        assertEquals(1, result.getViolations().stream().filter(violation -> violation.getLevel() == Violation.Level.WARNING).count());
    }
}
