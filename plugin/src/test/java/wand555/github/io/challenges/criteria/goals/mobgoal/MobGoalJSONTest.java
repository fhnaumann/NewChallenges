package wand555.github.io.challenges.criteria.goals.mobgoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.MobGoalConfig;
import wand555.github.io.challenges.mapping.MaterialDataSource;
import wand555.github.io.challenges.mapping.MaterialJSON;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MobGoalJSONTest {

    private static ResourceBundleContext resourceBundleContext;

    private static JsonNode schemaRoot;
    private static DataSourceContext dataSourceContext;

    private ServerMock server;
    private Challenges plugin;

    private Context context;
    private MobGoalMessageHelper messageHelperMock;

    private ObjectMapper objectMapper;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundle goalResourceBundle = ResourceBundle.getBundle("goals", Locale.ENGLISH, UTF8ResourceBundleControl.get());
        resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.goalResourceBundle()).thenReturn(goalResourceBundle);
        schemaRoot = new ObjectMapper().readTree(Challenges.class.getResource("/challenges_schema.json"));
        List<MaterialJSON> materialJSONS = new ObjectMapper().readValue(FileManager.class.getResourceAsStream("/materials.json"), MaterialDataSource.class).getData();
        dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.materialJSONList()).thenReturn(materialJSONS);
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);

        ChallengeManager manager = mock(ChallengeManager.class);
        context = new Context(plugin, resourceBundleContext, dataSourceContext, schemaRoot, manager);

        messageHelperMock = mock(MobGoalMessageHelper.class, RETURNS_DEFAULTS);

        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testMinimalMobGoalJSON2Model() throws JsonProcessingException {
        String minimalMobGoalJSON =
                """
                {
                  "mobs": {
                   
                  }
                }
                """;
        assertDoesNotThrow(() -> objectMapper.readValue(minimalMobGoalJSON, MobGoalConfig.class));
        MobGoalConfig config = objectMapper.readValue(minimalMobGoalJSON, MobGoalConfig.class);
        MobGoal mobGoal = new MobGoal(context, config, messageHelperMock);
        assertTrue(mobGoal.getToKill().isEmpty());
    }

    @Test
    public void testMultipleMobsMobGoalJSON2Model() throws IOException {
        URL multipleMobsMobGoalJSON = MobGoalJSONTest.class.getResource("multiple_mobs_mob_goal.json");
        assertDoesNotThrow(() -> objectMapper.readValue(multipleMobsMobGoalJSON, MobGoalConfig.class));
        MobGoalConfig config = objectMapper.readValue(multipleMobsMobGoalJSON, MobGoalConfig.class);
        MobGoal mobGoal = new MobGoal(context, config, messageHelperMock);
        Map<EntityType, Collect> expected = new HashMap<>();
        expected.put(EntityType.PIG, new Collect(2,0));
        expected.put(EntityType.WITHER_SKELETON, new Collect(3,1));
        assertEquals(expected, mobGoal.getToKill());
    }
}