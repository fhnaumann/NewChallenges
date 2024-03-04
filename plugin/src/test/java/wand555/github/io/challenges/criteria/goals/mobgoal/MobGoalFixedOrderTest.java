package wand555.github.io.challenges.criteria.goals.mobgoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PigMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.WitherSkeletonMock;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
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
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.MobGoalConfig;
import wand555.github.io.challenges.types.mob.MobData;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class MobGoalFixedOrderTest {

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private static Context context;

    private MobGoal mobGoal;
    private static MobGoalMessageHelper messageHelper;

    private EntityDeathEvent pigDeathEvent;

    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.goalResourceBundle()).thenReturn(CriteriaUtil.loadGoalResourceBundle());
        DataSourceContext dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.entityTypeJSONList()).thenReturn(CriteriaUtil.loadEntities().getData());
        ChallengeManager manager = mock(ChallengeManager.class);

        context = mock(Context.class);
        when(context.dataSourceContext()).thenReturn(dataSourceContext);
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        when(context.challengeManager()).thenReturn(manager);
        messageHelper = spy(new MobGoalMessageHelper(context));
    }

    @BeforeEach
    public void setUp() throws IOException {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        player = server.addPlayer("dummy");

        String mobGoalJSON =
                """
                {
                  "mobs": [
                    {
                      "collectableName": "pig",
                      "collectableData": {
                        "amountNeeded": 2,
                        "currentAmount": 0
                      }
                    },
                    {
                      "collectableName": "wither_skeleton",
                      "collectableData": {
                        "amountNeeded": 1,
                        "currentAmount": 0
                      }
                    }
                  ],
                  "shuffled": true,
                  "fixedOrder": true
                }
                """;
        mobGoal = new MobGoal(context, new ObjectMapper().readValue(mobGoalJSON, MobGoalConfig.class), messageHelper);

        LivingEntity pigMock = new PigMock(server, UUID.randomUUID());
        pigMock.setKiller(player);
        pigDeathEvent = new EntityDeathEvent(pigMock, List.of());
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testBossBarExists() {
        assertNotNull(mobGoal.getBossBar());
    }

    @Test
    public void testMobGoalTriggerCheck() {
        assertTrue(mobGoal.triggerCheck().applies(new MobData(EntityType.PIG, player)));
        assertFalse(mobGoal.triggerCheck().applies(new MobData(EntityType.WITHER_SKELETON, player)));
        assertFalse(mobGoal.triggerCheck().applies(new MobData(EntityType.ENDER_DRAGON, player)));
    }

    @Test
    public void testMobGoalTrigger() {
        mobGoal.trigger().actOnTriggered(new MobData(EntityType.PIG, player));
        assertEquals(1, mobGoal.getToKill().get(EntityType.PIG).getCurrentAmount());
        mobGoal.trigger().actOnTriggered(new MobData(EntityType.PIG, player));
        assertEquals(2, mobGoal.getToKill().get(EntityType.PIG).getCurrentAmount());
    }

    @Test
    public void testMobKillTracked() {
        server.getPluginManager().callEvent(pigDeathEvent);
        assertEquals(1, mobGoal.getToKill().get(EntityType.PIG).getCurrentAmount());
        assertEquals(0, mobGoal.getToKill().get(EntityType.WITHER_SKELETON).getCurrentAmount());

        server.getPluginManager().callEvent(pigDeathEvent);
        assertEquals(2, mobGoal.getToKill().get(EntityType.PIG).getCurrentAmount());
        assertEquals(0, mobGoal.getToKill().get(EntityType.WITHER_SKELETON).getCurrentAmount());
    }

    @Test
    public void testCurrentlyToComplete() {
        assertEquals(Map.entry(EntityType.PIG, new Collect(2, 0)), mobGoal.getGoalCollector().getCurrentlyToCollect());
        CriteriaUtil.callEvent(server, pigDeathEvent, 1);
        assertEquals(Map.entry(EntityType.PIG, new Collect(2, 1)), mobGoal.getGoalCollector().getCurrentlyToCollect());
    }

    @Test
    public void testCurrentlyToCompleteSwitch() {
        CriteriaUtil.callEvent(server, pigDeathEvent, 2);
        assertEquals(Map.entry(EntityType.WITHER_SKELETON, new Collect(1, 0)), mobGoal.getGoalCollector().getCurrentlyToCollect());
    }

    @Test
    public void testSingleStepComplete() {
        CriteriaUtil.callEvent(server, pigDeathEvent, 1);
        verify(messageHelper, times(1)).sendSingleStepAction(new MobData(EntityType.PIG, player), new Collect(2, 1));
        verify(messageHelper, never()).sendSingleReachedAction(any(), any());
        verify(messageHelper, never()).sendAllReachedAction();
    }

    @Test
    public void testSingleReachedComplete() {
        CriteriaUtil.callEvent(server, pigDeathEvent, 2);
        verify(messageHelper, times(1)).sendSingleReachedAction(new MobData(EntityType.PIG, player), new Collect(2, 2));
        verify(messageHelper, never()).sendAllReachedAction();
    }

    @Test
    public void testAllReachedComplete() {
        LivingEntity witherSkeletonMock = new WitherSkeletonMock(server, UUID.randomUUID());
        witherSkeletonMock.setKiller(player);
        EntityDeathEvent witherSkeletonDeathEvent = new EntityDeathEvent(witherSkeletonMock, List.of());
        CriteriaUtil.callEvent(server, pigDeathEvent, 2);
        CriteriaUtil.callEvent(server, witherSkeletonDeathEvent, 1);
        verify(messageHelper, times(1)).sendAllReachedAction();
    }

    @Test
    public void testCompleteConditionMet() {
        LivingEntity witherSkeletonMock = new WitherSkeletonMock(server, UUID.randomUUID());
        witherSkeletonMock.setKiller(player);
        EntityDeathEvent witherSkeletonDeathEvent = new EntityDeathEvent(witherSkeletonMock, List.of());
        assertFalse(mobGoal.isComplete());
        CriteriaUtil.callEvent(server, pigDeathEvent, 1);
        assertFalse(mobGoal.isComplete());
        CriteriaUtil.callEvent(server, pigDeathEvent, 1);
        assertFalse(mobGoal.isComplete());
        CriteriaUtil.callEvent(server, witherSkeletonDeathEvent, 1);
        assertTrue(mobGoal.isComplete());
    }
}
