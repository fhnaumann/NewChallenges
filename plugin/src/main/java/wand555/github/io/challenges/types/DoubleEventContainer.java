package wand555.github.io.challenges.types;

public interface DoubleEventContainer<E1, E2> {

    void onFirstEvent(E1 event);
    void onSecondEvent(E2 event);
}
