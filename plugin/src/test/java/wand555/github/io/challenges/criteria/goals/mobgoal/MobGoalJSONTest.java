package wand555.github.io.challenges.criteria.goals.mobgoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.files.FileManager;
import wand555.github.io.challenges.generated.MobGoalConfig;
import wand555.github.io.challenges.mapping.EntityTypeDataSource;
import wand555.github.io.challenges.mapping.EntityTypeJSON;
import wand555.github.io.challenges.offline_temp.OfflineTempData;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MobGoalJSONTest {

    private static ResourceBundleContext resourceBundleContextMock;

    private static JsonNode schemaRootMock;
    private static DataSourceContext dataSourceContextMock;

    private ServerMock server;
    private Challenges plugin;

    private Context context;
    private MobGoalMessageHelper messageHelper;
    private ObjectMapper objectMapper;
    private MobGoalCollectedInventory collectedInventory;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundle goalResourceBundle = ResourceBundle.getBundle("goals",
                                                                     Locale.ENGLISH,
                                                                     UTF8ResourceBundleControl.get()
        );
        resourceBundleContextMock = mock(ResourceBundleContext.class);
        when(resourceBundleContextMock.goalResourceBundle()).thenReturn(goalResourceBundle);
        schemaRootMock = mock(JsonNode.class);
        List<EntityTypeJSON> entityJSONS = new ObjectMapper().readValue(FileManager.class.getResourceAsStream(
                "/entity_types.json"), EntityTypeDataSource.class).getData();
        dataSourceContextMock = mock(DataSourceContext.class);
        when(dataSourceContextMock.entityTypeJSONList()).thenReturn(entityJSONS);
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);

        ChallengeManager managerMock = mock(ChallengeManager.class);
        context = new Context(plugin,
                              resourceBundleContextMock,
                              dataSourceContextMock,
                              schemaRootMock,
                              managerMock,
                              new Random(),
                              new OfflineTempData(plugin)
        );

        messageHelper = new MobGoalMessageHelper(context);
        collectedInventory = mock(MobGoalCollectedInventory.class);
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
                  "mobs": []
                }
                """;
        assertDoesNotThrow(() -> objectMapper.readValue(minimalMobGoalJSON, MobGoalConfig.class));
        MobGoalConfig config = objectMapper.readValue(minimalMobGoalJSON, MobGoalConfig.class);
        MobGoal mobGoal = new MobGoal(context,
                                      config,
                                      new GoalCollector<>(context,
                                                          config.getMobs(),
                                                          EntityType.class,
                                                          config.isFixedOrder(),
                                                          config.isShuffled()
                                      ),
                                      messageHelper,
                                      collectedInventory,
                                      null
        );
        assertTrue(mobGoal.getToKill().isEmpty());
    }

    @Test
    public void testMultipleMobsMobGoalJSON2Model() throws IOException {
        String multipleMobsMobGoalJSON =
                """
                {
                  "mobs": [
                    {
                      "collectableName": "pig",
                      "collectableData": {
                        "amountNeeded": 2,
                        "currentAmount": 0
                      }
                    },
                    {
                      "collectableName": "wither_skeleton",
                      "collectableData": {
                        "amountNeeded": 3,
                        "currentAmount": 1
                      }
                    }
                  ]
                }
                """;
        assertDoesNotThrow(() -> objectMapper.readValue(multipleMobsMobGoalJSON, MobGoalConfig.class));
        MobGoalConfig config = objectMapper.readValue(multipleMobsMobGoalJSON, MobGoalConfig.class);
        MobGoal mobGoal = new MobGoal(context,
                                      config,
                                      new GoalCollector<>(context,
                                                          config.getMobs(),
                                                          EntityType.class,
                                                          config.isFixedOrder(),
                                                          config.isShuffled()
                                      ),
                                      messageHelper,
                                      collectedInventory,
                                      null
        );
        Map<EntityType, Collect> expected = new HashMap<>();
        expected.put(EntityType.PIG, new Collect(2, 0));
        expected.put(EntityType.WITHER_SKELETON, new Collect(3, 1));
        assertEquals(expected, mobGoal.getToKill());
    }

    @Test
    public void testMultipleMobsFixedOrderMobGoalJSON2Model() throws IOException {
        String multipleMobsMobGoalJSON =
                """
                {
                  "fixedOrder": true,
                  "shuffled": true,
                  "mobs": [
                    {
                      "collectableName": "pig",
                      "collectableData": {
                        "amountNeeded": 2,
                        "currentAmount": 0
                      }
                    },
                    {
                      "collectableName": "wither_skeleton",
                      "collectableData": {
                        "amountNeeded": 3,
                        "currentAmount": 1
                      }
                    }
                  ]
                }
                """;
        assertDoesNotThrow(() -> objectMapper.readValue(multipleMobsMobGoalJSON, MobGoalConfig.class));
        MobGoalConfig config = objectMapper.readValue(multipleMobsMobGoalJSON, MobGoalConfig.class);
        MobGoal mobGoal = new MobGoal(context,
                                      config,
                                      new GoalCollector<>(context,
                                                          config.getMobs(),
                                                          EntityType.class,
                                                          config.isFixedOrder(),
                                                          config.isShuffled()
                                      ),
                                      messageHelper,
                                      collectedInventory,
                                      null
        );
        assertEquals(Map.entry(EntityType.PIG, new Collect(2, 0)), mobGoal.getGoalCollector().getCurrentlyToCollect());
    }

    @Test
    public void testAllMobGoalJSON2Model() {
        assertDoesNotThrow(() -> objectMapper.readValue(MobGoalJSONTest.class.getResourceAsStream(
                "all_mobs_code_mob_goal_isolation.json"), MobGoalConfig.class));
    }
}
