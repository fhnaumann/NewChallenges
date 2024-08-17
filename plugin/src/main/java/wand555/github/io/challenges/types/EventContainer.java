package wand555.github.io.challenges.types;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public interface EventContainer<E extends Event> {
    void onEvent(E event);
}
