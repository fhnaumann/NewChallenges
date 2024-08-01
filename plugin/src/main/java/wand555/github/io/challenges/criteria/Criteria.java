package wand555.github.io.challenges.criteria;

public interface Criteria {

    public default void onStart() {}

    public default void onPause() {}

    public default void onResume() {}

    public default void onEnd() {}
}
