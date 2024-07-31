package wand555.github.io.challenges.inventory;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.ResourceBundleContext;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakCollectedInventory;
import wand555.github.io.challenges.generated.CollectableDataConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.inventory.progress.CollectedInventory;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@Disabled
public class CollectedInventoryTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player1;
    private PlayerMock player2;

    private CollectedInventory<?, ?> collectedInventory;

    private static Context context;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.goalResourceBundle()).thenReturn(CriteriaUtil.loadGoalResourceBundle());
        when(resourceBundleContext.miscResourceBundle()).thenReturn(CriteriaUtil.loadMiscResourceBundle());
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
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);

        collectedInventory = new BlockBreakCollectedInventory(
                context,
                List.of(
                        new CollectableEntryConfig(new CollectableDataConfig(1, null, 0), "beacon"),
                        new CollectableEntryConfig(new CollectableDataConfig(20, null, 0), "dirt")
                ),
                Material.class
        );

        player1 = server.addPlayer();
        player2 = server.addPlayer();


    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testInitialOpen() {
        collectedInventory.show(player1);
        Inventory openInv = player1.getOpenInventory().getTopInventory();
        assertEquals(2, nonEmptyCollectableSize(openInv));
    }


    @Test
    @Disabled
    public void testPrevPageClickDoNothing() {
        fillNTimes(5);
        collectedInventory.show(player1);
        InventoryClickEvent prevPageEvent = player1.simulateInventoryClick(CollectedInventory.PREV_PAGE_IDX);
        collectedInventory.onPageSwapClick(prevPageEvent);
        assertTrue(prevPageEvent.isCancelled());
        player1.closeInventory();

        fillNTimes(45);
        collectedInventory.show(player1);
        InventoryClickEvent prevPageEvent2 = player1.simulateInventoryClick(CollectedInventory.PREV_PAGE_IDX);
        collectedInventory.onPageSwapClick(prevPageEvent2);
        assertTrue(prevPageEvent2.isCancelled());
        player1.closeInventory();
    }

    @Test
    @Disabled
    public void testNextPageClickDoMove() {
        int collectedItems = 55;
        fillNTimes(collectedItems);
        collectedInventory.show(player1);
        player1.simulateInventoryClick(CollectedInventory.NEXT_PAGE_IX);
        Inventory openInv = player1.getOpenInventory().getTopInventory();
        int expected = Math.min(collectedItems - CollectedInventory.USABLE_INV_SPACE,
                                CollectedInventory.USABLE_INV_SPACE
        );
        assertEquals(expected, nonEmptyCollectableSize(openInv));
    }

    @Test
    @Disabled
    public void testDifferentInvPerPlayer() {
        fillNTimes(50);
        collectedInventory.show(player1);
        collectedInventory.show(player2);
        Inventory p1OpenInv = player1.getOpenInventory().getTopInventory();
        Inventory p2OpenInv = player2.getOpenInventory().getTopInventory();
        assertEquals(nonEmptyCollectableSize(p1OpenInv), nonEmptyCollectableSize(p2OpenInv));
        player1.simulateInventoryClick(CollectedInventory.NEXT_PAGE_IX);
        assertNotEquals(nonEmptyCollectableSize(p1OpenInv), nonEmptyCollectableSize(p2OpenInv));
        player2.simulateInventoryClick(CollectedInventory.NEXT_PAGE_IX);
        assertEquals(nonEmptyCollectableSize(p1OpenInv), nonEmptyCollectableSize(p2OpenInv));
    }

    private int nonEmptyCollectableSize(Inventory inventory) {
        //ignoring last row
        return (int) IntStream.range(0, CollectedInventory.USABLE_INV_SPACE)
                              .mapToObj(inventory::getItem)
                              .filter(itemStack -> itemStack != null && !itemStack.isEmpty())
                              .count();
    }


    @Deprecated
    private void fillNTimes(int n) {

        for(int i = 0; i < n; i++) {
            //collectedInventory.addOrUpdate(new BlockBreakData(Material.STONE, player1), new Collect(2, 1));
            //collectedInventory.addCollectedItemStack(new MultipleCollectedItemStack(Material.STONE, "abc", i));
        }


    }
}
