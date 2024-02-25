package wand555.github.io.challenges.criteria.rules;


import org.bukkit.entity.Player;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.punishments.Punishment;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

public abstract class PunishableRule extends Rule {

    protected @NotNull List<Punishment> punishments;


    public PunishableRule(Context context) {
        this(context, List.of());
    }

    public PunishableRule(Context context, @NotNull List<Punishment> punishments) {
        super(context);
        this.punishments = punishments;
    }

    public void enforcePunishments(Player causer) {
        getPunishments().forEach(punishment -> punishment.enforcePunishment(causer));
    }

    protected final @Nullable PunishmentsConfig toPunishmentsConfig() {
        if(punishments.isEmpty()) {
            return null;
        }
        PunishmentsConfig punishmentsConfig = new PunishmentsConfig();
        punishments.forEach(punishment -> punishment.addToGeneratedConfig(punishmentsConfig));
        return punishmentsConfig;
    }

    public @NotNull List<Punishment> getPunishments() {
        return punishments;
    }

    public void setPunishments(@NotNull List<Punishment> punishments) {
        this.punishments = punishments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PunishableRule that = (PunishableRule) o;
        return Objects.equals(punishments, that.punishments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(punishments);
    }
}