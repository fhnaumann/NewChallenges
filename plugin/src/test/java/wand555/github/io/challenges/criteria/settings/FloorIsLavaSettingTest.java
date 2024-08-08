package wand555.github.io.challenges.criteria.settings;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.settings.floorislava.BlockChangeTimer;
import wand555.github.io.challenges.criteria.settings.floorislava.FloorBlock;
import wand555.github.io.challenges.criteria.settings.floorislava.FloorIsLavaSetting;
import wand555.github.io.challenges.generated.FloorIsLavaSettingConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FloorIsLavaSettingTest {

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private Context context;
    private ChallengeManager manager;
    private WorldMock world;
    private WorldMock mlgWorld;

    private FloorIsLavaSetting floorIsLavaSetting;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        world = server.addSimpleWorld("world");
        player = server.addPlayer();
        context = mock(Context.class);
        when(context.plugin()).thenReturn(plugin);
        manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);
        when(manager.canTakeEffect(context, player)).thenReturn(true);
        when(context.challengeManager()).thenReturn(manager);

        mlgWorld = server.addSimpleWorld("mlgWorld");

        floorIsLavaSetting = spy(new FloorIsLavaSetting(context, new FloorIsLavaSettingConfig(true, 30)));
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testNotInitiatedIfInMLGWorld() {
        player.teleport(new Location(mlgWorld, 0, 1, 0));
        mlgWorld.getBlockAt(0, 0, 0).setType(Material.DIRT);
        assertTrue(floorIsLavaSetting.getFloorBlockMap().isEmpty());
    }

    @Test
    public void testNotInitiatedIfAlreadyLava() {
        world.getBlockAt(0,5,0).setType(Material.LAVA);
        simulateMove(world, 1,0);
        assertTrue(floorIsLavaSetting.getFloorBlockMap().isEmpty());
    }

    @Test
    public void testNotInitiatedIfAlreadyScheduled() {
        simulateMove(world, 1,0);
        player.teleport(new Location(world, 0, 5, 0));
        simulateMove(world, 1, 0);
        assertTrue(floorIsLavaSetting.getFloorBlockMap().containsKey(new Location(world, 0, 4,0)));
    }

    @Test
    public void testFloorBlockIsTransformedFromDefaultToMagma() {
        simulateMove(world, 1, 0);
        server.getScheduler().performTicks(30);
        assertEquals(Material.MAGMA_BLOCK, world.getBlockAt(0, 4, 0).getType());
    }

    @Test
    public void testFloorBlockIsTransformedFromMagmaToLava() {
        Location source = new Location(world, 0, 4, 0);
        FloorBlock floorBlock = new FloorBlock(FloorBlock.FloorBlockStatus.MAGMA, Material.GRASS_BLOCK);
        floorIsLavaSetting.getFloorBlockMap().put(source, floorBlock);
        floorIsLavaSetting.getBlockChangeTimerMap().put(source, new BlockChangeTimer(context, source, floorBlock, floorIsLavaSetting).start());
        server.getScheduler().performTicks(30);
        assertEquals(Material.LAVA, world.getBlockAt(0,4,0).getType());
    }

    @Test
    public void testFloorBlockIsTransformedFromLavaToDefaultIfLavaNotPermanent() {
        when(floorIsLavaSetting.isLavaRemainsPermanently()).thenReturn(false);
        Location source = new Location(world, 0, 4, 0);
        FloorBlock floorBlock = new FloorBlock(FloorBlock.FloorBlockStatus.LAVA, Material.GRASS_BLOCK);
        floorIsLavaSetting.getFloorBlockMap().put(source, floorBlock);
        floorIsLavaSetting.getBlockChangeTimerMap().put(source, new BlockChangeTimer(context, source, floorBlock, floorIsLavaSetting).start());
        server.getScheduler().performTicks(30);
        assertEquals(Material.GRASS_BLOCK, world.getBlockAt(0,4,0).getType());
    }

    @Test
    public void testFloorBlockIsNotTransformedFromLavaToDefaultIfLavaPermanent() {
        Location source = new Location(world, 0, 4, 0);
        FloorBlock floorBlock = new FloorBlock(FloorBlock.FloorBlockStatus.LAVA, Material.GRASS_BLOCK);
        floorIsLavaSetting.getFloorBlockMap().put(source, floorBlock);
        floorIsLavaSetting.getBlockChangeTimerMap().put(source, new BlockChangeTimer(context, source, floorBlock, floorIsLavaSetting).start());
        server.getScheduler().performTicks(30);
        assertEquals(Material.GRASS_BLOCK, world.getBlockAt(0,4,0).getType());
    }

    private void simulateMove(World world, double xTo, double zTo) {
        player.teleport(new Location(world, 0, 5, 0));
        player.simulatePlayerMove(new Location(world, xTo, 4, zTo));
    }
}
