package wand555.github.io.challenges.criteria.goals.blockbreakgoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import org.junit.jupiter.api.*;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakCollectedInventory;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoal;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoalMessageHelper;
import wand555.github.io.challenges.generated.BlockBreakGoalConfig;
import wand555.github.io.challenges.generated.GoalTimer;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BlockBreakGoalTimerTest {

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private static Context context;
    private static ChallengeManager manager;

    private BlockBreakGoal blockBreakGoal;
    private static BlockBreakGoalMessageHelper messageHelper;
    private static BlockBreakCollectedInventory collectedInventory;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.goalResourceBundle()).thenReturn(CriteriaUtil.loadGoalResourceBundle());
        DataSourceContext dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.materialJSONList()).thenReturn(CriteriaUtil.loadMaterials().getData());
        manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);

        context = mock(Context.class);
        when(context.dataSourceContext()).thenReturn(dataSourceContext);
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        when(context.challengeManager()).thenReturn(manager);
        messageHelper = spy(new BlockBreakGoalMessageHelper(context));
        collectedInventory = mock(BlockBreakCollectedInventory.class);
    }

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        player = server.addPlayer("dummy");

        String blockBreakGoalJSON =
                """
                {
                  "broken": [
                    {
                      "collectableName": "dirt",
                      "collectableData": {
                        "currentAmount": 0,
                        "amountNeeded": 1
                      }
                    }
                  ],
                  "goalTimer": {
                    "time": -1,
                    "startingTime": -1,
                    "order": 2,
                    "minTimeSeconds": 180,
                    
                    "maxTimeSeconds": 180
                  }
                }
                """;
        BlockBreakGoalConfig config = new ObjectMapper().readValue(blockBreakGoalJSON, BlockBreakGoalConfig.class);
        Timer expectedGoalTimer = new Timer(new GoalTimer(180, 180, 2, -1, -1));
        blockBreakGoal = new BlockBreakGoal(context, config, new GoalCollector<>(context, config.getBroken(), Material.class, config.isFixedOrder(), config.isShuffled()), messageHelper, collectedInventory, expectedGoalTimer);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testHasTimer() {
        assertTrue(blockBreakGoal.hasTimer());
    }

    @Test
    public void testBlockBreakGoalTriggerCheckDifferentOrderNumber() {
        when(manager.getCurrentOrder()).thenReturn(1);
        assertFalse(blockBreakGoal.triggerCheck().applies(new BlockBreakData(Material.DIRT, player)));
    }

    @Test
    public void testBlockBreakGoalTriggerCheckSameOrderNumber() {
        when(manager.getCurrentOrder()).thenReturn(2);
        assertTrue(blockBreakGoal.triggerCheck().applies(new BlockBreakData(Material.DIRT, player)));
    }
}
