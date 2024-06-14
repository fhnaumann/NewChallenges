package wand555.github.io.challenges.criteria.goals.blockbreakgoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoal;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoalMessageHelper;
import wand555.github.io.challenges.criteria.goals.factory.BlockBreakGoalFactory;
import wand555.github.io.challenges.criteria.goals.itemgoal.ItemGoalMessageHelper;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoalValidationTest;
import wand555.github.io.challenges.generated.BlockBreakGoalConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BlockBreakGoalJSONTest {

    private static ResourceBundleContext resourceBundleContextMock;
    private static DataSourceContext dataSourceContextMock;

    private ServerMock server;
    private Challenges plugin;

    private Context context;
    private BlockBreakGoalMessageHelper messageHelper;

    private ObjectMapper objectMapper;

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

        messageHelper = new BlockBreakGoalMessageHelper(context);

        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testMultipleBrokenBlockBreakGoalJSON2Model() throws IOException {
        String json =
                """
                {
                  "broken": [
                    {
                      "collectableData": {
                        "amountNeeded": 2,
                        "currentAmount": 0
                      },
                      "collectableName": "stone"
                    },
                    {
                      "collectableData": {
                        "amountNeeded": 2,
                        "currentAmount": 0
                      },
                      "collectableName": "dirt"
                    }
                  ]
                }
                """;
        assertDoesNotThrow(() -> objectMapper.readValue(json, BlockBreakGoalConfig.class));
        BlockBreakGoalConfig config = objectMapper.readValue(json, BlockBreakGoalConfig.class);
        BlockBreakGoal blockBreakGoal = new BlockBreakGoalFactory().createGoal(context, config);
        Map<Material, Collect> expected = new HashMap<>();
        expected.put(Material.STONE, new Collect(2,0));
        expected.put(Material.DIRT, new Collect(2,0));
        assertEquals(expected, blockBreakGoal.getGoalCollector().getToCollect());
    }

    @Test
    public void testMultipleBrokenFixedOrderBlockBreakGoalJSON2Model() throws IOException {
        String json =
                """
                {
                  "fixedOrder": true,
                  "shuffled": true,
                  "broken": [
                    {
                      "collectableData": {
                        "amountNeeded": 2,
                        "currentAmount": 0
                      },
                      "collectableName": "stone"
                    },
                    {
                      "collectableData": {
                        "amountNeeded": 2,
                        "currentAmount": 0
                      },
                      "collectableName": "dirt"
                    }
                  ]
                }
                """;
        assertDoesNotThrow(() -> objectMapper.readValue(json, BlockBreakGoalConfig.class));
        BlockBreakGoalConfig config = objectMapper.readValue(json, BlockBreakGoalConfig.class);
        BlockBreakGoal blockBreakGoal = new BlockBreakGoalFactory().createGoal(context, config);
        assertEquals(Map.entry(Material.STONE, new Collect(2,0)), blockBreakGoal.getGoalCollector().getCurrentlyToCollect());
    }

    @Test
    public void testAllBlockBreakGoalJSON2Model() {
        assertDoesNotThrow(() -> objectMapper.readValue(BlockBreakGoalJSONTest.class.getResourceAsStream("all_blocks_code_block_break_goal.json"), BlockBreakGoalConfig.class));
    }
}
