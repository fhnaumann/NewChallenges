package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class InteractionManager {

    private static final Logger logger = ChallengesDebugLogger.getLogger(InteractionManager.class);

    private record UnableInfo(Consumer<Player> interaction, Consumer<Player> whenAborted) {}

    private static final Map<Player, UnableInfo> queuedInteractionMap = new HashMap<>();

    public static void setUnableToInteract(Player player, Consumer<Player> whenAborted) {
        if(queuedInteractionMap.containsKey(player)) {
            logger.severe("Cannot mark a player unable while they are already marked!");
            return;
        }
        queuedInteractionMap.put(player, new UnableInfo(player1 -> {}, whenAborted));
        logger.fine("%s is now unable to be punished.".formatted(player.getName()));
    }

    public static void applyInteraction(Player player, Consumer<Player> interaction) {
        if(queuedInteractionMap.containsKey(player)) {
            logger.fine("%s is scheduled to receive their punishment but is currently unable to.".formatted(player.getName()));
            queuedInteractionMap.merge(
                    player,
                    new UnableInfo(interaction, queuedInteractionMap.get(player).whenAborted),
                    (unableInfo, unableInfo2) -> new UnableInfo(unableInfo.interaction().andThen(unableInfo2.interaction()), unableInfo.whenAborted()));
        }
        else {
            logger.fine("%s is able to receive their punishment.".formatted(player.getName()));
            interaction.accept(player);
        }
    }

    public static void removeUnableToInteract(Context context, Player player, boolean aborted) {
        if(player == null) {
            // The player has left between a punishment trigger (for which they should receive a punishment)
            // and them now being able to be punished.
            // No key in the map should exist for this player, because it should have been deleted
            // when they left the server.
            return;
        }
        logger.fine("%s is now able to be punished. Aborted: %s?".formatted(player.getName(), aborted));
        UnableInfo unableInfo = queuedInteractionMap.remove(player);
        if(unableInfo == null) {
            // no punishments occurred while the player was unable to receive them
            return;
        }
        if(aborted) {
            logger.fine("Abort whatever %s is busy with.".formatted(player.getName()));
            unableInfo.whenAborted.accept(player);
        }

        logger.fine("Applying punishment for %s.".formatted(player.getName()));
        unableInfo.interaction.accept(player);

        Component delayed = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "misc.delayed",
                false
        );
        player.sendMessage(delayed);

    }

    public static boolean isUnableToInteract(Player player) {
        return queuedInteractionMap.containsKey(player);
    }
}
