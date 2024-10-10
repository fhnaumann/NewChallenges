package wand555.github.io.challenges.criteria.goals.deathgoal;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.generated.CollectableDataConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.DeathGoalConfig;
import wand555.github.io.challenges.types.death.DeathData;
import wand555.github.io.challenges.mapping.DeathMessage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeathGoalTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;
    private static Context context;
    private static DeathGoalMessageHelper messageHelper;
    private static DeathGoalCollectedInventory collectedInventory;
    private static PlayerDeathEvent emptyMockEvent;

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

        emptyMockEvent = mock(PlayerDeathEvent.class);
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

    private static Stream<Arguments> provideTotemUsages() {
        ServerMock serverMock = MockBukkit.getOrCreateMock();
        Challenges plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        Player player1 = serverMock.addPlayer();
        return Stream.of(
                Arguments.of(simple(false), dummyDeathData(player1, false), true),
                Arguments.of(simple(true), dummyDeathData(player1, false), true),
                Arguments.of(simple(false), dummyDeathData(player1, true), false),
                Arguments.of(simple(true), dummyDeathData(player1, true), true)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTotemUsages")
    public void testDeathGoalTriggerCheckAllowTotemNoTotemUsed(DeathGoal deathGoal, DeathData deathData, boolean expected) {
        assertEquals(expected, deathGoal.triggerCheck().applies(deathData));
    }

    @Test
    public void testSimpleDeathCounterAchievedTracked() {
        DeathGoal deathGoal = simple(2, false);
        deathGoal.trigger().actOnTriggered(dummyDeathData(player, false));
        assertEquals(1, deathGoal.getDeathAmount().getCurrentAmount());
    }

    @Test
    public void testIndividualDeathCounterAchievedTracked() {
        DeathGoal deathGoal = cactus();
        DeathData dummyData = dummyCactusData(player, false);
        deathGoal.trigger().actOnTriggered(dummyData);
        assertEquals(1, deathGoal.getGoalCollector().getToCollect().get(dummyData.deathMessage()).getCurrentAmount());
    }

    private static DeathGoal deathGoal(DeathGoalConfig config) {
        return new DeathGoal(
                context,
                config,
                new GoalCollector<>(context,
                                    config.getDeathMessages(),
                                    DeathMessage.class,
                                    config.isFixedOrder(),
                                    config.isShuffled()
                ),
                messageHelper,
                collectedInventory,
                config.getGoalTimer() != null ? new Timer(config.getGoalTimer()) : null
        );
    }

    private static DeathGoal simple(int deathAmount, boolean countTotem) {
        return deathGoal(new DeathGoalConfig(false,
                                             countTotem,
                                             new CollectableDataConfig(deathAmount, null, 0),
                                             List.of(),
                                             false,
                                             null,
                                             false
        ));
    }

    private static DeathGoal simple(boolean countTotem) {
        return simple(2, countTotem);
    }

    private static DeathGoal cactus() {
        return deathGoal(new DeathGoalConfig(
                false,
                false,
                new CollectableDataConfig(0, null, 0),
                List.of(new CollectableEntryConfig(new CollectableDataConfig(2, null, 0), "death.attack.cactus")),
                true,
                null,
                false
        ));
    }

    private static DeathData dummyDeathData(Player player, boolean usedTotem) {
        return new DeathData(
                emptyMockEvent,
                0,
                player,
                1,
                new DeathMessage("death.attack.cactus",
                                 "[player] was pricked to death"
                ),
                usedTotem
        );
    }

    private static DeathData dummyCactusData(Player player, boolean usedTotem) {
        return new DeathData(
                emptyMockEvent,
                0,
                player,
                1,
                new DeathMessage("death.attack.cactus",
                                 "[player] was pricked to death"
                ),
                usedTotem
        );
    }
}
