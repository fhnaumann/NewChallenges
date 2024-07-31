package wand555.github.io.challenges.utils;

import org.bukkit.plugin.java.JavaPlugin;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.ChallengesDebugLogger;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtil {

    public static <T> List<T> filter(Class<T> clazzToFilterAgainst, Collection<?>... collections) {
        if(collections == null) {
            throw new RuntimeException("Collections are null!");
        }
        return Stream.of(collections)
                     .flatMap(Collection::stream)
                     .filter(clazzToFilterAgainst::isInstance)
                     .map(clazzToFilterAgainst::cast)
                     .toList();
    }

    public static <T> void throwIfContainsNull(@NotNull Collection<T> collection, String throwMsg) throws RuntimeException {
        for(T value : collection) {
            if(value == null) {
                throw new RuntimeException(throwMsg);
            }
        }
    }

    @NotNull
    public static <K, V> Map<K, V> combine(@NotNull Map<K, V> map1, @NotNull Map<K, V> map2) {
        return Stream.of(map1, map2)
                     .flatMap(m -> m.entrySet().stream())
                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NotNull
    public static <T> List<T> pickN(@NotNull List<T> from, int n, @NotNull Random random) {
        from = new ArrayList<>(from);
        List<T> picked = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            int randomIdx = random.nextInt(from.size());
            T randomElement = from.get(randomIdx);
            from.remove(randomIdx);
            picked.add(randomElement);
        }
        return picked;
    }
}
