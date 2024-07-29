package wand555.github.io.challenges.mlg;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.ConfigValues;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.offline_temp.OfflinePlayerData;
import wand555.github.io.challenges.punishments.InteractionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class MLGHandler implements Listener {

    private final Context context;
    private final OfflinePlayerData offlinePlayerData;
    private Map<Player, BiConsumer<Player, Result>> whenFinished = new HashMap<>();

    public MLGHandler(Context context) {
        this.context = context;
        this.offlinePlayerData = new OfflinePlayerData(context.plugin());
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }


    public void newMLGScenarioFor(Player player, int height, BiConsumer<Player, Result> onFinished) {
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
        }
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

        World mlgWorld = Bukkit.getWorld(ConfigValues.MLG_WORLD.name());
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
        prepareMLGComplete(player, Result.FAILED);
    }

    @EventHandler
    public void onMLGPotentiallyBeatenEvent(PlayerBucketEmptyEvent event) {
        // It may not actually be beaten, maybe the player managed to place the water bucket, but not land in it.
        if(event.getBucket() != Material.WATER_BUCKET) {
            return;
        }

    }

    @EventHandler
    public void onPlayerJoinPlayerIsInMLGWorld(PlayerJoinEvent event) {
        // A player might be "stuck" in the MLG world if the server unexpectedly crashed, or they left when they were doing an MLG.
        if(event.getPlayer().getWorld().equals(Bukkit.getWorld(ConfigValues.MLG_WORLD.name()))) {
            prepareMLGComplete(event.getPlayer(), Result.ABORTED); // abort in favour of fail, it might have been a server crash
        }
    }

    private void prepareMLGComplete(Player player, Result result) {
        offlinePlayerData.loadTemporaryPlayerInformationFromDisk(context.plugin(), player);

        BiConsumer<Player, Result> onFinish = whenFinished.get(player);
        // run a tick later so the player is actually able to receive punishments with their entire state fully loaded from disk
        Bukkit.getScheduler().runTask(context.plugin(), () -> {
            // Mark the player to be able to receive punishments. This will call all queued interactions first.
            InteractionManager.removeUnableToInteract(context, player, result == Result.ABORTED);
            // Run the logic when it's formally completed
            onFinish.accept(player, result);
        });
    }

    public void abortMLG(Player player) {
        getWhenFinished().get(player).accept(player, Result.ABORTED);
    }

    public void abortAllMLGs() {
        getWhenFinished().forEach((player, resultConsumer) -> resultConsumer.accept(player, Result.ABORTED));
    }

    public Map<Player, BiConsumer<Player, Result>> getWhenFinished() {
        return whenFinished;
    }

    public enum Result {
        SUCCESS, FAILED, ABORTED
    }

}
