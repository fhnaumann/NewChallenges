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
        assertFalse(itemGoal.triggerCheck().applies(new ItemData(createMockMarkedItemStack(Material.CARROT, 1), player)));
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
    public void testCompleteConditionMet() {
        assertFalse(itemGoal.isComplete());
        CriteriaUtil.callEvent(server, new EntityPickupItemEvent(player, new ItemEntityMock(server, UUID.randomUUID(), new ItemStack(Material.CARROT, 50)), 0), 1);
        CriteriaUtil.callEvent(server, new EntityPickupItemEvent(player, new ItemEntityMock(server, UUID.randomUUID(), new ItemStack(Material.CARROT, 50)), 0), 1);
        assertFalse(itemGoal.isComplete());
        CriteriaUtil.callEvent(server, new EntityPickupItemEvent(player, new ItemEntityMock(server, UUID.randomUUID(), new ItemStack(Material.STICK, 50)), 0), 1);
        CriteriaUtil.callEvent(server, new EntityPickupItemEvent(player, new ItemEntityMock(server, UUID.randomUUID(), new ItemStack(Material.STICK, 50)), 0), 1);
        assertFalse(itemGoal.isComplete());
        CriteriaUtil.callEvent(server, new EntityPickupItemEvent(player, new ItemEntityMock(server, UUID.randomUUID(), new ItemStack(Material.STONE, 50)), 0), 1);
        assertTrue(itemGoal.isComplete());

    }

    private static ItemStack createMockMarkedItemStack(Material material, int amount) {
        ServerMock serverMock = MockBukkit.getOrCreateMock();
        Challenges plugin = MockBukkit.load(Challenges.class);
        NamespacedKey markedKey = new NamespacedKey(plugin, "marked");
        ItemStack itemStack = new ItemStack(material, amount);
        itemStack.editMeta(itemMeta -> itemMeta.getPersistentDataContainer().set(markedKey, PersistentDataType.STRING, "marked"));
        return itemStack;
    }
}
