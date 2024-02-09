package wand555.github.io.challenges.criteria.goals;

import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CollectableDataConfig;

import java.util.Objects;

public class Collect implements Storable<CollectableDataConfig> {

    private final int amountNeeded;
    private int currentAmount;

    public Collect(int amountNeeded) {
        this(amountNeeded, 0);
    }

    public Collect(int amountNeeded, int currentAmount) {
        this.amountNeeded = amountNeeded;
        this.currentAmount = currentAmount;
    }

    public int getAmountNeeded() {
        return amountNeeded;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public boolean isComplete() {
        return currentAmount >= amountNeeded;
    }

    @Override
    public CollectableDataConfig toGeneratedJSONClass() {
        return new CollectableDataConfig(amountNeeded, currentAmount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collect collect = (Collect) o;
        return amountNeeded == collect.amountNeeded && currentAmount == collect.currentAmount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amountNeeded, currentAmount);
    }
}
