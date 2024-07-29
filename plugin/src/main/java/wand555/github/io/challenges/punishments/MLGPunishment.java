package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.Abortable;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.MLGPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.mlg.MLGHandler;

public class MLGPunishment extends Punishment implements Abortable, Storable<MLGPunishmentConfig> {

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
    }

    private void handleMLGSuccess(Player player) {

    }

    private void handleMLGFailed(Player player) {

    }

    private void handleMLGAborted(Player player) {

    }

    @Override
    public void abort() {
        //mlgHandler.abortAllMLGs();
    }

    @Override
    public MLGPunishmentConfig toGeneratedJSONClass() {
        return new MLGPunishmentConfig(
                MLGPunishmentConfig.Affects.fromValue(getAffects().getValue()),
                height
        );
    }
}
