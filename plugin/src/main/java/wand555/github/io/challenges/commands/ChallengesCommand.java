package wand555.github.io.challenges.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.ConsoleCommandSender;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.files.ChallengeFilesHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ChallengesCommand {

    private static final Logger logger = ChallengesDebugLogger.getLogger(ChallengesCommand.class);

    private static final String CMD_NODE_LIST = "list";

    public static void registerChallengesCommand(Context context, ChallengeFilesHandler challengeFilesHandler) {
        new CommandAPICommand("challenges")
                .withArguments(new LiteralArgument(CMD_NODE_LIST))
                .executes((sender, args) -> {
                    Component component = formatChallengesInFolders2Component(context,
                                                                              challengeFilesHandler,
                                                                              sender instanceof ConsoleCommandSender
                    );
                    sender.sendMessage(component);
                })
                .register();
    }

    private static Component formatChallengesInFolders2Component(Context context, ChallengeFilesHandler challengeFilesHandler, boolean console) {
        Component fileSymbol = Component.text("\uD83D\uDCDD").appendSpace();
        return challengeFilesHandler.getChallengesInFolderStatus().stream().map(challengeLoadStatus -> {
            Map<String, Component> placeholders = new HashMap<>();
            placeholders.put("name", Component.text(challengeLoadStatus.challengeMetadata().getName()));
            placeholders.put("mc-version",
                             Component.text(challengeLoadStatus.challengeMetadata().getBuilderMCVersion())
            );
            Component formatted = fileSymbol.append(ComponentUtil.formatChatMessage(context.plugin(),
                                                                                    context.resourceBundleContext().commandsResourceBundle(),
                                                                                    "load.list.name",
                                                                                    placeholders,
                                                                                    false
            ));
            if(challengeLoadStatus.file().getName().equals(challengeFilesHandler.getFileNameBeingPlayed())) {
                formatted = Component.text().decorate(TextDecoration.BOLD).append(formatted).asComponent();
                if(console) {
                    formatted = formatted.appendSpace().append(Component.text("<- LOADED"));
                }
            } else {
                formatted = formatted.clickEvent(ClickEvent.suggestCommand("/load %s".formatted(challengeLoadStatus.challengeMetadata().getName())))
                                     .hoverEvent(HoverEvent.showText(ComponentUtil.formatChatMessage(
                                             context.plugin(),
                                             context.resourceBundleContext().commandsResourceBundle(),
                                             "load.list.hover",
                                             false
                                     )));
            }

            return formatted;
        }).reduce(Component.empty(), ComponentUtil.NEWLINE_ACCUMULATOR);
    }

}
