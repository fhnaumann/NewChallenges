package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.EndPunishmentConfig;
import wand555.github.io.challenges.generated.HealthPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;

public class EndPunishment extends Punishment implements Storable<HealthPunishmentConfig> {
    public EndPunishment(Context context, EndPunishmentConfig config) {
        super(context, map(config.getAffects()));
    }

    @Override
    public void addToGeneratedConfig(PunishmentsConfig config) {

    }

    @Override
    public HealthPunishmentConfig toGeneratedJSONClass() {
        return null;
    }

    @Override
    public void enforcePunishment(Player causer) {
        // TODO create backup

        context.challengeManager().endChallenge();
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }
}
