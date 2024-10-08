package wand555.github.io.challenges;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.files.FileManager;
import wand555.github.io.challenges.mapping.*;
import wand555.github.io.challenges.criteria.rules.noblockbreak.BlockBreakRule;
import wand555.github.io.challenges.offline_temp.OfflineTempData;
import wand555.github.io.challenges.mapping.DeathMessage;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class FileManagerTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;
    private ResourceBundle bundle;
    private ModelMapper mapper;
    private Context context;

    private ChallengeManager challengeManager;
    private BlockBreakRule blockBreakRule;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws IOException {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer();
        bundle = ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get());
        JsonNode schemaRoot = new ObjectMapper().readTree(FileManager.class.getResourceAsStream(
                "/challenges_schema.json"));
        List<MaterialJSON> materialJSONS = new ObjectMapper().readValue(FileManager.class.getResourceAsStream(
                "/materials.json"), MaterialDataSource.class).getData();
        List<EntityTypeJSON> entityTypeJSONS = new ObjectMapper().readValue(FileManager.class.getResourceAsStream(
                "/entity_types.json"), EntityTypeDataSource.class).getData();
        List<DeathMessage> deathMessages = new ObjectMapper().readValue(FileManager.class.getResourceAsStream(
                "/death_messages_as_data_source_JSON.json"), DeathMessageDataSource.class).getData();

        challengeManager = mock(ChallengeManager.class);
        when(challengeManager.getGoals()).thenReturn(List.of());
        when(challengeManager.getTeams()).thenReturn(List.of());

        context = new Context(plugin,
                              new ResourceBundleContext(bundle, CriteriaUtil.loadGoalResourceBundle(), null, null, null, null),
                              new DataSourceContext(materialJSONS, entityTypeJSONS, deathMessages, CriteriaUtil.loadCraftingTypes().getData()),
                              schemaRoot,
                              challengeManager,
                              new Random(),
                              new OfflineTempData(plugin)
        );
        mapper = new ModelMapper(context);




        //noBlockBreakRule = new NoBlockBreakRule(plugin, bundle, Set.of(Material.STONE));
        //when(noBlockBreakRule.toGeneratedJSONClass()).thenAnswer(invocation -> noBlockBreakRule.toGeneratedJSONClass());
        //when(challengeManager.getRules()).thenReturn(List.of(noBlockBreakRule));
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testWriteRead() throws IOException, URISyntaxException, LoadValidationException {
        String readJSON = objectMapper.writeValueAsString(objectMapper.readValue(FileManagerTest.class.getResourceAsStream(
                "integration/full1.json"), Object.class));
        File file = Paths.get(FileManagerTest.class.getResource("integration/full1.json").toURI()).toFile();
        assertDoesNotThrow(() -> FileManager.readFromFile(file, context, progress -> {}));
        FileManager.readFromFile(file, context, progress -> {});
        // TODO create expectations programmatically and compare
        assertDoesNotThrow(() -> FileManager.writeToFile(challengeManager, new StringWriter()));
    }

    private void assertRules(ChallengeManager manager) {
        assertFalse(manager.getRules().isEmpty());
    }
}
