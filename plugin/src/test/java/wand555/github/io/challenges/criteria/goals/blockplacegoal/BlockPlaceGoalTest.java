package wand555.github.io.challenges.criteria.goals.blockplacegoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.blockplace.BlockPlaceGoal;
import wand555.github.io.challenges.criteria.goals.blockplace.BlockPlaceGoalCollectedInventory;
import wand555.github.io.challenges.criteria.goals.blockplace.BlockPlaceGoalMessageHelper;
import wand555.github.io.challenges.generated.BlockBreakGoalConfig;
import wand555.github.io.challenges.generated.BlockPlaceGoalConfig;
import wand555.github.io.challenges.generated.ContributorsConfig;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.types.blockplace.BlockPlaceData;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class BlockPlaceGoalTest {

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private static Context context;

    private BlockPlaceGoal blockPlaceGoal;
    private static BlockPlaceGoalMessageHelper messageHelper;
    private static BlockPlaceGoalCollectedInventory collectedInventory;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.goalResourceBundle()).thenReturn(CriteriaUtil.loadGoalResourceBundle());
        DataSourceContext dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.materialJSONList()).thenReturn(CriteriaUtil.loadMaterials().getData());
        ChallengeManager manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);

        context = mock(Context.class);
        when(context.dataSourceContext()).thenReturn(dataSourceContext);
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        when(context.challengeManager()).thenReturn(manager);
        messageHelper = spy(new BlockPlaceGoalMessageHelper(context));
        collectedInventory = mock(BlockPlaceGoalCollectedInventory.class);
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
                  "placed": [
                    {
                      "collectableName": "stone",
                      "collectableData": {
                        "amountNeeded": 2,
                        "currentAmount": 0
                      }
                    },
                    {
                      "collectableName": "dirt",
                      "collectableData": {
                        "currentAmount": 0,
                        "amountNeeded": 1
                      }
                    }
                  ]
                }
                """;
        BlockPlaceGoalConfig config = new ObjectMapper().readValue(blockBreakGoalJSON, BlockPlaceGoalConfig.class);
        blockPlaceGoal = new BlockPlaceGoal(context,
                                            config,
                                            new GoalCollector<>(context,
                                                                config.getPlaced(),
                                                                Material.class,
                                                                config.isFixedOrder(),
                                                                config.isShuffled()
                                            ),
                                            messageHelper,
                                            collectedInventory,
                                            null
        );
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testContributionTracked() {
        blockPlaceGoal.trigger().actOnTriggered(new BlockPlaceData(Material.STONE, player));
        ContributorsConfig contributorsConfig = blockPlaceGoal.getGoalCollector().getToCollect().get(Material.STONE).getCompletionConfig().getContributors();
        assertTrue(contributorsConfig.getAdditionalProperties().containsKey(player.getName()));
        assertEquals(1, contributorsConfig.getAdditionalProperties().get(player.getName()));
    }

    @Test
    public void testSingleReached() {
        CriteriaUtil.callEvent(server, player.simulateBlockPlace(Material.STONE, player.getLocation()), 2);
        assertTrue(blockPlaceGoal.getGoalCollector().getToCollect().get(Material.STONE).isComplete());
    }

    @Test
    public void testCompleteConditionMet() {
        assertFalse(blockPlaceGoal.isComplete());
        CriteriaUtil.callEvent(server, player.simulateBlockPlace(Material.STONE, player.getLocation()), 2);
        assertFalse(blockPlaceGoal.isComplete());
        CriteriaUtil.callEvent(server, player.simulateBlockPlace(Material.DIRT, player.getLocation()), 1);
        assertTrue(blockPlaceGoal.isComplete());
    }

    @Test
    public void testAllCodes() throws IOException {
        BlockPlaceGoalConfig config = new ObjectMapper().readValue(BlockPlaceGoalTest.class.getResourceAsStream(
                "all_blocks_code_block_place_goal.json"), BlockPlaceGoalConfig.class);
        assertDoesNotThrow(() -> new BlockPlaceGoal(context,
                                                    config,
                                                    new GoalCollector<>(context,
                                                                        config.getPlaced(),
                                                                        Material.class,
                                                                        config.isFixedOrder(),
                                                                        config.isShuffled()
                                                    ),
                                                    messageHelper,
                                                    collectedInventory,
                                                    null
        ));
    }
}
