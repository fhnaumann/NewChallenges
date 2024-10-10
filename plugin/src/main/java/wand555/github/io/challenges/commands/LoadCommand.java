package wand555.github.io.challenges.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.commands.team.TeamOverviewPrinter;
import wand555.github.io.challenges.files.ChallengeFilesHandler;
import wand555.github.io.challenges.files.FileManager;
import wand555.github.io.challenges.files.ProgressListener;
import wand555.github.io.challenges.generated.ChallengeMetadata;
import wand555.github.io.challenges.teams.Team;
import wand555.github.io.challenges.utils.ActionHelper;
import wand555.github.io.challenges.utils.ParameterStringBuilder;
import wand555.github.io.challenges.utils.ResourceBundleHelper;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static wand555.github.io.challenges.commands.CommandUtil.failWith;
import static wand555.github.io.challenges.commands.CommandUtil.failWrapperWith;

public class LoadCommand {

    private static final Logger logger = ChallengesDebugLogger.getLogger(LoadCommand.class);

    private static final String CMD_NODE_NAME = "challenge-name";

    private static CompletableFuture<Context> loadingFuture;

    public static void registerLoadCommand(Context context, ChallengeFilesHandler challengeFilesHandler) {
        new CommandAPICommand("load")
                .withOptionalArguments(customLoadArgument(context, challengeFilesHandler))
                .executesPlayer((sender, args) -> {
                    if(loadingFuture != null && !loadingFuture.isDone()) {
                        throw failWrapperWith(context, "load.already_loading");
                    }
                    ChallengeFilesHandler.ChallengeLoadStatus toLoad = args.getByClass(CMD_NODE_NAME,
                                                                                       ChallengeFilesHandler.ChallengeLoadStatus.class
                    );
                    handleLoading(context, challengeFilesHandler, toLoad);
                })
                .register();

    }

    private static Argument<ChallengeFilesHandler.ChallengeLoadStatus> customLoadArgument(Context context, ChallengeFilesHandler challengeFilesHandler) {
        return new CustomArgument<>(new StringArgument(CMD_NODE_NAME), info -> {
            List<ChallengeFilesHandler.ChallengeLoadStatus> loadStatuses = challengeFilesHandler.getChallengesInFolderStatus();
            // attempt to load a challenge by its challenge name (not filename)
            ChallengeFilesHandler.ChallengeLoadStatus challengeLoadStatus = challengeName2Filename(info.input(),
                                                                                                   loadStatuses
            );
            if(challengeLoadStatus == null) {
                // the user provided challenge name does not match any challenge on the server
                throw failWith(context, "load.unknown_challenge_name");
            }
            return challengeLoadStatus;
        }).replaceSuggestions(ArgumentSuggestions.stringsWithTooltipsAsync(commandSenderSuggestionInfo -> loadToolTips(
                challengeFilesHandler)));
    }

    private static CompletableFuture<IStringTooltip[]> loadToolTips(ChallengeFilesHandler challengeFilesHandler) {
        return CompletableFuture.supplyAsync(() -> challengeFilesHandler.getChallengesInFolderStatus().stream()
                                                                        .map(challengeLoadStatus -> StringTooltip.ofString(
                                                                                challengeLoadStatus.challengeMetadata().getName(),
                                                                                challengeLoadStatus.file().getPath()
                                                                        )).toArray(IStringTooltip[]::new));
    }

    public static void handleLoading(Context context, ChallengeFilesHandler challengeFilesHandler, @Nullable ChallengeFilesHandler.ChallengeLoadStatus toLoad) throws WrapperCommandSyntaxException {
        List<ChallengeFilesHandler.ChallengeLoadStatus> loadStatuses = challengeFilesHandler.getChallengesInFolderStatus();
        if(loadStatuses.isEmpty()) {
            // there are 0 challenges on the server
            throw failWrapperWith(context, "load.empty_challenge_list");
        }
        if(toLoad == null) {
            // attempt to load the only challenge on the server
            if(loadStatuses.size() > 1) {
                // there is more than 1 challenge on the server, but the player did not specify which to load :(
                throw failWrapperWith(context, "load.specify_challenge_to_load");
            }
            if(anotherChallengeIsOngoing(context.challengeManager())) {
                // another challenge is ongoing, cancel that first before attempting to load this
                throw failWrapperWith(context, "load.already_running");
            }
            // loadStatuses should contain exactly 1 entry
            toLoad = loadStatuses.get(0);
        }

        if(context.challengeManager().isValid()) {
            // a previous challenges was successfully loaded -> save and unload it
            try {
                FileManager.writeToFile(context.challengeManager(),
                                        new FileWriter(new File(challengeFilesHandler.getFolderContainingChallenges(),
                                                                challengeFilesHandler.getFileNameBeingPlayed()
                                        ))
                );
            } catch(IOException e) {
                logger.severe("Failed to save previous challenge to disk!");
                logger.severe(e.getMessage());
                throw failWrapperWith(context, "load.unknown_error");
            }
            context.challengeManager().unload();
        }

        loadFile(context, challengeFilesHandler, toLoad.file());
    }

    private static void sendLoadingStarted(Context context) {
        // attempt to load the specified challenge
        ActionHelper.showAllTitle(ComponentUtil.formatTitleMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenges.validation.start.title"
        ));
    }

    private static void resetScoreboard() {
        // reset scoreboard teams
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        scoreboard.getTeams().forEach(org.bukkit.scoreboard.Team::unregister);
        Objective objective = scoreboard.getObjective("teams");
        if(objective != null) {
            objective.unregister();
        }

        // how the hell is "/scoreboard objectives setdisplay list" achieved using the spigot API???
        Objective objective1 = scoreboard.getObjective(DisplaySlot.PLAYER_LIST);
        if(objective1 != null) {
            objective1.unregister();
        }
    }

    private static void sendLoadingSuccess(Context context) {
        Component successTitle = ComponentUtil.formatTitleMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenges.validation.success.title"
        );
        Component successSubtitle = ComponentUtil.formatSubTitleMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenges.validation.success.subtitle"
        );
        ActionHelper.showAllTitle(successTitle, successSubtitle);

        Component successChat = ComponentUtil.formatChallengesPrefixChatMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenges.validation.success.chat"
        );
        Bukkit.broadcast(successChat);
        if(context.challengeManager().hasTeams()) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                Team team = Team.getTeamPlayerIn(context, player.getUniqueId());
                Component teamOverview = TeamOverviewPrinter.createTeamsOverviewForPlayer(context,
                                                                                          player,
                                                                                          team != Team.ALL_TEAM
                                                                                          ? team
                                                                                          : null
                );
                player.sendMessage(teamOverview);
            });
        }
    }

    private static void sendLoadingFailed(Context context, LoadValidationException e) {
        Component failureTitle = ComponentUtil.formatSubTitleMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenges.validation.failure.title"
        );
        Component failureSubtitle = ComponentUtil.formatSubTitleMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenges.validation.failure.subtitle"
        );
        ActionHelper.showAllTitle(failureTitle,
                                  failureSubtitle,
                                  Title.Times.times(Duration.ofSeconds(1),
                                                    Duration.ofSeconds(10),
                                                    Duration.ofSeconds(1)
                                  )
        );
        Component failureChat = ComponentUtil.formatChallengesPrefixChatMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenges.validation.failure.chat",
                Map.of(),
                false
        );
        Bukkit.broadcast(failureChat);
        Bukkit.broadcast(e.getValidationResult().asFormattedComponent(context));
    }

    private static void reassignContext(Context old, Context newContext) {
        // Do not delete (this is so ugly...)
        old = newContext;
    }

    private static void handleProgress(Context context, double progress) {
        logger.fine("Loading progress: %s".formatted(progress));
        Component title = ComponentUtil.formatTitleMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenges.validation.start.title"
        );
        Component subtitle = progressBar(context, progress);
        ActionHelper.showAllTitle(title,
                                  subtitle,
                                  Title.Times.times(Duration.ZERO, Duration.ofMinutes(2), Duration.ZERO)
        );
    }

    private static Component progressBar(Context context, double current) {
        int currentInt = (int) (current * 100);
        Component completed = Component.text(IntStream.range(0,
                                                             currentInt
                                             ).mapToObj(i -> ":").collect(Collectors.joining("")),
                                             TextColor.fromHexString(ResourceBundleHelper.getFromBundle(context.plugin(),
                                                                                                        context.resourceBundleContext().commandsResourceBundle(),
                                                                                                        "chat.color.highlight"
                                             ))
        );
        Component missing = Component.text(IntStream.range(currentInt,
                                                           100
                                           ).mapToObj(i -> ":").collect(Collectors.joining("")),
                                           TextColor.fromHexString(ResourceBundleHelper.getFromBundle(context.plugin(),
                                                                                                      context.resourceBundleContext().commandsResourceBundle(),
                                                                                                      "chat.color.default"
                                           ))
        );
        return completed.append(missing);
    }

    public static void loadFile(Context context, ChallengeFilesHandler challengeFilesHandler, File toLoad) {
        sendLoadingStarted(context);
        resetScoreboard();
        loadingFuture = FileManager.readFromFile(toLoad, context, progress -> handleProgress(context, progress))
                                   .exceptionally(throwable -> {
                                       if(throwable instanceof CompletionException completionException && completionException.getCause() instanceof LoadValidationException loadValidationException) {
                                           sendLoadingFailed(context, loadValidationException);
                                           return context;
                                       } else {
                                           logger.severe("Loading failed: %s".formatted(throwable.getMessage()));
                                           return null;
                                       }
                                   })
                                   .whenComplete((newContext, throwable) -> {
                                       if(newContext == null) {
                                           return;
                                       }
                                       reassignContext(context, newContext);
                                       challengeFilesHandler.setFileNameBeingPlayed(toLoad.getName());

                                       // URL Reminder no longer used. Setting it to null causes the main timer runnable to run.
                                       // It may already be null if this is the second time a challenge is unloaded and another one is loaded without
                                       // restarting the server
                                       if(context.plugin().urlReminder != null) {
                                           context.plugin().urlReminder.stop();
                                           context.plugin().urlReminder = null;
                                       }
                                       sendLoadingSuccess(context);

                                       // Set challenge ID for live events
                                       context.liveService().eventProvider().setChallengeID(context.challengeManager().getChallengeMetadata().getChallengeID());
                                   });
    }



    private static @Nullable ChallengeFilesHandler.ChallengeLoadStatus challengeName2Filename(String challengeName, List<ChallengeFilesHandler.ChallengeLoadStatus> statuses) {
        return statuses.stream()
                       .filter(challengeLoadStatus -> challengeLoadStatus.challengeMetadata() != null && challengeLoadStatus.challengeMetadata().getName().equals(
                               challengeName))
                       .findAny()
                       .orElse(null);
    }

    private static boolean anotherChallengeIsOngoing(ChallengeManager manager) {
        return manager.isRunning() || manager.isPaused();
    }
}
