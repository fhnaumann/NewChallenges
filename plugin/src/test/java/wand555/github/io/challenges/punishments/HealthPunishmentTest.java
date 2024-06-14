package wand555.github.io.challenges.punishments;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.attribute.Attribute;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.generated.HealthPunishmentConfig;
import wand555.github.io.challenges.mapping.MaterialDataSource;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.validation.Validation;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public class HealthPunishmentTest {

    private static ResourceBundleContext resourceBundleContext;
    private static JsonNode schemaRoot;
    private static DataSourceContext dataSourceContext;

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;
    private Context context;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("punishments", Locale.ENGLISH, UTF8ResourceBundleControl.get());
        resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.punishmentResourceBundle()).thenReturn(bundle);
        schemaRoot = new ObjectMapper().readTree(Challenges.class.getResource("/challenges_schema.json"));
        List<MaterialJSON> materialJSONS = new ObjectMapper().readValue(FileManager.class.getResourceAsStream("/materials.json"), MaterialDataSource.class).getData();
        dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.materialJSONList()).thenReturn(materialJSONS);
    }

    @BeforeEach
    public void setUp() throws IOException {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer();
        ChallengeManager challengeManager = new ChallengeManager();
        Random randomMock = mock(Random.class);
        when(randomMock.nextInt(anyInt(), anyInt())).thenReturn(3);
        context = new Context(plugin, resourceBundleContext, dataSourceContext, schemaRoot, challengeManager, randomMock, new OfflineTempData(plugin));
        challengeManager.setContext(context);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testCauserHeartsLostHealthPunishment() {
        HealthPunishmentConfig healthPunishmentConfigMock = mock(HealthPunishmentConfig.class);
        int heartsLost = 5;
        when(healthPunishmentConfigMock.getAffects()).thenReturn(HealthPunishmentConfig.Affects.CAUSER);
        when(healthPunishmentConfigMock.getHeartsLost()).thenReturn(heartsLost);
        when(healthPunishmentConfigMock.isRandomizeHeartsLost()).thenReturn(false);
        HealthPunishment heartsLostPunishment = new HealthPunishment(context, healthPunishmentConfigMock);
        heartsLostPunishment.enforcePunishment(player);
        assertEquals(player.getMaxHealth()-heartsLost, player.getHealth(), 1e-3);

        when(healthPunishmentConfigMock.getHeartsLost()).thenReturn(1);
        when(healthPunishmentConfigMock.isRandomizeHeartsLost()).thenReturn(true);
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
        when(healthPunishmentConfigMock.isRandomizeHeartsLost()).thenReturn(false);

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
}
