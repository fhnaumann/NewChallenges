package wand555.github.io.challenges.criteria.goals.blockbreakgoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.ResourceBundleContext;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakCollectedInventory;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoal;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoalMessageHelper;
import wand555.github.io.challenges.generated.BlockBreakGoalConfig;
import wand555.github.io.challenges.generated.ContributorsConfig;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BlockBreakGoalTest {

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private static Context context;

    private BlockBreakGoal blockBreakGoal;
    private static BlockBreakGoalMessageHelper messageHelper;
    private static BlockBreakCollectedInventory collectedInventory;

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
        BlockBreakGoalConfig config = new ObjectMapper().readValue(blockBreakGoalJSON, BlockBreakGoalConfig.class);
        blockBreakGoal = new BlockBreakGoal(context, config, new GoalCollector<>(context, config.getBroken(), Material.class, config.isFixedOrder(), config.isShuffled()), messageHelper, collectedInventory, null);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testBlockBreakGoalTriggerCheck() {
        assertTrue(blockBreakGoal.triggerCheck().applies(new BlockBreakData(Material.STONE, player)));
        assertTrue(blockBreakGoal.triggerCheck().applies(new BlockBreakData(Material.DIRT, player)));
        assertFalse(blockBreakGoal.triggerCheck().applies(new BlockBreakData(Material.ANDESITE, player)));
    }

    @Test
    public void testBlockBrokenTracked() {
        blockBreakGoal.trigger().actOnTriggered(new BlockBreakData(Material.STONE, player));
        assertEquals(1, blockBreakGoal.getGoalCollector().getToCollect().get(Material.STONE).getCurrentAmount());
        assertEquals(0, blockBreakGoal.getGoalCollector().getToCollect().get(Material.DIRT).getCurrentAmount());

        blockBreakGoal.trigger().actOnTriggered(new BlockBreakData(Material.STONE, player));
        assertEquals(2, blockBreakGoal.getGoalCollector().getToCollect().get(Material.STONE).getCurrentAmount());
        assertEquals(0, blockBreakGoal.getGoalCollector().getToCollect().get(Material.DIRT).getCurrentAmount());
    }

    @Test
    public void testContributionTracked() {
        blockBreakGoal.trigger().actOnTriggered(new BlockBreakData(Material.STONE, player));
        ContributorsConfig contributorsConfig = blockBreakGoal.getGoalCollector().getToCollect().get(Material.STONE).getCompletionConfig().getContributors();
        assertTrue(contributorsConfig.getAdditionalProperties().containsKey(player.getName()));
        assertEquals(1, contributorsConfig.getAdditionalProperties().get(player.getName()));
    }

    @Test
    public void testSingleReached() {
        CriteriaUtil.callEvent(server, new BlockBreakEvent(new BlockMock(Material.STONE), player), 2);
        assertTrue(blockBreakGoal.getGoalCollector().getToCollect().get(Material.STONE).isComplete());
    }

    @Test
    public void testCompleteConditionMet() {
        assertFalse(blockBreakGoal.isComplete());
        CriteriaUtil.callEvent(server, new BlockBreakEvent(new BlockMock(Material.STONE), player), 2);
        assertFalse(blockBreakGoal.isComplete());
        CriteriaUtil.callEvent(server, new BlockBreakEvent(new BlockMock(Material.DIRT), player), 1);
        assertTrue(blockBreakGoal.isComplete());
    }

    @Test
    public void testAllCodes() throws IOException {
        BlockBreakGoalConfig config = new ObjectMapper().readValue(BlockBreakGoalTest.class.getResourceAsStream("all_blocks_code_block_break_goal.json"), BlockBreakGoalConfig.class);
        assertDoesNotThrow(() -> new BlockBreakGoal(context, config, new GoalCollector<>(context, config.getBroken(), Material.class, config.isFixedOrder(), config.isShuffled()), messageHelper, collectedInventory, null));
    }
}
