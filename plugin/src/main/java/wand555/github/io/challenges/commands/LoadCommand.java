package wand555.github.io.challenges.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.files.ChallengeFilesHandler;
import wand555.github.io.challenges.utils.ActionHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class LoadCommand {

    private static final Logger logger = ChallengesDebugLogger.getLogger(LoadCommand.class);

    private static final String CMD_NODE_NAME = "challenge-name";

    public static void registerLoadCommand(Context context, ChallengeFilesHandler challengeFilesHandler) {
        new CommandAPICommand("load")
                .withOptionalArguments(customLoadArgument(context, challengeFilesHandler))
                .executesPlayer((sender, args) -> {
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

        try {
            // attempt to load the specified challenge
            ActionHelper.showAllTitle(ComponentUtil.formatTitleMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenges.validation.start.title"
            ));

            loadFile(context, challengeFilesHandler, toLoad.file());

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

        } catch(LoadValidationException e) {
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

            throw failWrapperWith(context, "load.invalid_challenge");
        }
    }

    public static void loadFile(Context context, ChallengeFilesHandler challengeFilesHandler, File toLoad) throws LoadValidationException {
        Context newContext = FileManager.readFromFile(toLoad, context.plugin());
        // TODO: ugly access
        context.plugin().tempContext = newContext;
        challengeFilesHandler.setFileNameBeingPlayed(toLoad.getName());

        // URL Reminder no longer used. Setting it to null causes the main timer runnable to run.
        // It may already be null if this is the second time a challenge is unloaded and another one is loaded without
        // restarting the server
        if(context.plugin().urlReminder != null) {
            context.plugin().urlReminder.stop();
            context.plugin().urlReminder = null;
        }

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

    private static WrapperCommandSyntaxException failWrapperWith(Context context, String pathInResourceBundle) {
        return CommandAPI.failWithString(
                PlainTextComponentSerializer.plainText().serialize(
                        ComponentUtil.formatChallengesPrefixChatMessage(
                                context.plugin(),
                                context.resourceBundleContext().commandsResourceBundle(),
                                pathInResourceBundle
                        )
                )
        );
    }

    private static CustomArgument.CustomArgumentException failWith(Context context, String pathInResourceBundle) {
        return CustomArgument.CustomArgumentException.fromAdventureComponent(
                ComponentUtil.formatChatMessage(
                        context.plugin(),
                        context.resourceBundleContext().commandsResourceBundle(),
                        pathInResourceBundle,
                        true
                )
        );
    }
}
