package wand555.github.io.challenges.criteria.goals.deathgoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.blockplace.BlockPlaceGoalCollectedInventory;
import wand555.github.io.challenges.criteria.goals.blockplace.BlockPlaceGoalMessageHelper;
import wand555.github.io.challenges.generated.CollectableDataConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.DeathGoalConfig;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

public class DeathGoalTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;
    private static Context context;
    private DeathGoal deathGoal;
    private static DeathGoalMessageHelper messageHelper;
    private static DeathGoalCollectedInventory collectedInventory;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.goalResourceBundle()).thenReturn(CriteriaUtil.loadGoalResourceBundle());
        DataSourceContext dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.deathMessageList()).thenReturn(CriteriaUtil.loadDeathMessages().getData());
        ChallengeManager manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);

        context = mock(Context.class);
        when(context.dataSourceContext()).thenReturn(dataSourceContext);
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        when(context.challengeManager()).thenReturn(manager);
        messageHelper = spy(new DeathGoalMessageHelper(context));
        collectedInventory = mock(DeathGoalCollectedInventory.class);
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        player = server.addPlayer("dummy");

    }
}
