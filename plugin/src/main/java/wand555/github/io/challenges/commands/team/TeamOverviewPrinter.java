package wand555.github.io.challenges.commands.team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.teams.Team;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class TeamOverviewPrinter {

    public static Component createTeamsOverviewForPlayer(Context context, Player player, @Nullable Team teamPlayerIsIn) {
        return createInfoMessage(context).appendNewline()
                                         .append(createHeader(context)).appendNewline()
                                         .append(createTeamRows(context, player, teamPlayerIsIn)).appendNewline()
                                         .append(createFooter(context));
    }

    private static Component createTeamRows(Context context, Player player, @Nullable Team teamPlayerIsIn) {
        return context.challengeManager().getTeams()
                      .stream()
                      .map(team -> createTeamRow(context, player, teamPlayerIsIn, team))
                      .reduce(Component.empty(), ComponentUtil.NEWLINE_ACCUMULATOR);
    }

    private static Component createTeamRow(Context context, Player player, @Nullable Team teamPlayerIsIn, Team team) {
        Component teamName = createTeamNameWithHover(context, player, teamPlayerIsIn, team);
        Component members = Component.text(getPlayerNames(team).toString());
        return format(context, "team.overview.team", Map.of(
                "team_name", teamName,
                "team_members", members
        ));
    }

    private static Component createTeamNameWithHover(Context context, Player player, @Nullable Team teamPlayerIsIn, Team team) {
        Component teamName = Component.text(team.getTeamName());
        if(Team.getTeamPlayerIn(context, player.getUniqueId()) == Team.ALL_TEAM) {
            // player not yet in a team
            teamName = teamName.hoverEvent(HoverEvent.showText(format(context, "team.overview.team.hover.join")))
                               .clickEvent(ClickEvent.runCommand("/%s join %s".formatted(TeamCommand.BASE_CMD, team.getTeamName())));
        } else {
            // player in a team
            if(team.equals(teamPlayerIsIn)) {
                teamName = teamName.hoverEvent(HoverEvent.showText(format(context, "team.overview.team.hover.leave")))
                                   .clickEvent(ClickEvent.runCommand("/%s leave %s".formatted(TeamCommand.BASE_CMD, teamPlayerIsIn.getTeamName())));
            } else {
                teamName = teamName.hoverEvent(HoverEvent.showText(format(context, "team.overview.team.hover.change")))
                                   .clickEvent(ClickEvent.callback(audience -> {
                                       player.performCommand("%s leave %s".formatted(TeamCommand.BASE_CMD, team.getTeamName()));
                                       player.performCommand("%s join %s".formatted(TeamCommand.BASE_CMD, team.getTeamName()));
                                   }));
            }
        }
        return teamName;
    }

    private static List<String> getPlayerNames(Team team) {
        return team.getPlayers().stream().map(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                return player.getName();
            } else {
                return Bukkit.getOfflinePlayer(uuid).getName();
            }
        }).toList();
    }

    private static Component createInfoMessage(Context context) {
        return format(context, "team.overview.info");
    }

    private static Component createHeader(Context context) {
        return format(context, "team.overview.header");
    }

    private static Component createFooter(Context context) {
        return format(context, "team.overview.footer");
    }

    private static Component format(Context context, String key) {
        return format(context, key, Map.of());
    }

    private static Component format(Context context, String key, Map<String, Component> placeholders) {
        return ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().commandsResourceBundle(),
                key,
                placeholders,
                false
        );
    }
}
