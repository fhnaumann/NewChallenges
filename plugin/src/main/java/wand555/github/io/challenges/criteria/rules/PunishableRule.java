package wand555.github.io.challenges.criteria.rules;


import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.types.EventContainer;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class PunishableRule<T> extends Rule implements Triggable<T> {

    protected @NotNull List<Punishment> punishments;

    protected final Result result;

    protected final RuleMessageHelper<T> messageHelper;


    public PunishableRule(Context context, Result result, RuleMessageHelper<T> messageHelper) {
        this(context, null, result, messageHelper);
    }

    public PunishableRule(Context context, PunishmentsConfig punishmentsConfig, Result result, RuleMessageHelper<T> messageHelper) {
        super(context);
        this.punishments = punishmentsConfig != null ? ModelMapper.mapToPunishments(context, punishmentsConfig) : new ArrayList<>();
        this.result = result;
        this.messageHelper = messageHelper;
    }

    protected final <E extends Event & Cancellable> EventContainer<E> cancelIfDeny() {
        return event -> {
            event.setCancelled(result == Result.DENY);
        };
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

    public Result getResult() {
        return result;
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

    public enum Result {
        DENY("Deny"),
        ALLOW("Allow");

        private String value;

        Result(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Result fromJSONString(String valueInJSONString) {
            switch (valueInJSONString) {
                case "Deny": return DENY;
                case "Allow": return ALLOW;
                default: throw new RuntimeException();
            }
        }
    }
}
