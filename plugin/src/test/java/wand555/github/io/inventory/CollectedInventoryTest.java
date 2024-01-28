package wand555.github.io.inventory;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.inventory.CollectedItemStack;

import java.util.Arrays;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CollectedInventoryTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player1;
    private PlayerMock player2;

    private CollectedInventory collectedInventory;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);

        collectedInventory = spy(new CollectedInventory(plugin));

        player1 = server.addPlayer();
        player2 = server.addPlayer();



    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testInitialOpen() {
        int collectedItems = 5;
        fillNTimes(collectedItems);
        Inventory openInv = player1.getOpenInventory().getTopInventory();
        assertEquals(collectedItems, nonEmptyCollectableSize(openInv));
    }

    @Test
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
    public void testNextPageClickDoMove() {
        int collectedItems = 55;
        fillNTimes(collectedItems);
        collectedInventory.show(player1);
        player1.simulateInventoryClick(CollectedInventory.NEXT_PAGE_IX);
        Inventory openInv = player1.getOpenInventory().getTopInventory();
        int expected = Math.min(collectedItems-CollectedInventory.USABLE_INV_SPACE, CollectedInventory.USABLE_INV_SPACE);
        assertEquals(expected, nonEmptyCollectableSize(openInv));
    }

    @Test
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

    private void fillNTimes(int n) {
        collectedInventory.clearCollectedItemStacks();
        for(int i=0; i<n; i++) {
            collectedInventory.addCollectedItemStack(new CollectedItemStack(Material.STONE, "abc", i));
        }
    }
}
