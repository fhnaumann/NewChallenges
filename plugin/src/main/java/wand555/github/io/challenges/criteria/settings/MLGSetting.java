package wand555.github.io.challenges.criteria.settings;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.generated.MLGSettingConfig;
import wand555.github.io.challenges.generated.SettingsConfig;
import wand555.github.io.challenges.mlg.MLGHandler;
import wand555.github.io.challenges.teams.Team;

import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MLGSetting extends BaseSetting implements Storable<MLGSettingConfig>, Listener {

    private static final Logger logger = ChallengesDebugLogger.getLogger(MLGSetting.class);

    private final MLGSettingConfig config;
    private final MLGHandler mlgHandler;
    private MLGTimer mlgTimer;
    private int taskID;

    public MLGSetting(Context context, MLGSettingConfig config, MLGHandler mlgHandler) {
        super(context);
        this.config = config;
        this.mlgHandler = mlgHandler;
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
        int time = context.random().nextInt(config.getMinTimeSeconds(), config.getMaxTimeSeconds() + 1);
        mlgTimer = new MLGTimer();
        logger.fine("Scheduled new MLG timer for in %s seconds".formatted(time));
        taskID = mlgTimer.runTaskLater(context.plugin(), time * 20L).getTaskId();
    }

    public int getTaskID() {
        Preconditions.checkNotNull(mlgTimer, "No new timer was scheduled yet!");
        return taskID;
    }

    @Override
    public void onPause() {
        mlgTimer.cancel();
    }

    @Override
    public void onResume() {
        scheduleNewTimer();
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
                    context.challengeManager().failChallengeFor(Team.getTeamPlayerIn(context,
                                                                                     mlgHandler.getResults().keySet().stream().findFirst().orElseThrow().getUniqueId()
                    ));
                    // context.challengeManager().endChallenge(false);
                });
            } else {
                Component toSend = ComponentUtil.formatChatMessage(
                        context.plugin(),
                        context.resourceBundleContext().punishmentResourceBundle(),
                        "mlg.enforced.all.success"
                );
                Bukkit.broadcast(toSend);
                scheduleNewTimer();
            }
        }
    }
}
