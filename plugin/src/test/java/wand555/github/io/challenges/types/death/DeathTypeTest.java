package wand555.github.io.challenges.types.death;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityResurrectEvent;
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
import wand555.github.io.challenges.generated.MCEventAlias;
import wand555.github.io.challenges.live.LiveService;
import wand555.github.io.challenges.mapping.DeathMessage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

public class DeathTypeTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;

    private DeathType deathType;

    private TriggerCheck<DeathData> mockedTriggerCheck;
    private Trigger<DeathData> mockedTrigger;
    private static List<DeathMessage> deathMessages;

    private static PlayerDeathEvent emptyMockEvent;

    @BeforeAll
    public static void beforeAll() throws IOException {
        deathMessages = CriteriaUtil.loadDeathMessages().getData();
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer("dummy");
        mockedTriggerCheck = mock(TriggerCheck.class);
        when(mockedTriggerCheck.applies(any(DeathData.class))).thenReturn(true);
        mockedTrigger = mock(Trigger.class);
        doNothing().when(mockedTrigger).actOnTriggered(any(DeathData.class));
        ChallengeManager manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);
        Context mockedContext = mock(Context.class);
        when(mockedContext.plugin()).thenReturn(plugin);
        when(mockedContext.challengeManager()).thenReturn(manager);
        LiveService mockLiveService = CriteriaUtil.mockLiveService();
        when(mockedContext.liveService()).thenReturn(mockLiveService);
        DataSourceContext mockedDataSourceContext = mock(DataSourceContext.class);
        when(mockedDataSourceContext.deathMessageList()).thenReturn(deathMessages);
        when(mockedContext.dataSourceContext()).thenReturn(mockedDataSourceContext);
        deathType = spy(new DeathType(mockedContext, mockedTriggerCheck, mockedTrigger, MCEventAlias.EventType.NO_DEATH));

        emptyMockEvent = mock(PlayerDeathEvent.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @MethodSource("provideDeathMessages")
    public void testPlaceholdersMapped(DeathMessage expectedDeathMessage, PlayerDeathEvent event) {
        deathType.onPlayerResurrectEvent(noTotem(event.getPlayer()));
        deathType.onPlayerDeathEvent(event);
        verify(mockedTrigger, times(1)).actOnTriggered(new DeathData(emptyMockEvent, 0, event.getPlayer(), expectedDeathMessage));
        //assertEquals(expectedDeathMessage, event.getDeathMessage());
    }

    private static EntityResurrectEvent noTotem(Player player) {
        EntityResurrectEvent event = new EntityResurrectEvent(player, null);
        event.setCancelled(true);
        return event;
    }

    private static EntityResurrectEvent totem(Player player) {
        return new EntityResurrectEvent(player, null);
    }

    private static Stream<Arguments> provideDeathMessages() {
        ServerMock serverMock = MockBukkit.getOrCreateMock();
        Player player1 = serverMock.addPlayer("wand555");
        return Stream.of(
                Arguments.of(death_anvil_attack(), wrap(player1, Component.translatable("death.attack.anvil"))),
                Arguments.of(death_anvil_attack_player(),
                             wrap(player1, Component.translatable("death.attack.anvil.player"))
                ),
                Arguments.of(death_attack_fireworks(), wrap(player1, Component.translatable("death.attack.fireworks"))),
                Arguments.of(death_attack_fireworks_player(),
                             wrap(player1, Component.translatable("death.attack.fireworks.player"))
                ),
                Arguments.of(death_attack_fireworks_item(),
                             wrap(player1, Component.translatable("death.attack.fireworks.item"))
                ),
                Arguments.of(death_attack_cramming(), wrap(player1, Component.translatable("death.attack.cramming")))
        );
    }

    @Test
    public void testOnDeathNoTotem() {
        String expectedKey = "death.attack.cactus";
        DeathMessage expectedDeathMessage = new DeathMessage(expectedKey,
                                                             "[player] was pricked to death"
        );
        PlayerDeathEvent deathEvent = wrap(player, Component.translatable(expectedKey));
        deathType.onPlayerResurrectEvent(noTotem(player));
        deathType.onPlayerDeathEvent(deathEvent);
        verify(mockedTrigger, times(1)).actOnTriggered(new DeathData(emptyMockEvent, 0, deathEvent.getPlayer(), expectedDeathMessage));
    }

    @Test
    public void testOnDeathWithTotem() {
        /*
        Verify that a totem itself has no difference to no item when it comes to triggering.
        The check if totems should be ignored or not, is done in the actual classes depending on deathType via triggerCheck
         */

        String expectedKey = "death.attack.cactus";
        DeathMessage expectedDeathMessage = new DeathMessage(expectedKey,
                                                             "[player] was pricked to death"
        );
        PlayerDeathEvent deathEvent = wrap(player, Component.translatable(expectedKey));
        deathType.onPlayerResurrectEvent(totem(player));
        deathType.onPlayerDeathEvent(deathEvent);
        verify(mockedTrigger, times(1)).actOnTriggered(new DeathData(emptyMockEvent, 0, deathEvent.getPlayer(),
                                                                     1,
                                                                     expectedDeathMessage,
                                                                     true
        ));
    }


    private static PlayerDeathEvent wrap(Player player, Component deathMessage) {
        return new PlayerDeathEvent(player, List.of(), 3, deathMessage);
    }

    private static DeathMessage death_attack_cramming() {
        return new DeathMessage(
                "death.attack.cramming",
                "[player] was squished too much"
        );
    }

    private static DeathMessage death_attack_fireworks() {
        return new DeathMessage(
                "death.attack.fireworks",
                "[player] went off with a bang"
        );
    }

    private static DeathMessage death_attack_fireworks_player() {
        return new DeathMessage(
                "death.attack.fireworks.player",
                "[player] went off with a bang while fighting [mob]"
        );
    }

    private static DeathMessage death_attack_fireworks_item() {
        return new DeathMessage(
                "death.attack.fireworks.item",
                "[player] went off with a bang due to a firework fired from [item] by [mob]"
        );
    }

    private static DeathMessage death_anvil_attack() {
        return new DeathMessage(
                "death.attack.anvil",
                "[player] was squashed by a falling anvil"
        );
    }

    private static DeathMessage death_anvil_attack_player() {
        return new DeathMessage(
                "death.attack.anvil.player",
                "[player] was squashed by a falling anvil while fighting [mob]"
        );
    }


}
