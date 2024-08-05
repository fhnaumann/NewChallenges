package wand555.github.io.challenges.commands;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;

public class CommandUtil {

    public static WrapperCommandSyntaxException failWrapperWith(Context context, String pathInResourceBundle) {
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

    public static CustomArgument.CustomArgumentException failWith(Context context, String pathInResourceBundle) {
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
