package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.EndPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.teams.Team;

import java.util.Map;
import java.util.UUID;

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
    public void enforceCauserPunishment(UUID causer) {
        if(Bukkit.getPlayer(causer) != null) {
            Bukkit.getPlayer(causer).setGameMode(GameMode.SPECTATOR);
            Component toSend = ComponentUtil.formatChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().punishmentResourceBundle(),
                    "end.enforced.causer",
                    Map.of("player", Component.text(Bukkit.getPlayer(causer).getName()))
            );
            context.plugin().getServer().broadcast(toSend);
        }

    }

    @Override
    public void enforceAllPunishment(Team team) {
        context.challengeManager().failChallengeFor(team);
        // no own message, because the messages about the challenge failure will be sent anyway.
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }
}
