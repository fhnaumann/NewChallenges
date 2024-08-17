package wand555.github.io.challenges.types;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.teams.Team;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;

public abstract class Type<T> implements Listener {

    private static final Logger logger = ChallengesDebugLogger.getLogger(Type.class);

    protected final Context context;

    protected final TriggerCheck<T> triggerCheck;
    protected final Trigger<T> whenTriggered;

    public static boolean sameTeamCheck(Context context, BaseGoal baseGoal, Player player) {
        Team team = Team.getTeamPlayerIn(context, player.getUniqueId());
        return team == Team.ALL_TEAM || team.getGoals().contains(baseGoal);
    }

    protected final Map<Class<? extends Event>, EventContainer<? extends Event>> eventContainers;

    public Type(Context context, TriggerCheck<T> triggerCheck, Trigger<T> whenTriggered) {
        this(context, triggerCheck, whenTriggered, Map.of(BlockBreakEvent.class, event -> {}));
    }

    public Type(Context context, TriggerCheck<T> triggerCheck, Trigger<T> whenTriggered, Map<Class<? extends Event>, EventContainer<? extends Event>> eventContainers) {
        this.context = context;
        this.triggerCheck = triggerCheck;
        this.whenTriggered = whenTriggered;
        this.eventContainers = eventContainers;
    }

    public void unload() {
        HandlerList.unregisterAll(this);
    }

    public <E extends Event> void triggerIfCheckPasses(T data, E event) {
        if(triggerCheck.applies(data)) {
            logger.fine("Trigger check applies for %s with data '%s'.".formatted(event.getEventName(), data));
            callEventInContainer(event);
            whenTriggered.actOnTriggered(data);
        }
    }

    protected final <E extends Event> void callEventInContainer(E event) {
        EventContainer<E> eventContainer = (EventContainer<E>) eventContainers.get(event.getClass());
        if(eventContainer != null) {
            logger.fine("Additional action event detected... Calling it now.");
            eventContainer.onEvent(event);
        }
    }
}
