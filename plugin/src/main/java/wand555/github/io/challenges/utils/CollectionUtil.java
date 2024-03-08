package wand555.github.io.challenges.utils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtil {

    @NotNull
    public static <K,V> Map<K,V> combine(@NotNull Map<K,V> map1, @NotNull Map<K,V> map2) {
        return Stream.of(map1, map2)
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NotNull
    public static <T> List<T> pickN(@NotNull List<T> from, int n) {
        List<T> picked = new ArrayList<>();
        for(int i=0; i<n; i++) {
            int randomIdx = ThreadLocalRandom.current().nextInt(from.size());
            T randomElement = from.get(randomIdx);
            from.remove(randomIdx);
            picked.add(randomElement);
        }
        return picked;
    }
}
