package wand555.github.io.challenges.criteria.settings;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.generated.MLGSettingConfig;
import wand555.github.io.challenges.generated.SettingsConfig;
import wand555.github.io.challenges.mlg.MLGHandler;

import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MLGSetting extends BaseSetting implements Storable<MLGSettingConfig>, Listener {

    private static final Logger logger = ChallengesDebugLogger.getLogger(MLGSetting.class);

    private final MLGSettingConfig config;
    private final MLGHandler mlgHandler;
    private final MLGTimer mlgTimer;

    public MLGSetting(Context context, MLGSettingConfig config, MLGHandler mlgHandler) {
        super(context);
        this.config = config;
        this.mlgHandler = mlgHandler;
        this.mlgTimer = new MLGTimer();
    }

    @Override
    public void addToGeneratedConfig(SettingsConfig config) {
        config.setMlgSetting(toGeneratedJSONClass());
    }

    @Override
    public MLGSettingConfig toGeneratedJSONClass() {
        return config;
    }

    @Override
    public void onStart() {
        if(Bukkit.getWorld(ConfigValues.MLG_WORLD.getValueOrDefault(context.plugin())) == null) {
            logger.warning("MLGWorld not found. This should not have happened. Attempting to create the world now.");
            MLGHandler.createOrLoadMLGWorld(context.plugin());
        }
        scheduleNewTimer();
    }

    private void scheduleNewTimer() {
        int time = context.random().nextInt(config.getMinTime(), config.getMaxTime() + 1);
        logger.fine("Scheduled new MLG timer for in %s seconds".formatted(time));
        mlgTimer.runTaskLater(context.plugin(), time);
    }

    @Override
    public void onEnd() {
        mlgTimer.cancel();
    }

    private class MLGTimer extends BukkitRunnable {

        @Override
        public void run() {
            Bukkit.getOnlinePlayers().forEach(player -> {
                mlgHandler.newMLGScenarioFor(player, config.getHeight(), this::handleMLGResult);
            });
        }

        private void handleMLGResult(Player player, MLGHandler.Result mlgResult) {
            mlgHandler.handleMLGResult(player, mlgResult);
            actWhenAllResultsAreIn();
        }

        private void actWhenAllResultsAreIn() {
            if(!mlgHandler.getWhenFinished().isEmpty()) {
                // some players are still in a MLG
                return;
            }
            if(mlgHandler.hasAtLeastOnePlayerFailed()) {
                Component toSend = ComponentUtil.formatChatMessage(
                        context.plugin(),
                        context.resourceBundleContext().punishmentResourceBundle(),
                        "mlg.enforced.fail",
                        Map.of("player",
                               Component.text(mlgHandler.getResults().keySet().stream().map(Player::getName).collect(
                                       Collectors.joining(",")))
                        )
                );
                Bukkit.broadcast(toSend);
                Bukkit.getScheduler().runTask(context.plugin(), () -> {
                    // end challenge as soon as one MLG has failed (after all have been completed)
                    context.challengeManager().endChallenge(false);
                });
            } else {
                Component toSend = ComponentUtil.formatChatMessage(
                        context.plugin(),
                        context.resourceBundleContext().punishmentResourceBundle(),
                        "mlg.enforced.all.success"
                );
                Bukkit.broadcast(toSend);
            }

            scheduleNewTimer();
        }
    }
}
