package wand555.github.io.challenges;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Predicate;

public interface TriggerCheck<T> {
    public boolean applies(T data);

    static <T> TriggerCheck<T> pass() {
        return ignored -> true;
    }

    default TriggerCheck<T> and(TriggerCheck<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> this.applies(t) && other.applies(t);
    }

}
