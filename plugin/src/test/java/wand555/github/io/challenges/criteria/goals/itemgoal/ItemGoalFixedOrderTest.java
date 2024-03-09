package wand555.github.io.challenges.criteria.goals.itemgoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.ItemEntityMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
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
import wand555.github.io.challenges.criteria.goals.factory.ItemGoalFactory;
import wand555.github.io.challenges.generated.ItemGoalConfig;
import wand555.github.io.challenges.types.item.ItemData;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class ItemGoalFixedOrderTest {

    private NamespacedKey markedKey;

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private static Context context;

    private ItemGoal itemGoal;
    private static ItemGoalMessageHelper messageHelper;
    private static ItemGoalBossBarHelper bossBarHelper;

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
        bossBarHelper = mock(ItemGoalBossBarHelper.class);
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
                  "fixedOrder": true,
                  "shuffled": true,
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
        ItemGoalConfig config = new ObjectMapper().readValue(itemGoalJSON, ItemGoalConfig.class);
        itemGoal = new ItemGoal(context, config, new GoalCollector<>(context, config.getItems(), Material.class, config.isFixedOrder(), config.isShuffled()),messageHelper, bossBarHelper);

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
        assertFalse(itemGoal.triggerCheck().applies(new ItemData(new ItemStack(Material.STONE), player)));
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
}
