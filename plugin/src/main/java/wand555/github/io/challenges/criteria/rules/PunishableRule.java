package wand555.github.io.challenges.criteria.rules;


import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.mapping.CriteriaMapper;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.punishments.CancelPunishment;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.types.Data;
import wand555.github.io.challenges.types.EventContainer;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class PunishableRule<T extends Data<K>, K> extends Rule implements Triggable<T> {

    protected @NotNull List<Punishment> punishments;

    protected final RuleMessageHelper<T> messageHelper;


    public PunishableRule(Context context, RuleMessageHelper<T> messageHelper) {
        this(context, null, messageHelper);
    }

    public PunishableRule(Context context, PunishmentsConfig punishmentsConfig, RuleMessageHelper<T> messageHelper) {
        super(context);
        this.punishments = punishmentsConfig != null
                           ? CriteriaMapper.mapToPunishments(context, punishmentsConfig)
                           : new ArrayList<>();
        this.messageHelper = messageHelper;
    }

    protected final  <E extends Event & Cancellable> EventContainer<E> cancelIfCancelPunishmentActive() {
        return event -> {
            boolean cancel = Stream.of(context.challengeManager().getGlobalPunishments(), getPunishments())
                                   .flatMap(Collection::stream)
                                   .anyMatch(punishment -> punishment instanceof CancelPunishment);
            event.setCancelled(cancel);
        };

    }

    @Override
    public Trigger<T> trigger() {
        return data -> {
            messageHelper.sendViolationAction(data);
            enforcePunishments(data.player());
        };
    }

    public void enforcePunishments(Player causer) {
        // enforce local punishments
        getPunishments().forEach(punishment -> punishment.enforcePunishment(causer));
        // enforce global punishments
        context.challengeManager().getGlobalPunishments().forEach(punishment -> punishment.enforcePunishment(causer));
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
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        PunishableRule that = (PunishableRule) o;
        return Objects.equals(punishments, that.punishments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(punishments);
    }
}
