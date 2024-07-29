package wand555.github.io.challenges.offline_temp;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import wand555.github.io.challenges.ChallengesDebugLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class OfflinePlayerData {

    private static final Logger logger = ChallengesDebugLogger.getLogger(OfflinePlayerData.class);

    private final File file;
    private final FileConfiguration cfg;

    public OfflinePlayerData(JavaPlugin plugin) {
        file = Paths.get(plugin.getDataFolder().getAbsolutePath(), "offline_temp", "offline_player_temp.yml").toFile();
        cfg = YamlConfiguration.loadConfiguration(file);
        if(!file.exists()) {
            create();
        }
    }

    private void create() {
        try {
            Files.createDirectories(file.getParentFile().toPath());
            Files.createFile(file.toPath());
            logger.fine("Created offline_player_data.json file and parent folders");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void temporarilyStorePlayerInformationOnDisk(Player player) {
        ConfigurationSection sectionForPlayer = cfg.createSection(player.getUniqueId().toString());
        sectionForPlayer.set("location", player.getLocation());
        sectionForPlayer.set("gamemode", player.getGameMode().toString());
        sectionForPlayer.set("fireticks", player.getFireTicks());
        sectionForPlayer.set("health", player.getHealth());
        sectionForPlayer.set("hunger", player.getFoodLevel());
        sectionForPlayer.set("saturation", player.getSaturation());
        sectionForPlayer.set("experience", player.getTotalExperience());
        sectionForPlayer.set("air", player.getRemainingAir());
        sectionForPlayer.set("inventory", Arrays.asList(player.getInventory().getContents()));
        sectionForPlayer.set("effects", player.getActivePotionEffects());
        if(player.isInsideVehicle()) {
            sectionForPlayer.set("vehicle.location", player.getVehicle().getLocation());
            sectionForPlayer.set("vehicle.uuid", player.getVehicle().getUniqueId().toString());
        }
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadTemporaryPlayerInformationFromDisk(JavaPlugin plugin, Player player) {
        ConfigurationSection sectionForPlayer = cfg.getConfigurationSection(player.getUniqueId().toString());
        if(sectionForPlayer == null) {
            logger.severe("Attempted to load temporary information for %s but none was found. The challenge is corrupted!".formatted(player));
            return;
        }
        Location location = sectionForPlayer.getLocation("location");
        GameMode gameMode = GameMode.valueOf(sectionForPlayer.getString("gamemode"));
        int fireticks = sectionForPlayer.getInt("fireticks");
        double health = sectionForPlayer.getDouble("health");
        int hunger = sectionForPlayer.getInt("hunger");
        double saturation = sectionForPlayer.getDouble("saturation");
        int exp = sectionForPlayer.getInt("experience");
        int remainingAir = sectionForPlayer.getInt("air");
        List<ItemStack> inventory = (List<ItemStack>) sectionForPlayer.getList("inventory");
        List<PotionEffect> effects = (List<PotionEffect>) sectionForPlayer.getList("effects");
        Location vehicleLocation = sectionForPlayer.getLocation("vehicle.location");
        UUID vehicleUUID = UUID.fromString(sectionForPlayer.getString("vehicle.uuid"));

        // delete temp data for this player
        cfg.set(player.getUniqueId().toString(), null);

        // running it on a different thread may mess things up later...
        //Bukkit.getScheduler().runTask(plugin, () -> {
            boolean teleported = player.teleport(location);
            if(!teleported) {
                logger.severe("Failed to teleport player %s from the MLG world! The challenge is corrupted!".formatted(player.getName()));
            }
            player.setGameMode(gameMode);
            player.setFireTicks(fireticks);
            player.setHealth(health);
            player.setFoodLevel(hunger);
            player.setSaturation((float) saturation);
            player.setTotalExperience(exp);
            player.setRemainingAir(remainingAir);
            player.getInventory().setContents(inventory.stream().toArray(ItemStack[]::new));
            player.addPotionEffects(effects);
            if(vehicleLocation != null) {
                Collection<Entity> entities = vehicleLocation.getWorld().getNearbyEntities(vehicleLocation, 1, 1, 1, e -> e.getUniqueId().equals(vehicleUUID));
                if(entities.isEmpty()) {
                    logger.info("Failed to find the vehicle %s was in. No match found for vehicle %s!".formatted(player.getName(), vehicleUUID.toString()));
                }
                entities.stream().findFirst().orElseThrow().addPassenger(player);
            }
        //});
    }

    public boolean hasOfflinePlayerData(Player player) {
        return cfg.get(player.getUniqueId().toString()) != null;
    }
}
