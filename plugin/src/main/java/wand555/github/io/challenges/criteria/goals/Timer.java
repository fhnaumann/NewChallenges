package wand555.github.io.challenges.criteria.goals;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.GoalTimer;
import wand555.github.io.challenges.mapping.NullHelper;

import java.util.concurrent.ThreadLocalRandom;

public class Timer implements Storable<GoalTimer> {

    private final GoalTimer config;

    public Timer(GoalTimer config) {
        this.config = config;
        if(config.getStartingTime() == -1) {
            // The time has not been set before by the server. Doing it now.
            int minTime = NullHelper.notNullOrDefault(config.getMinTimeSeconds(), Integer.class);
            int maxTime = NullHelper.notNullOrDefault(config.getMaxTimeSeconds(), Integer.class);
            int randomizedTime = ThreadLocalRandom.current().nextInt(minTime, maxTime + 1);
            setTime(randomizedTime);
            setStartingTime(randomizedTime);
        }
    }

    public int getTime() {
        return config.getTime();
    }

    public void setTime(int time) {
        config.setTime(time);
    }

    public int getStartingTime() {
        return config.getStartingTime();
    }

    public void setStartingTime(int startingTime) {
        config.setStartingTime(startingTime);
    }

    public int decrementTime() {
        setTime(getTime() - 1);
        return getTime();
    }

    public boolean isFailedDueTimeLimit() {
        return getTime() <= 0;
    }

    public int getOrder() {
        return config.getOrder();
    }

    @Override
    public GoalTimer toGeneratedJSONClass() {
        return config;
    }
}
