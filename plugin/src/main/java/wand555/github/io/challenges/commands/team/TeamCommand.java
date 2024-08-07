package wand555.github.io.challenges.commands.team;

import com.google.common.base.Preconditions;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.teams.Team;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static wand555.github.io.challenges.commands.CommandUtil.failWith;
import static wand555.github.io.challenges.commands.CommandUtil.failWrapperWith;

public class TeamCommand {

    private static final Logger logger = ChallengesDebugLogger.getLogger(TeamCommand.class);

    public static final String BASE_CMD = "challengeteams";

    private static final String CMD_NODE_TEAM_ACTION = "team_action";
    private static final String CMD_NODE_TEAM_ID = "team_name";

    public static void registerTeamCommand(Context context) {
        new CommandAPICommand(BASE_CMD)
                .withOptionalArguments(new MultiLiteralArgument(CMD_NODE_TEAM_ACTION,
                                                                "join",
                                                                "leave"
                ).combineWith(customTeamArgument(context)))
                .executesPlayer((sender, args) -> {
                    Team team = args.getByClass(CMD_NODE_TEAM_ID, Team.class);
                    if(!challengeHasTeams(context)) {
                        throw failWrapperWith(context, "team.challenge_has_no_teams");
                    }

                    String teamAction = (String) args.get(CMD_NODE_TEAM_ACTION);
                    if(teamAction == null) {
                        // list all available teams with clickable components to join them (or to leave the current team)
                        handleTeamsOverview(context, sender);
                        return;
                    }
                    if(!challengeIsValidAndLoadedWhenAttemptingToJoinATeam(context)) {
                        throw failWrapperWith(context, "team.not_loaded_or_valid");
                    }
                    if(teamAction.equals("join")) {
                        handleJoinTeam(context, sender, team);
                    } else if(teamAction.equals("leave")) {
                        handleTeamLeave(context, sender);
                    } else {
                        throw new RuntimeException();
                    }
                })
                .register();
    }

    private static Argument<Team> customTeamArgument(Context context) {
        return new CustomArgument<>(new StringArgument(CMD_NODE_TEAM_ID),
                                    info -> {
                                        return getTeamFromTeamName(context, info.input());
                                    }
        ).replaceSuggestions(ArgumentSuggestions.stringsAsync(senderSuggestionInfo -> loadToolTips(context,
                                                                                                   senderSuggestionInfo
        )));
    }

    private static Team getTeamFromTeamName(Context context, String teamName) throws CustomArgument.CustomArgumentException {
        Preconditions.checkArgument(challengeIsValidAndLoadedWhenAttemptingToJoinATeam(context),
                                    "Cannot get the team name when the challenge hasn't been loaded yet (and is valid)!"
        );
        return context.challengeManager().getTeams()
                      .stream()
                      .filter(team -> team.getTeamName().equals(teamName))
                      .findFirst()
                      .orElseThrow(() -> failWith(context, "team.team_does_not_exist"));
    }

    private static CompletableFuture<String[]> loadToolTips(Context context, SuggestionInfo<CommandSender> senderSuggestionInfo) {
        return CompletableFuture.supplyAsync(() -> {
            if(!(senderSuggestionInfo.sender() instanceof Player player)) {
                throw new RuntimeException("Not a player");
            }
            List<Team> allTeams = context.challengeManager().getTeams();
            Team teamPlayerIsIn = Team.getTeamPlayerIn(context, player.getUniqueId());
            if(teamPlayerIsIn == Team.ALL_TEAM) {
                return suggestTeams(allTeams);
            } else {
                return new String[] {};
            }
        });
    }

    private static String[] suggestTeams(List<Team> teams) {
        return teams.stream().map(Team::getTeamName).toArray(String[]::new);
    }

    private static void handleTeamsOverview(Context context, Player player) {
        Team teamPlayerIsIn = Team.getTeamPlayerIn(context, player.getUniqueId());
        Component toSend = TeamOverviewPrinter.createTeamsOverviewForPlayer(context,
                                                                            player,
                                                                            teamPlayerIsIn != Team.ALL_TEAM
                                                                            ? teamPlayerIsIn
                                                                            : null
        );
        player.sendMessage(toSend);
    }

    private static void handleJoinTeam(Context context, Player player, Team team) throws WrapperCommandSyntaxException {
        if(context.challengeManager().getTeams().stream().anyMatch(team1 -> team1.isInTeam(player.getUniqueId()))) {
            throw failWrapperWith(context, "team.already_in_a_team");
        }
        team.addPlayer(player);
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().commandsResourceBundle(),
                "team.joined",
                Map.of("teamname", Component.text(team.getTeamName())),
                true, false, true
        );
        player.sendMessage(toSend);
    }

    private static void handleTeamLeave(Context context, Player player) throws WrapperCommandSyntaxException {
        Team team = Team.getTeamPlayerIn(context, player.getUniqueId());
        if(team == Team.ALL_TEAM) {
            throw failWrapperWith(context, "team.leave_not_in_this_team");
        }
        if(!team.isInTeam(player.getUniqueId())) {
            throw failWrapperWith(context, "team.leave_not_in_this_team");
        }
        team.removePlayer(player);
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().commandsResourceBundle(),
                "team.left",
                Map.of("teamname", Component.text(team.getTeamName())),
                true, false, true
        );
        player.sendMessage(toSend);
    }

    private static boolean challengeIsValidAndLoadedWhenAttemptingToJoinATeam(Context context) {
        return context.challengeManager().isValid();
    }

    private static boolean challengeHasTeams(Context context) {
        Preconditions.checkArgument(challengeIsValidAndLoadedWhenAttemptingToJoinATeam(context),
                                    "Cannot check if challenge has teams, if they weren't loaded yet!"
        );
        return context.challengeManager().hasTeams();
    }

}
