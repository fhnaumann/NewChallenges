package wand555.github.io.challenges.criteria.goals;

import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CollectableDataConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.CompletionConfig;

import java.util.Objects;

public class Collect implements Storable<CollectableDataConfig> {

    private final CollectableDataConfig config;

    public Collect(int amountNeeded) {
        this(amountNeeded, 0);
    }

    public Collect(int amountNeeded, int currentAmount) {
        this(new CollectableDataConfig(amountNeeded, new CompletionConfig(), currentAmount));
    }

    public Collect(CollectableDataConfig config) {
        this.config = config;
    }

    public int getRemainingToCollect() {
        return getAmountNeeded() - getCurrentAmount();
    }

    public int getAmountNeeded() {
        return config.getAmountNeeded();
    }

    public int getCurrentAmount() {
        return config.getCurrentAmount();
    }

    public void setCurrentAmount(int currentAmount) {
        config.setCurrentAmount(currentAmount);
    }

    public boolean isComplete() {
        return config.getCurrentAmount() >= config.getAmountNeeded();
    }

    @Override
    public CollectableDataConfig toGeneratedJSONClass() {
        return config;
    }

    public CompletionConfig getCompletionConfig() {
        return config.getCompletion();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collect collect = (Collect) o;
        return config.equals(collect.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config);
    }
}
