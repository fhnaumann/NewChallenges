package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.HealthPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.mapping.NullHelper;
import wand555.github.io.challenges.teams.Team;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HealthPunishment extends Punishment implements Storable<HealthPunishmentConfig> {

    private final int heartsLost;
    private final boolean randomizeHeartsLost;

    private final int minimumHeartsLost, maximumHeartsLost;

    public HealthPunishment(Context context, HealthPunishmentConfig config) {
        super(context, map(config.getAffects()));
        this.heartsLost = config.getHeartsLost();
        this.randomizeHeartsLost = config.isRandomizeHeartsLost();
        this.minimumHeartsLost = NullHelper.minValue(context.schemaRoot(), "HealthPunishmentConfig", "heartsLost");
        this.maximumHeartsLost = NullHelper.maxValue(context.schemaRoot(), "HealthPunishmentConfig", "heartsLost");

    }

    @Override
    public void enforceCauserPunishment(UUID causer) {
        int damageAmount = getCalculatedHeartsLost();
        enforceOnReceiver(causer, damageAmount);
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().punishmentResourceBundle(),
                "health.enforced.causer",
                Map.of("player", Component.text(Bukkit.getOfflinePlayer(causer).getName()),
                       "amount", Component.text(Integer.toString(damageAmount))
                )
        );
        context.plugin().getServer().broadcast(toSend);
    }

    @Override
    public void enforceAllPunishment(Team team) {
        int damageAmount = getCalculatedHeartsLost();
        team.getAllOnlinePlayers().forEach(player -> InteractionManager.applyInteraction(player,
                                                                                        receiver -> enforceOnReceiver(
                                                                                                receiver.getUniqueId(),
                                                                                                damageAmount
                                                                                        )
        ));
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().punishmentResourceBundle(),
                "health.enforced.all",
                Map.of("amount", Component.text(Integer.toString(damageAmount)))
        );
        context.plugin().getServer().broadcast(toSend);
    }

    private void enforceOnReceiver(UUID receiver, double amount) {
        if(Bukkit.getPlayer(receiver) != null) {
            Bukkit.getPlayer(receiver).damage(amount);
        }

    }

    @Override
    public void addToGeneratedConfig(PunishmentsConfig generatedPunishmentsConfig) {
        generatedPunishmentsConfig.setHealthPunishment(toGeneratedJSONClass());
    }

    private int getCalculatedHeartsLost() {
        if(!randomizeHeartsLost) {
            return heartsLost;
        }
        return context.random().nextInt(minimumHeartsLost, maximumHeartsLost + 1);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        if(!super.equals(o)) {
            return false;
        }
        HealthPunishment that = (HealthPunishment) o;
        return heartsLost == that.heartsLost && randomizeHeartsLost == that.randomizeHeartsLost && minimumHeartsLost == that.minimumHeartsLost && maximumHeartsLost == that.maximumHeartsLost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), heartsLost, randomizeHeartsLost, minimumHeartsLost, maximumHeartsLost);
    }

    @Override
    public HealthPunishmentConfig toGeneratedJSONClass() {
        return new HealthPunishmentConfig(
                HealthPunishmentConfig.Affects.fromValue(getAffects().getValue()),
                heartsLost,
                randomizeHeartsLost
        );
    }

    @Override
    public Component getCurrentStatus() {
        return ComponentUtil.formatChatMessage(
                                    context.plugin(),
                                    context.resourceBundleContext().punishmentResourceBundle(),
                                    "health.name"
                            )
                            .append(Component.text(": "))
                            .append(
                                    ComponentUtil.formatChatMessage(
                                            context.plugin(),
                                            context.resourceBundleContext().ruleResourceBundle(),
                                            "health.statusinfo",
                                            Map.of(
                                                    "affects", Component.text(getAffects().getValue()),
                                                    "amount", Component.text(heartsLost)
                                            )
                                    )
                            );
    }
}
