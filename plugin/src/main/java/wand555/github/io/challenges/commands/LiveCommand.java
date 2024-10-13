package wand555.github.io.challenges.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.StringTooltip;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.files.ChallengeFilesHandler;
import wand555.github.io.challenges.utils.ResourceBundleHelper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static wand555.github.io.challenges.commands.CommandUtil.failWith;
import static wand555.github.io.challenges.commands.CommandUtil.failWrapperWith;

public class LiveCommand {

    private static final Logger logger = ChallengesDebugLogger.getLogger(LiveCommand.class);

    private static final String CMD_NODE_NAME = "challenge_name";

    public static void registerLiveCommand(Context context, ChallengeFilesHandler challengeFilesHandler) {
        new CommandAPICommand("live")
                .withOptionalArguments(customLiveArgument(context, challengeFilesHandler))
                .executes((sender, args) -> {
                    ChallengeFilesHandler.ChallengeLoadStatus prepareForLive = args.getByClass(CMD_NODE_NAME,
                                                                                               ChallengeFilesHandler.ChallengeLoadStatus.class
                    );
                    if(prepareForLive == null) {
                        if(!context.challengeManager().isValid()) {
                            throw failWrapperWith(context, "live.no_challenge_loaded");
                        }
                        prepareForLive = new ChallengeFilesHandler.ChallengeLoadStatus(challengeFilesHandler.getFileBeingPlayed(),
                                                                                       context.challengeManager().getChallengeMetadata()
                        );
                    }
                    Component uploadingComp = ComponentUtil.formatChallengesPrefixChatMessage(context.plugin(),
                                                                                              context.resourceBundleContext().commandsResourceBundle(),
                                                                                              "live.uploading"
                    );
                    sender.sendMessage(uploadingComp);
                    uploadChallenge(context, prepareForLive, sender);
                }).register();
    }

    private static Argument<ChallengeFilesHandler.ChallengeLoadStatus> customLiveArgument(Context context, ChallengeFilesHandler challengeFilesHandler) {
        return new CustomArgument<>(new StringArgument(CMD_NODE_NAME), info -> {
            List<ChallengeFilesHandler.ChallengeLoadStatus> loadStatuses = challengeFilesHandler.getChallengesInFolderStatus();
            ChallengeFilesHandler.ChallengeLoadStatus matchingChallenge = loadStatuses.stream()
                                                                                      .filter(challengeLoadStatus -> info.input().equals(
                                                                                              challengeLoadStatus.challengeMetadata().getName()))
                                                                                      .findFirst()
                                                                                      .orElseThrow(() -> failWith(
                                                                                              context,
                                                                                              "live.no_matching_challenge"
                                                                                      ));
            return matchingChallenge;

        }).replaceSuggestions(ArgumentSuggestions.stringsAsync(senderSuggestionInfo -> loadToolTips(
                challengeFilesHandler)));
    }

    private static CompletableFuture<String[]> loadToolTips(ChallengeFilesHandler challengeFilesHandler) {
        return CompletableFuture.supplyAsync(() -> challengeFilesHandler.getChallengesInFolderStatus().stream()
                                                                        .map(challengeLoadStatus -> challengeLoadStatus.challengeMetadata().getName()
                                                                        ).toArray(String[]::new));
    }

    public static CompletableFuture<Void> uploadChallenge(Context context, ChallengeFilesHandler.ChallengeLoadStatus prepareForLive, CommandSender sender) {
        return context.liveService().challengeUploader().uploadChallenge(prepareForLive.challengeMetadata(), prepareForLive.file())
               .exceptionally(throwable -> {
                   logger.severe(throwable.toString());
                   Component errorComp = ComponentUtil.formatChallengesPrefixChatMessage(context.plugin(),
                                                                                         context.resourceBundleContext().commandsResourceBundle(),
                                                                                         "live.uploading_error"
                   );
                   sender.sendMessage(errorComp);
                   return null;
               })
               .whenComplete((unused, throwable) -> {
                   if(throwable != null) {
                       return;
                   }
                   String challengeID = prepareForLive.challengeMetadata().getChallengeID();
                   String url_text = ResourceBundleHelper.getFromBundle(context.plugin(),
                                                                        context.resourceBundleContext().commandsResourceBundle(),
                                                                        "live.url_text"
                   );

                   Component uploadSuccess = ComponentUtil.formatChallengesPrefixChatMessage(
                           context.plugin(),
                           context.resourceBundleContext().commandsResourceBundle(),
                           "live.uploading_done",
                           Map.of(
                                   "url",
                                   Component.text(url_text).clickEvent(ClickEvent.openUrl(
                                           "%s/challenge/%s".formatted(ComponentUtil.LIVE_ACTUAL_URL,
                                                                       challengeID
                                           ))),
                                   "live_website_url",
                                   ComponentUtil.LIVE_LINK,
                                   "challenge_id",
                                   Component.text(challengeID)
                           )
                   );
                   sender.sendMessage(uploadSuccess);
               });
    }

}
