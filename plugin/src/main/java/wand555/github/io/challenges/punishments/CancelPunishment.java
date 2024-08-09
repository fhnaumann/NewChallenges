package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CancelPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.teams.Team;

import java.util.UUID;

public class CancelPunishment extends Punishment implements Storable<CancelPunishmentConfig> {
    public CancelPunishment(Context context, CancelPunishmentConfig config) {
        super(context, Affects.CAUSER);
    }

    @Override
    public void addToGeneratedConfig(PunishmentsConfig config) {
        config.setCancelPunishment(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }

    @Override
    public CancelPunishmentConfig toGeneratedJSONClass() {
        return new CancelPunishmentConfig();
    }

    @Override
    public void enforceCauserPunishment(UUID causer) {
        // empty, cancelling is handled when the type is instantiated (see PunishableRule#cancelIfCancelPunishmentActive)
    }

    @Override
    public void enforceAllPunishment(Team team) {
        // empty, cancelling is handled when the type is instantiated (see PunishableRule#cancelIfCancelPunishmentActive)
    }
}
