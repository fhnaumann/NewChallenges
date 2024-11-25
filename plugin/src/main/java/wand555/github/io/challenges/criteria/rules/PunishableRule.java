package wand555.github.io.challenges.criteria.rules;


import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.generated.PunishmentsDataConfig;
import wand555.github.io.challenges.mapping.CriteriaMapper;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.types.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.util.*;

public abstract class PunishableRule<T extends Data<?, K>, K> extends Rule implements Triggable<T> {

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

    @Override
    public Trigger<T> trigger() {
        return data -> {
            messageHelper.sendViolationAction(data);
            List<Object> punishmentLiveData = enforcePunishments(data); // Type 'Object' is really 'BasePunishmentDataConfig'
            //Object mcEventData = constructMCEventData(data, punishmentLiveData);
            return;
        };
    }

    private List<Object> enforcePunishments(Data<?, K> data) {
        List<Object> appliedPunishments = new ArrayList<>();
        // enforce local punishments
        for(Punishment localPunishment : getPunishments()) {
            Object result = localPunishment.enforcePunishment(data);
            appliedPunishments.add(result);
        }
        // enforce global punishments
        for(Punishment globalPunishment : context.challengeManager().getGlobalPunishments()) {
            Object result = globalPunishment.enforcePunishment(data);
            appliedPunishments.add(result);
        }
        return appliedPunishments;
    }

    protected final @Nullable PunishmentsConfig toPunishmentsConfig() {
        if(punishments.isEmpty()) {
            return null;
        }
        PunishmentsConfig punishmentsConfig = new PunishmentsConfig();
        punishments.forEach(punishment -> punishment.addToGeneratedConfig(punishmentsConfig));
        return punishmentsConfig;
    }

    protected final PunishmentsDataConfig toPunishmentsDataConfig() {
        PunishmentsDataConfig punishmentsDataConfig = new PunishmentsDataConfig();
        //punishments.forEach(punishment -> punishment.add);
        return punishmentsDataConfig;
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
        PunishableRule<?, ?> that = (PunishableRule<?, ?>) o;
        return Objects.equals(punishments, that.punishments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(punishments);
    }
}
