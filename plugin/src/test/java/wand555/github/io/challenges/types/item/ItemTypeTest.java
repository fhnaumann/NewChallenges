package wand555.github.io.challenges.types.item;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.state.ChestMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.WorkbenchInventoryMock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.types.mob.MobData;
import wand555.github.io.challenges.types.mob.MobType;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.spy;

public class ItemTypeTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;

    private ItemType itemType;

    private TriggerCheck<ItemData<?>> mockedTriggerCheck;
    private Trigger<ItemData<?>> mockedTrigger;

    private static Event emptyMockEvent;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer("dummy");
        mockedTriggerCheck = mock(TriggerCheck.class);
        when(mockedTriggerCheck.applies(any(ItemData.class))).thenReturn(true);
        mockedTrigger = mock(Trigger.class);
        doNothing().when(mockedTrigger).actOnTriggered(any(ItemData.class));
        Context mockedContext = mock(Context.class);
        when(mockedContext.plugin()).thenReturn(plugin);
        ChallengeManager mockedManager = mock(ChallengeManager.class);
        when(mockedManager.isRunning()).thenReturn(true);
        when(mockedContext.challengeManager()).thenReturn(mockedManager);
        itemType = spy(new ItemType(mockedContext, mockedTriggerCheck, mockedTrigger));

        emptyMockEvent = mock(Event.class);

    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @MethodSource("provideInventoryClickEvents")
    public void testInventoryEvents(Inventory inventory, ClickType clickType, int slot, Material changedMaterial, int expectedCurrentAmount) {
        player.openInventory(inventory);
        InventoryClickEvent event = player.simulateInventoryClick(player.getOpenInventory(), clickType, slot);
        verify(mockedTrigger, expectedCurrentAmount != 0 ? times(1) : never()).actOnTriggered(new ItemData(
                emptyMockEvent,
                createMockMarkedItemStack(changedMaterial, expectedCurrentAmount),
                expectedCurrentAmount,
                player
        ));
    }

    @Disabled("fails for crafting inventory for now, because there is no way to simulate clicking in the result slot\n" +
            "instead index 0 means the first index of the crafting matrix in the mock (which is a wrong implementation)")
    @ParameterizedTest
    @MethodSource("provideCraftingClickEvents")
    public void testCraftingEvents(CraftingInventory inventory, ClickType clickType, int slot, Material changedMaterial, int expectedCurrentAmount) {
        player.openInventory(inventory);

        InventoryClickEvent event = player.simulateInventoryClick(player.getOpenInventory(), clickType, slot);
        assertEquals(createMockMarkedItemStack(changedMaterial, expectedCurrentAmount), inventory.getResult());
        verify(mockedTrigger, expectedCurrentAmount != 0 ? times(1) : never()).actOnTriggered(new ItemData(
                emptyMockEvent,
                createMockMarkedItemStack(changedMaterial, expectedCurrentAmount),
                player
        ));
    }

    private static ItemStack createMockMarkedItemStack(Material material, int amount) {
        ServerMock serverMock = MockBukkit.getOrCreateMock();
        Challenges plugin = MockBukkit.load(Challenges.class);
        NamespacedKey markedKey = new NamespacedKey(plugin, "marked");
        ItemStack itemStack = new ItemStack(material, amount);
        itemStack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(markedKey,
                                                                                 PersistentDataType.STRING,
                                                                                 "marked"
        ));
        return itemStack;
    }

    public static Stream<Arguments> provideInventoryClickEvents() {
        MockBukkit.getOrCreateMock();
        return Stream.of(
                Arguments.of(set(new ChestMock(Material.CHEST).getInventory(), 0, new ItemStack(Material.CARROT)),
                             ClickType.LEFT,
                             0,
                             Material.CARROT,
                             1
                ),
                Arguments.of(set(new ChestMock(Material.CHEST).getInventory(), 0, new ItemStack(Material.CARROT, 10)),
                             ClickType.RIGHT,
                             0,
                             Material.CARROT,
                             5
                ), // pick up half
                Arguments.of(set(new ChestMock(Material.CHEST).getInventory(), 0, new ItemStack(Material.CARROT, 10)),
                             ClickType.DROP,
                             0,
                             Material.CARROT,
                             0
                ) // dropping does not count as pick up
        );
    }

    public static Stream<Arguments> provideCraftingClickEvents() {
        MockBukkit.getOrCreateMock();
        return Stream.of(
                Arguments.of(stickCrafting(1), ClickType.LEFT, 0, Material.STICK, 4)
        );
    }

    private static Inventory set(Inventory inventory, int slot, ItemStack what) {
        inventory.setItem(slot, what);
        return inventory;
    }

    private static CraftingInventory stickCrafting(int amountPerPlank) {
        WorkbenchInventoryMock workbenchInventoryMock = new WorkbenchInventoryMock(null);
        workbenchInventoryMock.setMatrix(new ItemStack[] {
                null, null, null,
                null, plank(amountPerPlank), null,
                null, plank(amountPerPlank), null
        });
        workbenchInventoryMock.setResult(createMockMarkedItemStack(Material.STICK, Math.min(amountPerPlank * 4, 64)));
        return workbenchInventoryMock;
    }

    private static ItemStack plank(int amount) {
        return new ItemStack(Material.OAK_PLANKS, amount);
    }
}
