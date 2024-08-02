package wand555.github.io.challenges.criteria;

import wand555.github.io.challenges.teams.Team;

public interface Criteria {

    public default void onStart() {}

    public default void onStart(Team team) {}

    public default void onPause() {}

    public default void onResume() {}

    public default void onEnd() {}

    public default void onEnd(Team team) {}
}
