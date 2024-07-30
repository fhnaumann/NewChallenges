package wand555.github.io.challenges;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.mlg.MLGHandler;
import wand555.github.io.challenges.offline_temp.OfflinePlayerData;
import wand555.github.io.challenges.punishments.InteractionManager;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Funky behaviour: If there's test code within the callback when the MLG is complete and no MLG completion "event" (simulate{Success,Fail,Abort} method)
 * is called at the end of the test, then the test code inside the callback is never executed and the test will always pass.
 */
public class MLGHandlerTest {

    private Context context;
    private ServerMock server;
    private Challenges plugin;
    private WorldMock mlgWorld;
    private PlayerMock player;

    private MLGHandler mlgHandler;
    private OfflinePlayerData offlinePlayerData;

    private static final BiConsumer<Player, MLGHandler.Result> NOTHING = (player1, result) -> {};

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer();
        // add world after adding a player, so the player is initially spawned in the default 'world'
        mlgWorld = server.addSimpleWorld(ConfigValues.MLG_WORLD.name());

        context = mock(Context.class);
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.miscResourceBundle()).thenReturn(CriteriaUtil.loadMiscResourceBundle());
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        when(context.plugin()).thenReturn(plugin);
        offlinePlayerData = mock(OfflinePlayerData.class);
        mlgHandler = spy(new MLGHandler(context, offlinePlayerData));
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testPlayerIsTeleportedToMLGWorld() {
        int height = 20;
        mlgHandler.newMLGScenarioFor(player, height, NOTHING);
        assertEquals(mlgWorld, player.getWorld());
    }

    @Test
    public void testPlayerOnlyHasWaterBucket() {
        mlgHandler.newMLGScenarioFor(player, 20, NOTHING);
        assertTrue(player.getInventory().contains(Material.WATER_BUCKET));
        player.getInventory().forEach(itemStack -> {
            if(itemStack == null) {
                return;
            }
            if(itemStack.getType() != Material.WATER_BUCKET) {
                fail("%s in player inventory, but it should only be a water bucket".formatted(itemStack.getType()));
            }
        });
    }

    @Test
    public void testPlayerInSurvival() {
        mlgHandler.newMLGScenarioFor(player, 20, NOTHING);
        player.assertGameMode(GameMode.SURVIVAL);
    }

    @Test
    public void testPlayerStateStored() {
        mlgHandler.newMLGScenarioFor(player, 20, NOTHING);
        verify(offlinePlayerData).temporarilyStorePlayerInformationOnDisk(player);
    }

    @Test
    public void testPlayerStateLoaded() {
        BiConsumer<Player, MLGHandler.Result> mlgResult = mlgFinishSpy((player1, result) -> verify(offlinePlayerData).loadTemporaryPlayerInformationFromDisk(plugin, player));
        mlgHandler.newMLGScenarioFor(player, 20, mlgResult);
        simulateMLGFailHitGround();
        verify(mlgResult).accept(eq(player), any(MLGHandler.Result.class));
    }

    @Test
    public void testPlayerIsUnableToInteractDuringMLG() {
        mlgHandler.newMLGScenarioFor(player, 20, NOTHING);
        assertTrue(InteractionManager.isUnableToInteract(player));
    }

    @Test
    public void testPlayerIsAbleToInteractAfterMLG() {
        BiConsumer<Player, MLGHandler.Result> mlgResult = mlgFinishSpy((player1, result) -> assertFalse(InteractionManager.isUnableToInteract(player)));
        mlgHandler.newMLGScenarioFor(player, 20, mlgResult);
        simulateMLGFailHitGround();
        verify(mlgResult).accept(eq(player), any(MLGHandler.Result.class));
    }

    @Test
    public void testNewMLGScheduledIfPlayerUnableToInteract() {
        InteractionManager.setUnableToInteract(player, player1 -> {});
        mlgHandler.newMLGScenarioFor(player, 20, NOTHING);
        assertNotEquals(mlgWorld, player.getWorld());
        InteractionManager.removeUnableToInteract(context, player, false);
        assertEquals(mlgWorld, player.getWorld());
    }

    @Test
    public void testMLGFailHitGround() {
        BiConsumer<Player, MLGHandler.Result> mlgResult = mlgFinishSpy((player1, result) -> assertEquals(MLGHandler.Result.FAILED, result));
        mlgHandler.newMLGScenarioFor(player, 20, mlgResult);
        simulateMLGFailHitGround();
        verify(mlgResult).accept(eq(player), eq(MLGHandler.Result.FAILED));
    }

    @Test
    public void testMLGFailWaterPlacedButMissedLanding() {
        BiConsumer<Player, MLGHandler.Result> mlgResult = mlgFinishSpy((player1, result) -> assertEquals(MLGHandler.Result.FAILED, result));
        mlgHandler.newMLGScenarioFor(player, 20, mlgResult);
        simulateMLGFailWaterPlacedButMissedLanding();
        verify(mlgResult).accept(eq(player), eq(MLGHandler.Result.FAILED));
    }

    @Test
    public void testMLGSuccessWithSuccessStatus() {
        BiConsumer<Player, MLGHandler.Result> mlgResult = mlgFinishSpy((player1, result) -> assertEquals(MLGHandler.Result.SUCCESS, result));
        mlgHandler.newMLGScenarioFor(player, 20, mlgResult);
        simulateMLGSuccess();
        verify(mlgResult).accept(eq(player), eq(MLGHandler.Result.SUCCESS));
    }

    @Test
    public void testMLGAbortWithAbortStatus() {
        BiConsumer<Player, MLGHandler.Result> mlgResult = mlgFinishSpy(NOTHING);
        mlgHandler.newMLGScenarioFor(player, 20, mlgResult);
        simulateMLGAbort();
        verify(mlgResult).accept(eq(player), eq(MLGHandler.Result.ABORTED));
    }

    @Test
    public void testMLGAbortPlayerIsNowAbleToInteract() {
        mlgHandler.newMLGScenarioFor(player, 20, NOTHING);
        simulateMLGAbort();
        assertFalse(InteractionManager.isUnableToInteract(player));
    }

    @Test
    public void testMLGAbortPlayerStateLoaded() {
        BiConsumer<Player, MLGHandler.Result> mlgResult = mlgFinishSpy((player1, result) -> verify(offlinePlayerData).loadTemporaryPlayerInformationFromDisk(plugin, player));
        mlgHandler.newMLGScenarioFor(player, 20, mlgResult);
        simulateMLGAbort();
        verify(mlgResult).accept(eq(player), any(MLGHandler.Result.class));
    }

    @Test
    public void testPlayerLeftDuringMLG() {
        mlgHandler.newMLGScenarioFor(player, 20, NOTHING);
        CriteriaUtil.reconnect(server, player);
        // assertNotEquals(mlgWorld, player.getWorld()); cannot test this, as OfflinePlayerData is mocked
        assertFalse(InteractionManager.isUnableToInteract(player));
    }

    @Test
    public void testPlayerNotInMLGWorldNotTriggerListener() {

    }

    private void simulateMLGFailHitGround() {
        player.simulateDamage(30d, (Entity) null);
        server.getScheduler().performOneTick();
    }

    private void simulateMLGFailWaterPlacedButMissedLanding() {
        callPlayerBucketEmptyEvent();

        simulateMLGFailHitGround();
        // scheduler to check for received damage is now called
        server.getScheduler().performTicks(19);
    }

    private void simulateMLGSuccess() {
        callPlayerBucketEmptyEvent();
        // scheduler to check for received damage is now called
        server.getScheduler().performTicks(20);
        server.getScheduler().performOneTick();
    }

    private void simulateMLGAbort() {
        server.getScheduler().performOneTick();
        InteractionManager.removeUnableToInteract(context, player, true);
        server.getScheduler().performOneTick();
    }

    private void callPlayerBucketEmptyEvent() {
        PlayerBucketEmptyEvent event = new PlayerBucketEmptyEvent(player, mlgWorld.getBlockAt(0, 5, 0), mlgWorld.getBlockAt(0, 4, 0), BlockFace.UP, Material.WATER_BUCKET, new ItemStack(Material.WATER_BUCKET), EquipmentSlot.HAND);
        CriteriaUtil.callEvent(server, event, 1);
    }

    private BiConsumer<Player, MLGHandler.Result> mlgFinishSpy(BiConsumer<Player, MLGHandler.Result> mlgFinish) {
        @SuppressWarnings("unchecked")
        BiConsumer<Player, MLGHandler.Result> spy = Mockito.spy(BiConsumer.class);
        doAnswer(invocation -> {
            Player player1 = invocation.getArgument(0);
            MLGHandler.Result result = invocation.getArgument(1);
            mlgFinish.accept(player1, result);
            return null;
        }).when(spy).accept(any(Player.class), any(MLGHandler.Result.class));
        return spy;
    }
}
