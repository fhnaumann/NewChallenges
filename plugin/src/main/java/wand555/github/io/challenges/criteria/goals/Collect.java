package wand555.github.io.challenges.criteria.goals;

import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CollectableDataConfig;

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
}
