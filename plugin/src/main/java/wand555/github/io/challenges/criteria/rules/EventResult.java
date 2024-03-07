package wand555.github.io.challenges.criteria.rules;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import wand555.github.io.challenges.types.EventContainer;

public interface EventResult<E extends Event & Cancellable> {

    EventContainer<E> onEventAction();
}
