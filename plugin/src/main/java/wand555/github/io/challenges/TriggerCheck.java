package wand555.github.io.challenges;

import org.bukkit.Material;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface TriggerCheck<T> {
    public boolean applies(T data);
}
