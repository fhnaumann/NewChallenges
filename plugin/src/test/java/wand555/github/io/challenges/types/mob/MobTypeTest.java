package wand555.github.io.challenges.types.mob;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PigMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MobTypeTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;

    private MobType mobType;

    private TriggerCheck<MobData> mockedTriggerCheck;
    private Trigger<MobData> mockedTrigger;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer("dummy");
        mockedTriggerCheck = mock(TriggerCheck.class);
        when(mockedTriggerCheck.applies(any(MobData.class))).thenReturn(true);
        mockedTrigger = mock(Trigger.class);
        doNothing().when(mockedTrigger).actOnTriggered(any(MobData.class));
        Context mockedContext = mock(Context.class);
        when(mockedContext.plugin()).thenReturn(plugin);
        mobType = spy(new MobType(mockedContext, mockedTriggerCheck, mockedTrigger));
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testOnMobDeathValid() {
        LivingEntity pigMock = new PigMock(server, UUID.randomUUID());
        pigMock.setKiller(player);
        EntityDeathEvent entityDeathEvent = new EntityDeathEvent(pigMock, List.of());
        mobType.onMobDeath(entityDeathEvent);
        verify(mockedTriggerCheck, times(1)).applies(any(MobData.class));
        verify(mockedTrigger, times(1)).actOnTriggered(any(MobData.class));
    }

    @Test
    public void testOnMobDeathNoKiller() {
        LivingEntity pigMock = new PigMock(server, UUID.randomUUID());
        EntityDeathEvent entityDeathEvent = new EntityDeathEvent(pigMock, List.of());
        mobType.onMobDeath(entityDeathEvent);
        verify(mockedTriggerCheck, never()).applies(any(MobData.class));
        verify(mockedTrigger, never()).actOnTriggered(any(MobData.class));
    }
}
