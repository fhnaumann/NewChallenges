package wand555.github.io.challenges.mlg;

import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.ConfigValues;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.offline_temp.OfflinePlayerData;
import wand555.github.io.challenges.punishments.InteractionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public class MLGHandler implements Listener {

    private static final Logger logger = ChallengesDebugLogger.getLogger(MLGHandler.class);

    private final Context context;
    private final OfflinePlayerData offlinePlayerData;
    private Map<Player, BiConsumer<Player, Result>> whenFinished = new HashMap<>();
    private Map<Player, Result> results = new HashMap<>();
    private final World mlgWorld;

    public MLGHandler(Context context, OfflinePlayerData offlinePlayerData) {
        this.context = context;
        this.offlinePlayerData = offlinePlayerData;
        this.mlgWorld = Bukkit.getWorld(ConfigValues.MLG_WORLD.getValueOrDefault(context.plugin()));
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }


    public void newMLGScenarioFor(Player player, int height, BiConsumer<Player, Result> onFinished) {
        InteractionManager.applyInteraction(player, samePlayer -> newMLGScenarioImpl(samePlayer, height, onFinished));
        /*
        if(InteractionManager.isUnableToInteract(player)) {
            // The player is already unable to interact. This could, for example, be the case if they are currently
            // in an MLG and another player (not in an MLG) triggered a MLG punishment for all players. Now this player
            // is currently unable to receive this, but it will be scheduled for when the first MLG is complete.
            InteractionManager.applyInteraction(player, samePlayer -> {
                newMLGScenarioImpl(samePlayer, height, onFinished);
            });
        }
        else {
            newMLGScenarioImpl(player, height, onFinished);
        }*/
    }

    private void newMLGScenarioImpl(Player player, int height, BiConsumer<Player, Result> onFinished) {
        InteractionManager.setUnableToInteract(player, this::abortMLG);
        offlinePlayerData.temporarilyStorePlayerInformationOnDisk(player);
        prepareMLGSetup(player, height);
        whenFinished.put(player, onFinished);
    }

    private void prepareMLGSetup(Player player, int height) {
        player.closeInventory();
        player.getInventory().clear();
        player.getInventory().setItemInMainHand(new ItemStack(Material.WATER_BUCKET));
        player.setGameMode(GameMode.SURVIVAL);

        World mlgWorld = Bukkit.getWorld(ConfigValues.MLG_WORLD.getValueOrDefault(context.plugin()));
        List<Player> players = mlgWorld.getPlayers();
        int xOffset = (int) (height * 0.5 * (players.size()-1));
        Location mlgLocation = new Location(mlgWorld, xOffset, height, 0.5);
        player.teleport(mlgLocation);
    }

    @EventHandler
    public void onMLGFailEvent(EntityDamageEvent event) {
        // The player definitely failed the MLG, otherwise they wouldn't have taken damage.
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }
        if(!whenFinished.containsKey(player)) {
            return;
        }
        event.setCancelled(true);
        prepareMLGComplete(player, Result.FAILED);
    }

    @EventHandler
    public void onMLGPotentiallyBeatenEvent(PlayerBucketEmptyEvent event) {
        // It may not actually be beaten, maybe the player managed to place the water bucket, but not land in it.
        if(event.getBucket() != Material.WATER_BUCKET) {
            return;
        }
        if(!event.getPlayer().getWorld().equals(mlgWorld)) {
            return;
        }
        if(!InteractionManager.isUnableToInteract(event.getPlayer())) {
            logger.severe("MLG complete event triggered by %s, but they're not marked unable. This state should not have been reached!".formatted(event.getPlayer().getName()));
            return;
        }
        // Wait up to a second. If they did not trigger any DamageEvent, then they probably landed the MLG correctly.
        Bukkit.getScheduler().runTaskLater(context.plugin(), () -> {
            // If they are still considered unable to receive events, then the MLG complete was never called, because
            // the player did not take any damage -> they successfully completed it
            if(InteractionManager.isUnableToInteract(event.getPlayer())) {
                // Player succeeded MLG
                prepareMLGComplete(event.getPlayer(), Result.SUCCESS);
            }

            // No matter the result (success/fail/abort), clean up is necessary
            event.getBlock().setType(Material.AIR);

        }, 20L);

    }

    private void prepareMLGComplete(Player player, Result result) {
        this.results.put(player, result);
        offlinePlayerData.loadTemporaryPlayerInformationFromDisk(context.plugin(), player);

        BiConsumer<Player, Result> onFinish = whenFinished.remove(player);
        // run a tick later so the player is actually able to receive punishments with their entire state fully loaded from disk
        Bukkit.getScheduler().runTask(context.plugin(), () -> {
                // Mark the player to be able to receive punishments. This will call all queued interactions first.
                InteractionManager.removeUnableToInteract(context, player, result == Result.ABORTED);

            // Run the logic when it's formally completed
            onFinish.accept(player, result);
        });
    }

    private void abortMLG(Player player) {
        prepareMLGComplete(player, Result.ABORTED);
    }

    public static void handlePlayerJoinedInMLGWorld(JavaPlugin plugin, Player player) {
        logger.warning("Attempting to deal with a player who joined the server being in the MLG world. This shouldn't have happened!");
        OfflinePlayerData offlinePlayerData1 = new OfflinePlayerData(plugin);
        if(!offlinePlayerData1.hasOfflinePlayerData(player)) {
            logger.warning("'%s' has no offline data after they left an ongoing MLG.".formatted(player.getName()));
            return;
        }
        offlinePlayerData1.loadTemporaryPlayerInformationFromDisk(plugin, player);
    }

    public Map<Player, BiConsumer<Player, Result>> getWhenFinished() {
        return whenFinished;
    }

    public boolean hasAtLeastOnePlayerFailed() {
        return results.values().stream().anyMatch(result -> result == Result.FAILED);
    }

    public Map<Player, Result> getResults() {
        return results;
    }

    public static boolean isInMLGWorld(JavaPlugin plugin, Player player) {
        World mlgWorld = Bukkit.getWorld(ConfigValues.MLG_WORLD.getValueOrDefault(plugin));
        return mlgWorld != null && player.getWorld().getName().equals(mlgWorld.getName());
    }

    public static void createOrLoadMLGWorld(JavaPlugin plugin) {
        String mlgWorldName = ConfigValues.MLG_WORLD.getValueOrDefault(plugin);
        boolean mlgWorldExists = Bukkit.getWorld(mlgWorldName) != null;
        if(!mlgWorldExists) {
            logger.info("No MLG World with name '%s' detected. Creating world...".formatted(mlgWorldName));
            createMLGWorld(mlgWorldName);
            logger.info("Created MLG World '%s'!".formatted(mlgWorldName));
        }
        else {
            logger.fine("MLG World '%s' detected. Skipping creation.".formatted(mlgWorldName));
        }


    }

    private static World createMLGWorld(String name) {
        World world = new WorldCreator(name)
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generateStructures(false)
                .keepSpawnLoaded(TriState.TRUE)
                .createWorld();
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        return world;
    }

    public enum Result {
        SUCCESS, FAILED, ABORTED
    }

}
