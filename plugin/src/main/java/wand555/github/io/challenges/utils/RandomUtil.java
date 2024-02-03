package wand555.github.io.challenges.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    public static <T> T pickRandom(Collection<T> from) {
        if(from.isEmpty()) {
            throw new RuntimeException();
        }
        return from.stream().skip((ThreadLocalRandom.current().nextInt(from.size()))).findAny().orElseThrow();
    }



    public static <K,V> Map.Entry<K,V> pickRandom(Map<K,V> from) {
        if(from.isEmpty()) {
            throw new RuntimeException();
        }
        K randomKey = pickRandom(from.keySet());
        V randomValue = from.get(randomKey);
        return Map.entry(randomKey, randomValue);
    }
}
