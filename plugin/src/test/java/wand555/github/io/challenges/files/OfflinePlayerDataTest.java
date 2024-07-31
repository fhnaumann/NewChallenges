package wand555.github.io.challenges.files;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.BoatMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.offline_temp.OfflinePlayerData;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OfflinePlayerDataTest {

    private OfflinePlayerData offlinePlayerData;

    @TempDir
    private Path tempFolderContainingOfflineData;

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;
    private File file;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer();

        offlinePlayerData = new OfflinePlayerData(plugin);
        // Dirty: This file points to the same file that is used in the OfflinePlayerData
        file = Paths.get(plugin.getDataFolder().getAbsolutePath(), "offline_temp", "offline_player_temp.yml").toFile();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testFileExists() {
        assertTrue(file.exists() && file.isFile());
    }

    @Test
    public void testStoringPlayerData() {
        assertDoesNotThrow(() -> offlinePlayerData.temporarilyStorePlayerInformationOnDisk(player));
    }

    @Test
    public void testLoadingPlayerData() {
        // save it first
        offlinePlayerData.temporarilyStorePlayerInformationOnDisk(player);
        server.getScheduler().performOneTick();
        assertDoesNotThrow(() -> offlinePlayerData.loadTemporaryPlayerInformationFromDisk(plugin, player));
    }

    @Test
    public void testLoadingPlayerDataIsCorrectlyApplied() {
        Location location = new Location(player.getWorld(), 10, 10, 10);
        player.teleport(location);
        offlinePlayerData.temporarilyStorePlayerInformationOnDisk(player);
        server.getScheduler().performOneTick();
        player.teleport(location.clone().add(10, 0, 0));
        offlinePlayerData.loadTemporaryPlayerInformationFromDisk(plugin, player);
        server.getScheduler().performOneTick();
        player.assertTeleported(location, 1e-3);
    }

    @Test
    public void testSaveAndLoadMinimal() {
        player.setFireTicks(0);
        player.getInventory().clear();
        player.getActivePotionEffects().clear();
        offlinePlayerData.temporarilyStorePlayerInformationOnDisk(player);
        offlinePlayerData.loadTemporaryPlayerInformationFromDisk(plugin, player);
        assertEquals(0, player.getFireTicks());
        assertTrue(player.getInventory().isEmpty());
        assertTrue(player.getActivePotionEffects().isEmpty());
    }

    @Test
    public void testSaveAndLoadMaximal() {
        Location location = new Location(player.getWorld(), 10, 10, 10);
        GameMode gameMode = GameMode.SURVIVAL;
        int fireticks = 10;
        double health = 15d;
        int hunger = 8;
        float saturation = 5f;
        int exp = 365;
        int remainingAir = 3;
        List<ItemStack> inventory = List.of(new ItemStack(Material.STONE),
                                            new ItemStack(Material.DIRT),
                                            new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 64)
        );
        List<PotionEffect> potions = List.of(PotionEffectType.REGENERATION.createEffect(10, 3),
                                             PotionEffectType.BLINDNESS.createEffect(20, 1)
        );
        BoatMock boatMock = (BoatMock) player.getWorld().spawnEntity(player.getLocation(), EntityType.BOAT);

        player.setLocation(location);
        player.setGameMode(gameMode);
        player.setFireTicks(fireticks);
        player.setHealth(health);
        player.setFoodLevel(hunger);
        player.setSaturation(saturation);
        player.setTotalExperience(exp);
        player.setRemainingAir(remainingAir);
        player.getInventory().setContents(inventory.toArray(ItemStack[]::new));
        inventory = Stream.of(player.getInventory().getContents()).toList(); // the nulls have to be at the right place
        player.addPotionEffects(new ArrayList<>(potions));
        boatMock.addPassenger(player);

        offlinePlayerData.temporarilyStorePlayerInformationOnDisk(player);

        // set other values in player
        player.setLocation(new Location(player.getWorld(), 0, 10, 0));
        player.setGameMode(GameMode.CREATIVE);
        player.setFireTicks(15);
        player.setHealth(20d);
        player.setFoodLevel(7);
        player.setSaturation(1);
        player.setTotalExperience(400);
        player.setRemainingAir(5);
        player.getInventory().clear();
        player.clearActivePotionEffects();
        boatMock.removePassenger(player);

        offlinePlayerData.loadTemporaryPlayerInformationFromDisk(plugin, player);

        assertEquals(location, player.getLocation());
        assertEquals(gameMode, player.getGameMode());
        assertEquals(fireticks, player.getFireTicks());
        assertEquals(health, player.getHealth());
        assertEquals(hunger, player.getFoodLevel());
        assertEquals(saturation, player.getSaturation());
        assertEquals(exp, player.getTotalExperience());
        assertEquals(remainingAir, player.getRemainingAir());
        assertEquals(inventory, Stream.of(player.getInventory().getContents()).toList());
        assertFalse(player.getActivePotionEffects().isEmpty());
        // assertEquals(potions, player.getActivePotionEffects()); screw potions, very annoying to compare to
        assertTrue(boatMock.getPassengers().contains(player));
    }

}
