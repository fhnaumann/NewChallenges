package wand555.github.io.goals;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PigMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.block.Block;
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
import wand555.github.io.challenges.ResourceBundleContext;
import wand555.github.io.challenges.goals.Collect;
import wand555.github.io.challenges.goals.MobGoal;
import wand555.github.io.challenges.mapping.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;


public class MobGoalTest {

    private static ResourceBundleContext resourceBundleContext;

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;
    private Block toBeBroken;

    private ResourceBundle bundle;
    private ModelMapper mapper;
    private Context context;

    @BeforeAll
    public static void setUpIOData() {
        ResourceBundle goalResourceBundle = ResourceBundle.getBundle("goals", Locale.ENGLISH, UTF8ResourceBundleControl.get());
        resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.goalResourceBundle()).thenReturn(goalResourceBundle);
    }

    @BeforeEach
    public void setUp() throws IOException {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer("dummy");
        toBeBroken = player.getWorld().getBlockAt(0, 0, 0);

        JsonNode schemaRoot = new ObjectMapper().readTree(Challenges.class.getResource("/test-output-schema.json"));
        ChallengeManager manager = mock(ChallengeManager.class);
        context = new Context(plugin, resourceBundleContext, schemaRoot, manager); //TODO load correct bundle
        mapper = new ModelMapper(context);


    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testMobKillTracked() {
        EntityType killType = EntityType.PIG;
        int killAmount = 2;
        Collect collectMock = mock(Collect.class);
        when(collectMock.getAmountNeeded()).thenReturn(killAmount);
        when(collectMock.getCurrentAmount()).thenCallRealMethod();
        doCallRealMethod().when(collectMock).setCurrentAmount(any(Integer.class));
        when(collectMock.isComplete()).thenAnswer(invocation -> collectMock.getCurrentAmount() == killAmount);
        MobGoal mobGoal = spy(new MobGoal(context, new HashMap<>(Map.of(killType, collectMock))));

        LivingEntity pigMock = new PigMock(server, UUID.randomUUID());
        pigMock.setKiller(player);
        EntityDeathEvent event = new EntityDeathEvent(pigMock, List.of());
        // first event
        mobGoal.onMobDeath(event);
        verify(mobGoal, never()).onComplete();
        assertEquals(1, mobGoal.getToKill().get(killType).getCurrentAmount());

        // second event
        mobGoal.onMobDeath(event);
        verify(mobGoal, times(1)).onComplete();
        assertEquals(2, mobGoal.getToKill().get(killType).getCurrentAmount());

    }
}
