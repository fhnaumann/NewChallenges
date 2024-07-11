package wand555.github.io.challenges.types.death;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.types.EventContainer;
import wand555.github.io.challenges.types.Type;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.regex.Matcher;

public class DeathType extends Type<DeathData> {

    public static final Logger logger = ChallengesDebugLogger.getLogger(DeathType.class);

    /*
     Temporarily store the deathMessage from the PlayerDeathEvent to provide it, once the death is confirmed.
     "Confirmed" means that the player did not use a totem to prevent death.
     */
    private DeathMessage deathMessage;

    public DeathType(Context context, TriggerCheck<DeathData> triggerCheck, Trigger<DeathData> whenTriggered) {
        super(context, triggerCheck, whenTriggered, Map.of());
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        String plainDeathMessage = PlainTextComponentSerializer.plainText().serializeOrNull(event.deathMessage());
        logger.info("Received death message '%s'.".formatted(plainDeathMessage));

        /*
        Slight dilemma: Some messages contain other message completely. For example:
        (?<player>.*?) was shot by (?<mob>.*?)
        (?<player>.*?) was shot by (?<mob>.*?) using (?<item>.*?)
        If the death message is "wand555 was shot by Skeleton", then the regex matcher would match both, but we only want the first.
        To circumvent that, we can check if the <item> group matched anything.
        If it did not match (<item> group returned null), then the death message is the first one.
        If it did match (<item> group returned not null), then the death message is the second one. This case should still return false
        in general, because it will match the proper message anyway in the next iteration, because the actual death message contains
        "using an_item".
         */
        /*
        Predicate<DeathMessage> predicate = sourceDeathMessage -> {
            Matcher matcher = sourceDeathMessage.getMatcherFor(plainDeathMessage);
            boolean matches = matcher.matches();
            try {
                if (matches) {


                    try {
                        // TODO: does not work

                        // probably a very bad approach relying on the error being thrown as model logic...
                        boolean mobGroupFound = matcher.group("mob") != null;
                        if (mobGroupFound) {
                            // it's the long one with a mob, therefore don't match
                            return false;
                        }
                        boolean itemGroupFound = matcher.group("item") != null;
                        if (itemGroupFound) {
                            // it's the long one with an item, therefore don't match
                            return false;
                        } else {
                            // it's actually the short one - technically impossible to reach as it should have thrown an exception
                            return true;
                        }
                    } catch (IllegalArgumentException e) {
                        // it's actually the short one
                        return true;
                    }

                } else {
                    return false;
                }
            } catch (IllegalStateException e) {
                logger.severe("The regex check for %s failed with %s".formatted(plainDeathMessage, e.getMessage()));
            }
            // something terrible happened, don't match
            return false;
        };*/

        try {
            deathMessage = matchDeathMessage(context, plainDeathMessage);
            logger.info("Matched death message entry '%s: %s:'".formatted(deathMessage.getCode(), deathMessage.getMessage()));
        } catch (Exception e) {
            logger.severe("Error matching death message '%s'".formatted(plainDeathMessage));
            logger.severe(e.getMessage());
        }

    }

    @EventHandler
    public void onPlayerResurrectEvent(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        triggerIfCheckPasses(new DeathData(player, deathMessage), event);
    }

    private DeathMessage matchDeathMessage(Context context, String plainDeathMessage) {
        DeathMessage actualMatchingDeathMessage;
        List<DeathMessage> matchingMessages = context.dataSourceContext().deathMessageList().stream()
                .filter(deathMessage -> deathMessage.getMatcherFor(plainDeathMessage).matches())
                .toList();

        /*
        If the actual message is "wand555 went off with a bang", then it could be:

        [player] went off with a bang
        [player] went off with a bang due to a firework fired from [item] by [mob]
        [player] went off with a bang while fighting [mob]

        Now check if the "item" or "mob" group exist. If mob and item exist, then it's the second. If only mob exists,
        then it's the third. If only item exists, then throw an error. If none exist, then it's the first
         */
        List<DeathMessage> sortedByGroupCount = matchingMessages.stream()
                //.map(deathMessage -> deathMessage.getMatcherFor(plainDeathMessage).groupCount())
                .sorted(Collections.reverseOrder(Comparator.comparingInt(value -> value.getMatcherFor(plainDeathMessage).groupCount())))
                .toList();
        if (sortedByGroupCount.isEmpty()) {
            throw new RuntimeException("No death message found for '%s'".formatted(plainDeathMessage));
        } else if (sortedByGroupCount.size() == 1) {
            actualMatchingDeathMessage = sortedByGroupCount.get(0); // easy,there is only one match
        } else {
            /*
            Sorted by group count:

            [player] went off with a bang
            [player] went off with a bang while fighting [mob]
            [player] went off with a bang due to a firework fired from [item] by [mob]
             */

            DeathMessage deathMessageWithMostGroups = sortedByGroupCount.get(0);
            Matcher matcher = deathMessageWithMostGroups.getMatcherFor(plainDeathMessage);
            matcher.matches(); // re-run so group checks work
            actualMatchingDeathMessage = switch (matcher.groupCount()) {
                case 3 -> {
                    // deathMessage could be: [player] went off with a bang due to a firework fired from [item] by [mob]
                    // that means the sortedByGroupCount list has 3 entries because when a message with an item exist, that
                    // automatically means that another message with just the mob exists
                    boolean itemGroup = matcher.group("item") != null;
                    boolean mobGroup = matcher.group("mob") != null;
                    if (itemGroup && mobGroup) {
                        // both mob and item exist, it can only be the long one
                        yield deathMessageWithMostGroups;
                    } else if (!itemGroup && mobGroup) {
                        // only mob exists, it can only be the middle one
                        yield sortedByGroupCount.get(1);
                    } else if (itemGroup) {
                        throw new RuntimeException("'%s' has an [item] placeholder, but no '[mob]' placeholder!".formatted(deathMessageWithMostGroups));
                    } else {
                        // neither one exists, it can only be the short one
                        yield sortedByGroupCount.get(2);
                    }
                }
                case 2 -> {
                    // deathMessage could be: [player] was squashed by a falling anvil while fighting [mob]
                    // that means the sortedByGroupCount list has 2 entries because when a message with a mob exist, but not with an item,
                    // there is also the "base" version without the mob
                    boolean mobGroup = matcher.group("mob") != null;
                    if (mobGroup) {
                        // mob exists, it can only be the first entry from the list
                        yield deathMessageWithMostGroups;
                    } else {
                        // mob does not exist, it can only be the short one
                        yield sortedByGroupCount.get(1);
                    }
                }
                case 1, 0 ->
                        throw new RuntimeException("Death message '%s' has 0 or 1 group. It should have matched earlier and never come to this stage!".formatted(deathMessageWithMostGroups));
                default ->
                        throw new RuntimeException("Death message '%s' has more groups (>4) than expected!".formatted(deathMessageWithMostGroups));
            };
        }

        if(actualMatchingDeathMessage.getCode().equals("death.attack.cramming.player")) {
            /*
            Edge case:
            If the message contains "was squashed by", then it could be:
            "death.attack.cramming.player": "[player] was squashed by [mob]"
            "death.attack.anvil": "[player] was squashed by a falling anvil"
            "death.attack.fallingBlock": "(?<player>.*?) was squashed by a falling block"
            or one of its variants containing a mob.
            The problem here is, that "[player] was squashed by [mob]" also matches, even if the death message contains explicitly
            "was squashed by a falling anvil" or "was squashed by a falling block", because the regex group for "[mob]"
            matches anything. We need to handle these two cases separately to prevent "[player] was squashed by [mob]" from
            greedily matching it when it really shouldn't.
            If a player really wants to exploit this, then there is no solution against it. The player could name a mob
            "a falling anvil" or "a falling block" and this method would return the "death.attack.anvil" or
            "death.attack.fallingBlock" when it was "death.attack.cramming.player" in reality. But what are the odds of this O_O
             */
            if(plainDeathMessage.endsWith("falling anvil")) {
                return DataSourceJSON.fromCode(context.dataSourceContext().deathMessageList(), "death.attack.anvil");
            }
            else if(plainDeathMessage.endsWith("a falling block")) {
                return DataSourceJSON.fromCode(context.dataSourceContext().deathMessageList(), "death.attack.fallingBlock");
            }
            else {
                return actualMatchingDeathMessage;
            }
        }

        return actualMatchingDeathMessage;
    }
}
