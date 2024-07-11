package wand555.github.io.challenges.types.death;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;

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
        DataSourceContext mockedDataSourceContext = mock(DataSourceContext.class);
        when(mockedDataSourceContext.deathMessageList()).thenReturn(deathMessages);
        when(mockedContext.dataSourceContext()).thenReturn(mockedDataSourceContext);
        deathType = spy(new DeathType(mockedContext, mockedTriggerCheck, mockedTrigger));
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @MethodSource("provideNoSpaceDeathMessages")
    public void testPlaceholdersNoSpaceMapped(DeathMessage expectedDeathMessage, PlayerDeathEvent event) {
        deathType.onPlayerDeathEvent(event);
        deathType.onPlayerResurrectEvent(noTotem(event.getPlayer()));
        verify(mockedTrigger, times(1)).actOnTriggered(new DeathData(event.getPlayer(), expectedDeathMessage));
        //assertEquals(expectedDeathMessage, event.getDeathMessage());
    }

    private static EntityResurrectEvent noTotem(Player player) {
        EntityResurrectEvent event = new EntityResurrectEvent(player, null);
        event.setCancelled(true);
        return event;
    }

    private static Stream<Arguments> provideNoSpaceDeathMessages() {
        ServerMock serverMock = MockBukkit.getOrCreateMock();
        Player player1 = serverMock.addPlayer("wand555");
        return Stream.of(
                Arguments.of(death_anvil_attack(), wrap(player1, "wand555 was squashed by a falling anvil")),
                Arguments.of(death_anvil_attack_player(), wrap(player1, "wand555 was squashed by a falling anvil while fighting Zombie")),
                Arguments.of(death_attack_fireworks(), wrap(player1, "wand555 went off with a bang")),
                Arguments.of(death_attack_fireworks_player(), wrap(player1, "wand555 went off with a bang while fighting Zombie")),
                Arguments.of(death_attack_fireworks_item(), wrap(player1, "wand555 went off with a bang due to a firework fired from MyItem by Zombie")),
                Arguments.of(death_attack_cramming(), wrap(player1, "wand555 was squished too much"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideWithSpaceDeathMessages")
    public void testPlaceholdersWithSpaceMapped(DeathMessage expectedDeathMessage, PlayerDeathEvent event) {
        deathType.onPlayerDeathEvent(event);
        deathType.onPlayerResurrectEvent(noTotem(event.getPlayer()));
        verify(mockedTrigger, times(1)).actOnTriggered(new DeathData(event.getPlayer(), expectedDeathMessage));
        //assertEquals(expectedDeathMessage, event.getDeathMessage());
    }

    private static Stream<Arguments> provideWithSpaceDeathMessages() {
        ServerMock serverMock = MockBukkit.getOrCreateMock();
        String playerName = "wand555 123";
        String mobName = "My almighty Zombie";
        String itemName = "The best crossbow";
        Player player1 = serverMock.addPlayer(playerName);
        return Stream.of(
                Arguments.of(death_anvil_attack(), wrap(player1, "%s was squashed by a falling anvil".formatted(playerName))),
                Arguments.of(death_anvil_attack_player(), wrap(player1, "%s was squashed by a falling anvil while fighting %s".formatted(playerName, mobName))),
                Arguments.of(death_attack_fireworks(), wrap(player1, "%s went off with a bang".formatted(playerName))),
                Arguments.of(death_attack_fireworks_player(), wrap(player1, "%s went off with a bang while fighting %s".formatted(playerName, mobName))),
                Arguments.of(death_attack_fireworks_item(), wrap(player1, "%s went off with a bang due to a firework fired from %s by %s".formatted(playerName, itemName, mobName))),
                Arguments.of(death_attack_cramming(), wrap(player1, "%s was squished too much".formatted(playerName)))
        );
    }

    private static PlayerDeathEvent wrap(Player player, String deathMessage) {
        return new PlayerDeathEvent(player, List.of(), 3, deathMessage);
    }

    private static DeathMessage death_attack_cramming() {
        return new DeathMessage(
                "death.attack.cramming",
                "(?<player>.*?) was squished too much",
                "[player] was squished too much"
        );
    }

    private static DeathMessage death_attack_fireworks() {
        return new DeathMessage(
                "death.attack.fireworks",
                "(?<player>.*?) went off with a bang",
                "[player] went off with a bang"
        );
    }

    private static DeathMessage death_attack_fireworks_player() {
        return new DeathMessage(
                "death.attack.fireworks.player",
                "(?<player>.*?) went off with a bang while fighting (?<mob>.*?)",
                "[player] went off with a bang while fighting [mob]"
        );
    }

    private static DeathMessage death_attack_fireworks_item() {
        return new DeathMessage(
                "death.attack.fireworks.item",
                "(?<player>.*?) went off with a bang due to a firework fired from (?<item>.*?) by (?<mob>.*?)",
                "[player] went off with a bang due to a firework fired from [item] by [mob]"
        );
    }

    private static DeathMessage death_anvil_attack() {
        return new DeathMessage(
                "death.attack.anvil",
                "(?<player>.*?) was squashed by a falling anvil",
                "[player] was squashed by a falling anvil"
        );
    }

    private static DeathMessage death_anvil_attack_player() {
        return new DeathMessage(
                "death.attack.anvil.player",
                "(?<player>.*?) was squashed by a falling anvil while fighting (?<mob>.*?)",
                "[player] was squashed by a falling anvil while fighting [mob]"
        );
    }


}
