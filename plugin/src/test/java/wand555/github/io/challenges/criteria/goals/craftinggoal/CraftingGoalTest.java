package wand555.github.io.challenges.criteria.goals.craftinggoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.craftingoal.CraftingGoalCollectedInventory;
import wand555.github.io.challenges.criteria.goals.craftingoal.CraftingGoalMessageHelper;
import wand555.github.io.challenges.criteria.goals.deathgoal.DeathGoalCollectedInventory;
import wand555.github.io.challenges.criteria.goals.deathgoal.DeathGoalMessageHelper;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class CraftingGoalTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;
    private static Context context;
    private static CraftingGoalMessageHelper messageHelper;
    private static CraftingGoalCollectedInventory collectedInventory;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.goalResourceBundle()).thenReturn(CriteriaUtil.loadGoalResourceBundle());
        DataSourceContext dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.craftingTypeJSONList()).thenReturn(CriteriaUtil.loadCraftingTypes().getData());
        ChallengeManager manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);
        when(manager.canTakeEffect(any(), any())).thenReturn(true);

        context = mock(Context.class);
        when(context.dataSourceContext()).thenReturn(dataSourceContext);
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        when(context.challengeManager()).thenReturn(manager);
        messageHelper = spy(new CraftingGoalMessageHelper(context));
        collectedInventory = mock(CraftingGoalCollectedInventory.class);
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
    public void testSingleReached() {

    }

    @Test
    public void testCompleteConditionMet() {

    }
}
