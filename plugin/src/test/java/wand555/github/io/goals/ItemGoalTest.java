package wand555.github.io.goals;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.InventoryViewMock;
import be.seeseemelk.mockbukkit.inventory.PlayerInventoryViewMock;
import be.seeseemelk.mockbukkit.inventory.WorkbenchInventoryMock;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.Challenges;

public class ItemGoalTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer("dummy");
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testInventoryClick() {
        WorkbenchInventoryMock workbenchInventoryMock = new WorkbenchInventoryMock(null);
        workbenchInventoryMock.setMatrix(new ItemStack[] { // <- throws error
                null, null, null,
                null, plank(), null,
                null, plank(), null, null
        });
        InventoryViewMock inventoryViewMock = new PlayerInventoryViewMock(player, workbenchInventoryMock);
        InventoryClickEvent event = player.simulateInventoryClick(inventoryViewMock, ClickType.LEFT, 0);
    }

    private ItemStack[] createStickCraftingMatrix() {
        return new ItemStack[] {
                null, null, null,
                null, plank(), null,
                null, plank(), null
        };
    }

    private ItemStack plank() {
        return new ItemStack(Material.OAK_PLANKS, 1);
    }
}
