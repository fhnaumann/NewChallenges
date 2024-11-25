package wand555.github.io.challenges;

@FunctionalInterface
public interface Trigger<T> {
    public void actOnTriggered(T data);

    public static <T> Trigger<T> pass() {
        return data -> {};
    }
}
