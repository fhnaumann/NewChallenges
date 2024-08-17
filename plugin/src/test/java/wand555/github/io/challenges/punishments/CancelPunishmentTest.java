package wand555.github.io.challenges.punishments;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.event.Cancellable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.files.FileManager;
import wand555.github.io.challenges.generated.CancelPunishmentConfig;
import wand555.github.io.challenges.mapping.MaterialDataSource;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.offline_temp.OfflineTempData;
import wand555.github.io.challenges.teams.Team;
import wand555.github.io.challenges.types.Data;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CancelPunishmentTest {

    private static ResourceBundleContext resourceBundleContext;
    private static JsonNode schemaRoot;
    private static DataSourceContext dataSourceContext;

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock causer;
    private Context context;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("punishments",
                                                         Locale.ENGLISH,
                                                         UTF8ResourceBundleControl.get()
        );
        resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.punishmentResourceBundle()).thenReturn(bundle);
        when(resourceBundleContext.miscResourceBundle()).thenReturn(CriteriaUtil.loadMiscResourceBundle());
        schemaRoot = new ObjectMapper().readTree(Challenges.class.getResource("/challenges_schema.json"));
        List<MaterialJSON> materialJSONS = new ObjectMapper().readValue(FileManager.class.getResourceAsStream(
                "/materials.json"), MaterialDataSource.class).getData();
        dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.materialJSONList()).thenReturn(materialJSONS);
    }

    @BeforeEach
    public void setUp() throws IOException {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        causer = server.addPlayer();
        ChallengeManager challengeManager = mock(ChallengeManager.class);
        challengeManager.setContext(context);
        when(challengeManager.getGoals()).thenReturn(List.of());
        Random randomMock = mock(Random.class);
        when(randomMock.nextInt(anyInt(), anyInt())).thenReturn(3);
        context = new Context(plugin,
                              resourceBundleContext,
                              dataSourceContext,
                              schemaRoot,
                              challengeManager,
                              randomMock,
                              new OfflineTempData(plugin)
        );
        Team.initAllTeam(context, -1);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testEventIsCancelled() {
        CancelPunishment cancelPunishment = new CancelPunishment(context, new CancelPunishmentConfig());
        Data<? extends Cancellable,?> data = PunishmentUtil.mockDataWithEvent(causer);
        cancelPunishment.enforcePunishment(data);
        assertTrue(data.event().isCancelled());
    }
}
