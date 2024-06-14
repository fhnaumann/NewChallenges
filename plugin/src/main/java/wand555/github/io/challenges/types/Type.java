package wand555.github.io.challenges.types;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import java.util.Map;

public abstract class Type<T> implements Listener {

    protected final Context context;

    protected final TriggerCheck<T> triggerCheck;
    protected final Trigger<T> whenTriggered;

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

    protected <E extends Event & Cancellable> void triggerIfCheckPasses(T data, E event) {
        if(triggerCheck.applies(data)) {
            callEventInContainer(event);
            whenTriggered.actOnTriggered(data);
        }
    }

    protected final <E extends Event & Cancellable> void callEventInContainer(E event) {
        EventContainer<E> eventContainer = (EventContainer<E>)eventContainers.get(event.getClass());
        if(eventContainer != null) {
            eventContainer.onEvent(event);
        }
    }
}
