package wand555.github.io.challenges;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.types.Data;

import java.util.Collection;
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

    static <T extends Data<K>, K extends Keyed> TriggerCheck<T> handleIfContains(Collection<K> collection) {
        return data -> collection.contains(data.mainDataInvolved());
    }

    static <T extends Data<K>, K extends Keyed> TriggerCheck<T> ignoreIfContains(Collection<K> collection) {
        return data -> !collection.contains(data.mainDataInvolved());
    }

}
