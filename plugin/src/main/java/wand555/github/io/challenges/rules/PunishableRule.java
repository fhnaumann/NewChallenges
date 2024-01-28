package wand555.github.io.challenges.rules;


import org.bukkit.entity.Player;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.HealthPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.punishments.HealthPunishment;
import wand555.github.io.challenges.punishments.Punishment;
import wand555.github.io.challenges.punishments.RandomEffectPunishment;

import javax.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class PunishableRule extends Rule {

    protected @NotNull List<Punishment> punishments;


    public PunishableRule(Challenges plugin, ResourceBundle ruleResourceBundle) {
        this(plugin, ruleResourceBundle, List.of());
    }

    public PunishableRule(Challenges plugin, ResourceBundle ruleResourceBundle, @NotNull List<Punishment> punishments) {
        super(plugin, ruleResourceBundle);
        this.punishments = punishments;
    }

    public void enforcePunishments(Player causer) {
        getPunishments().forEach(punishment -> punishment.enforcePunishment(causer));
    }

    protected final PunishmentsConfig toPunishmentsConfig() {
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

    @Override
    public String toString() {
        return "PunishableRule{" +
                "punishments=" + punishments +
                ", plugin=" + plugin +
                ", rulesResourceBundle=" + rulesResourceBundle +
                '}';
    }
}
