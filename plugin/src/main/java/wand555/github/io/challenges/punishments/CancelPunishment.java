package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CancelPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.teams.Team;
import wand555.github.io.challenges.types.Data;

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
    public <E extends Event, K> void enforceCauserPunishment(Data<E, K> data) {
        if(data.event() instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }
    }

    @Override
    public <E extends Event, K> void enforceAllPunishment(Data<E, K> data, Team team) {
        if(data.event() instanceof Cancellable cancellable) {
            cancellable.setCancelled(true);
        }
    }
}
