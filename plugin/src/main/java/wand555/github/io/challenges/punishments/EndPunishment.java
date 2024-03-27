package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.EndPunishmentConfig;
import wand555.github.io.challenges.generated.HealthPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;

import java.util.HashMap;
import java.util.Map;

public class EndPunishment extends Punishment implements Storable<EndPunishmentConfig> {
    public EndPunishment(Context context, EndPunishmentConfig config) {
        super(context, map(config.getAffects()));
    }

    @Override
    public void addToGeneratedConfig(PunishmentsConfig config) {
        config.setEndPunishment(toGeneratedJSONClass());
    }

    @Override
    public EndPunishmentConfig toGeneratedJSONClass() {
        return new EndPunishmentConfig(EndPunishmentConfig.Affects.fromValue(getAffects().getValue()));
    }

    @Override
    public void enforcePunishment(Player causer) {
        // TODO create backup

        String key = "";
        Map<String, Component> placeholders = new HashMap<>();
        switch (getAffects()) {
            case ALL -> {
                context.challengeManager().endChallenge();
                // no own message, because the messages about the challenge failure will be sent anyway.
            }
            case CAUSER -> {
                causer.setGameMode(GameMode.SPECTATOR);
                Component toSend = ComponentUtil.formatChatMessage(
                        context.plugin(),
                        context.resourceBundleContext().punishmentResourceBundle(),
                        "end.enforced.causer",
                        Map.of("player", Component.text(causer.getName()))
                );
                context.plugin().getServer().broadcast(toSend);
            }
        }

    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }
}
