package wand555.github.io.challenges.criteria.goals.itemgoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.generated.ItemGoalConfig;
import wand555.github.io.challenges.offline_temp.OfflineTempData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemGoalJSONTest {

    private static ResourceBundleContext resourceBundleContextMock;
    private static DataSourceContext dataSourceContextMock;

    private ServerMock server;
    private Challenges plugin;

    private Context context;
    private ItemGoalMessageHelper messageHelper;

    private ObjectMapper objectMapper;
    private ItemGoalCollectedInventory collectedInventory;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        resourceBundleContextMock = mock(ResourceBundleContext.class);
        when(resourceBundleContextMock.goalResourceBundle()).thenReturn(CriteriaUtil.loadGoalResourceBundle());
        dataSourceContextMock = mock(DataSourceContext.class);
        when(dataSourceContextMock.materialJSONList()).thenReturn(CriteriaUtil.loadMaterials().getData());
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);

        ChallengeManager managerMock = mock(ChallengeManager.class);
        context = new Context(plugin, resourceBundleContextMock, dataSourceContextMock, null, managerMock, new Random(), new OfflineTempData(plugin));

        messageHelper = mock(ItemGoalMessageHelper.class);
        collectedInventory = mock(ItemGoalCollectedInventory.class);
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testMinimalItemGoalJSON2Model() throws JsonProcessingException {
        String minimalItemGoalJSON =
                """
                {
                  "items": []
                }
                """;
        assertDoesNotThrow(() -> objectMapper.readValue(minimalItemGoalJSON, ItemGoalConfig.class));
        ItemGoalConfig config = objectMapper.readValue(minimalItemGoalJSON, ItemGoalConfig.class);
        ItemGoal itemGoal = new ItemGoal(context, config, new GoalCollector<>(context, config.getItems(), Material.class, config.isFixedOrder(), config.isShuffled()), messageHelper, collectedInventory, null);
        assertTrue(itemGoal.getToCollect().isEmpty());
    }

    @Test
    public void testMultipleItemsItemGoalJSON2Model() throws IOException {
        String multipleItemsItemGoalJSON =
                """
                {
                  "items": [
                    {
                      "collectableName": "carrot",
                      "collectableData": {
                        "amountNeeded": 2,
                        "currentAmount": 0
                      }
                    },
                    {
                      "collectableName": "stone",
                      "collectableData": {
                        "amountNeeded": 3,
                        "currentAmount": 1
                      }
                    }
                  ]
                }
                """;
        assertDoesNotThrow(() -> objectMapper.readValue(multipleItemsItemGoalJSON, ItemGoalConfig.class));
        ItemGoalConfig config = objectMapper.readValue(multipleItemsItemGoalJSON, ItemGoalConfig.class);
        ItemGoal itemGoal = new ItemGoal(context, config, new GoalCollector<>(context, config.getItems(), Material.class, config.isFixedOrder(), config.isShuffled()), messageHelper, collectedInventory, null);
        Map<Material, Collect> expected = new HashMap<>();
        expected.put(Material.CARROT, new Collect(2,0));
        expected.put(Material.STONE, new Collect(3,1));
        assertEquals(expected, itemGoal.getToCollect());
    }

    @Test
    public void testMultipleMobsFixedOrderMobGoalJSON2Model() throws IOException {
        String multipleItemsItemGoalJSON =
                """
                {
                  "fixedOrder": true,
                  "shuffled": true,
                  "items": [
                    {
                      "collectableName": "carrot",
                      "collectableData": {
                        "amountNeeded": 2,
                        "currentAmount": 0
                      }
                    },
                    {
                      "collectableName": "stone",
                      "collectableData": {
                        "amountNeeded": 3,
                        "currentAmount": 1
                      }
                    }
                  ]
                }
                """;
        assertDoesNotThrow(() -> objectMapper.readValue(multipleItemsItemGoalJSON, ItemGoalConfig.class));
        ItemGoalConfig config = objectMapper.readValue(multipleItemsItemGoalJSON, ItemGoalConfig.class);
        ItemGoal itemGoal = new ItemGoal(context, config, new GoalCollector<>(context, config.getItems(), Material.class, config.isFixedOrder(), config.isShuffled()), messageHelper, collectedInventory, null);
        assertEquals(Map.entry(Material.CARROT, new Collect(2, 0)), itemGoal.getGoalCollector().getCurrentlyToCollect());
    }
}
