package wand555.github.io.challenges.criteria.rules;


import org.bukkit.entity.Player;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.punishments.Punishment;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class PunishableRule<T, E extends Enum<E>> extends Rule implements Triggable<T> {

    protected @NotNull List<Punishment> punishments;

    protected final RuleMessageHelper<T, E> messageHelper;


    public PunishableRule(Context context, RuleMessageHelper<T, E> messageHelper) {
        this(context, null, messageHelper);
    }

    public PunishableRule(Context context, PunishmentsConfig punishmentsConfig, RuleMessageHelper<T, E> messageHelper) {
        super(context);
        this.punishments = punishmentsConfig != null ? ModelMapper.mapToPunishments(context, punishmentsConfig) : new ArrayList<>();
        this.messageHelper = messageHelper;
    }

    @Override
    public Trigger<T> trigger() {
        return data -> {
            messageHelper.sendViolationAction(data);
            enforcePunishments(playerFrom(data));
        };
    }

    protected abstract Player playerFrom(T data);

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
