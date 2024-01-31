package wand555.github.io.challenges.punishments;

import com.fasterxml.jackson.databind.JsonNode;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.HealthPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class HealthPunishment extends Punishment implements Storable<HealthPunishmentConfig> {

    private final int heartsLost;
    private final boolean randomizeHeartsLost;

    private final int minimumHeartsLost, maximumHeartsLost;

    public HealthPunishment(Context context, HealthPunishmentConfig config) {
        super(context, map(config.getAffects()));
        this.heartsLost = config.getHeartsLost();
        this.randomizeHeartsLost = config.getRandomizeHeartsLost();
        String path = "/definitions/HealthPunishmentConfig/properties/heartsLost";
        JsonNode heartsLostNode = context.schemaRoot().at(path);
        if(heartsLostNode.isMissingNode() || heartsLostNode.path("minimum").isMissingNode() || heartsLostNode.path("maximum").isMissingNode()) {
            throw new IllegalStateException(String.format("Path '%s' is incorrectly formatted. Either it is missing completely, or at the end point are no minimum/maximum fields.", path));
        }
        this.minimumHeartsLost = heartsLostNode.path("minimum").asInt();
        this.maximumHeartsLost = heartsLostNode.path("maximum").asInt();
    }

    @Override
    public void enforcePunishment(Player causer) {
        int damageAmount = getCalculatedHeartsLost();

        String key = "";
        Map<String, Component> placeholders = new HashMap<>();
        placeholders.put("amount", Component.text(Integer.toString(damageAmount)));
        switch(getAffects()) {
            case CAUSER -> {
                causer.damage(damageAmount);
                key = "health.enforced.causer";
                placeholders.put("player", Component.text(causer.getName()));

            }
            case ALL -> {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.damage(damageAmount);
                });
                key = "health.enforced.all";
            }
        }
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().punishmentResourceBundle(),
                key,
                placeholders
        );
        context.plugin().getServer().broadcast(toSend);
    }

    @Override
    public void addToGeneratedConfig(PunishmentsConfig generatedPunishmentsConfig) {
        generatedPunishmentsConfig.setHealthPunishment(toGeneratedJSONClass());
    }

    private int getCalculatedHeartsLost() {
        if(!randomizeHeartsLost) {
            return heartsLost;
        }
        return ThreadLocalRandom.current().nextInt(minimumHeartsLost, maximumHeartsLost+1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
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
