package wand555.github.io.challenges.criteria.goals;

import wand555.github.io.challenges.exceptions.UnskippableException;

public interface Skippable {

    public void onSkip() throws UnskippableException;

    String getSkipNameInCommand();
}
