package wand555.github.io.challenges.criteria.goals.itemgoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.state.ChestMock;
import be.seeseemelk.mockbukkit.entity.ItemEntityMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.inventory.PlayerInventoryViewMock;
import be.seeseemelk.mockbukkit.inventory.WorkbenchInventoryMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.FileManager;
import wand555.github.io.challenges.ResourceBundleContext;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoal;
import wand555.github.io.challenges.criteria.goals.mobgoal.MobGoalMessageHelper;
import wand555.github.io.challenges.generated.ItemGoalConfig;
import wand555.github.io.challenges.mapping.MaterialDataSource;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.types.item.ItemData;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
public class ItemGoalTest {

    private NamespacedKey markedKey;

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private static Context context;

    private ItemGoal itemGoal;
    private static ItemGoalMessageHelper messageHelper;

    private EntityPickupItemEvent pickupOneCarrot;

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
        messageHelper = spy(new ItemGoalMessageHelper(context));
    }

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        player = server.addPlayer("dummy");
        markedKey = new NamespacedKey(plugin, "marked");

        String itemGoalJSON =
                """
                {
                  "items": [
                    {
                      "collectableName": "carrot",
                      "collectableData": {
                        "amountNeeded": 100,
                        "currentAmount": 0
                      }
                    },
                    {
                      "collectableName": "stick",
                      "collectableData": {
                        "currentAmount": 0,
                        "amountNeeded": 100
                      }
                    },
                    {
                      "collectableName": "stone",
                      "collectableData": {
                        "currentAmount": 0,
                        "amountNeeded": 50
                      }
                    }
                  ]
                }
                """;
        itemGoal = new ItemGoal(context, new ObjectMapper().readValue(itemGoalJSON, ItemGoalConfig.class), messageHelper);

        ItemStack toCollect = new ItemStack(Material.CARROT);
        pickupOneCarrot = new EntityPickupItemEvent(player, new ItemEntityMock(server, UUID.randomUUID(), toCollect), 0);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testItemGoalTriggerCheck() {
        assertTrue(itemGoal.triggerCheck().applies(new ItemData(new ItemStack(Material.CARROT), player)));
        assertTrue(itemGoal.triggerCheck().applies(new ItemData(new ItemStack(Material.STONE), player)));
        assertFalse(itemGoal.triggerCheck().applies(new ItemData(new ItemStack(Material.DIRT), player)));
    }

    @Test
    public void testItemCollectedTracked() {
        itemGoal.trigger().actOnTriggered(new ItemData(new ItemStack(Material.CARROT), player));
        assertEquals(1, itemGoal.getToCollect().get(Material.CARROT).getCurrentAmount());
        assertEquals(0, itemGoal.getToCollect().get(Material.STONE).getCurrentAmount());

        itemGoal.trigger().actOnTriggered(new ItemData(new ItemStack(Material.CARROT), player));

        assertEquals(2, itemGoal.getToCollect().get(Material.CARROT).getCurrentAmount());
        assertEquals(0, itemGoal.getToCollect().get(Material.STONE).getCurrentAmount());
    }

    @Test
    public void testInventoryResultClick() {
        Inventory inventory = stickCrafting(1);
        player.openInventory(inventory);
        player.simulateInventoryClick(player.getOpenInventory(), ClickType.LEFT, 0);
        assertEquals(new ItemStack(Material.STICK, 4), ((CraftingInventory) inventory).getResult()); // passes
        assertEquals(new ItemStack(Material.STICK, 4), inventory.getItem(0)); // fails
    }

    @ParameterizedTest
    @MethodSource("provideInventoryClickEvents")
    public void testInventoryEvents(Inventory inventory, ClickType clickType, int slot, Material changedMaterial, int expectedCurrentAmount) {
        player.openInventory(inventory);
        InventoryClickEvent event = player.simulateInventoryClick(player.getOpenInventory(), clickType, slot);
        assertEquals(expectedCurrentAmount, itemGoal.getToCollect().get(changedMaterial).getCurrentAmount());
        /*List<ItemStack> resultsInInventory = Arrays.stream(event.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(itemStack -> itemStack.getType() == changedMaterial)
                .toList();
        assertFalse(resultsInInventory.isEmpty(), "Expected items to have moved to the player's inventory, but they cannot be found!");
        assertEquals(expectedCurrentAmount, resultsInInventory.stream().mapToInt(ItemStack::getAmount).count());
        resultsInInventory.forEach(itemStack -> {
            assertTrue(itemStack.getItemMeta().getPersistentDataContainer().has(markedKey, PersistentDataType.STRING));
        });

         */
    }

    public static Stream<Arguments> provideInventoryClickEvents() {
        MockBukkit.getOrCreateMock();
        return Stream.of(
                Arguments.of(set(new ChestMock(Material.CHEST).getInventory(), 0, new ItemStack(Material.CARROT)), ClickType.LEFT, 0, Material.CARROT, 1),
                Arguments.of(set(new ChestMock(Material.CHEST).getInventory(), 0, new ItemStack(Material.CARROT, 10)), ClickType.RIGHT, 0, Material.CARROT, 5), // pick up half
                Arguments.of(set(new ChestMock(Material.CHEST).getInventory(), 0, new ItemStack(Material.CARROT, 10)), ClickType.DROP, 0, Material.CARROT, 0), // dropping does not count as pick up
                Arguments.of(stickCrafting(1), ClickType.LEFT, 0, Material.STICK, 4)
        );
    }

    private static Inventory set(Inventory inventory, int slot, ItemStack what) {
        inventory.setItem(slot, what);
        return inventory;
    }

    private static Inventory stickCrafting(int amountPerPlank) {
        WorkbenchInventoryMock workbenchInventoryMock = new WorkbenchInventoryMock(null);
        workbenchInventoryMock.setMatrix(new ItemStack[]{
                null, null, null,
                null, plank(amountPerPlank), null,
                null, plank(amountPerPlank), null
        });
        workbenchInventoryMock.setResult(new ItemStack(Material.STICK, Math.min(amountPerPlank*4, 64)));
        return workbenchInventoryMock;
    }

    private static ItemStack plank(int amount) {
        return new ItemStack(Material.OAK_PLANKS, amount);
    }

    @Test
    public void testCompleteConditionMet() {

    }
}
