package wand555.github.io.challenges.criteria.goals;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.deathgoal.DeathGoalCollectedInventory;
import wand555.github.io.challenges.criteria.goals.deathgoal.DeathGoalMessageHelper;
import wand555.github.io.challenges.generated.CollectableDataConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GoalCollectorTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;

    private static Context context;

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
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        player = server.addPlayer("dummy");
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testGoalCollectorForcesFixedOrderIfOnlyOneCollectable() {
        GoalCollector<?> goalCollector = new GoalCollector<>(context,
                                                             List.of(new CollectableEntryConfig(new CollectableDataConfig(
                                                                     5,
                                                                     null,
                                                                     0
                                                             ), "stone")),
                                                             Material.class,
                                                             false,
                                                             true
        );
        assertTrue(goalCollector.isFixedOrder());
    }

    @Test
    public void testGoalCollectorDoesNotForceFixedOrderIfMoreThanOneCollectable() {
        GoalCollector<?> goalCollector = new GoalCollector<>(context,
                                                             List.of(new CollectableEntryConfig(new CollectableDataConfig(
                                                                             5,
                                                                             null,
                                                                             0
                                                                     ), "stone"),
                                                                     new CollectableEntryConfig(new CollectableDataConfig(
                                                                             2, null, 0
                                                                     ), "dirt")
                                                             ),
                                                             Material.class,
                                                             false,
                                                             true
        );
        assertFalse(goalCollector.isFixedOrder());
    }
}
