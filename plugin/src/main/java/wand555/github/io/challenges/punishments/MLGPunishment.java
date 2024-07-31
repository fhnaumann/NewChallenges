package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.generated.MLGPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.mlg.MLGHandler;

import java.util.Map;
import java.util.stream.Collectors;

public class MLGPunishment extends Punishment implements Storable<MLGPunishmentConfig> {

    private final int height;
    private final MLGHandler mlgHandler;

    public MLGPunishment(Context context, MLGPunishmentConfig config, MLGHandler mlgHandler) {
        super(context, map(config.getAffects()));
        this.height = config.getHeight();
        this.mlgHandler = mlgHandler;
    }

    @Override
    public void addToGeneratedConfig(PunishmentsConfig config) {
        config.setMlgPunishment(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }

    @Override
    public void enforceCauserPunishment(Player causer) {
        enforceOnReceiver(causer, height);
    }

    @Override
    public void enforceAllPunishment() {
        Bukkit.getOnlinePlayers().forEach(player -> InteractionManager.applyInteraction(player, this::enforceCauserPunishment));
    }

    private void enforceOnReceiver(Player receiver, int height) {
        mlgHandler.newMLGScenarioFor(receiver.getPlayer(), height, this::handleMLGResult);
    }

    private void handleMLGResult(Player player, MLGHandler.Result mlgResult) {
        switch(mlgResult) {
            case SUCCESS -> handleMLGSuccess(player);
            case FAILED -> handleMLGFailed(player);
            case ABORTED -> handleMLGAborted(player);
        }
        actWhenAllResultsAreIn();
    }

    private void handleMLGSuccess(Player player) {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().punishmentResourceBundle(),
                "mlg.enforced.individual.success"
        );
        player.sendMessage(toSend);
    }

    private void handleMLGFailed(Player player) {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().punishmentResourceBundle(),
                "mlg.enforced.individual.fail"
        );
        player.sendMessage(toSend);
    }

    private void handleMLGAborted(Player player) {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().punishmentResourceBundle(),
                "mlg.enforced.individual.abort"
        );
        player.sendMessage(toSend);
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
                    Map.of("player", Component.text(mlgHandler.getResults().keySet().stream().map(Player::getName).collect(Collectors.joining(","))))
            );
            Bukkit.broadcast(toSend);

            switch (getAffects()) {
                case CAUSER -> {
                    mlgHandler.getResults().keySet()
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Causer not found in MLG result handler!"))
                            .setGameMode(GameMode.SPECTATOR);
                }
                case ALL -> {
                    Bukkit.getScheduler().runTask(context.plugin(), () -> {
                        // end challenge as soon as one MLG has failed (after all have been completed)
                        context.challengeManager().endChallenge(false);
                    });
                }
            }
        }
        else {
            switch (getAffects()) {
                case CAUSER -> {
                    Component toSend = ComponentUtil.formatChatMessage(
                            context.plugin(),
                            context.resourceBundleContext().punishmentResourceBundle(),
                            "mlg.enforced.causer.success"
                    );
                    Bukkit.broadcast(toSend);
                }
                case ALL -> {
                    Component toSend = ComponentUtil.formatChatMessage(
                            context.plugin(),
                            context.resourceBundleContext().punishmentResourceBundle(),
                            "mlg.enforced.all.success"
                    );
                    Bukkit.broadcast(toSend);
                }
            }
        }

    }

    @Override
    public MLGPunishmentConfig toGeneratedJSONClass() {
        return new MLGPunishmentConfig(
                MLGPunishmentConfig.Affects.fromValue(getAffects().getValue()),
                height
        );
    }
}
