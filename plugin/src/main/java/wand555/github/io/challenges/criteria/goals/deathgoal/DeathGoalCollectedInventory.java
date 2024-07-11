package wand555.github.io.challenges.criteria.goals.deathgoal;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.inventory.progress.CollectedInventory;
import wand555.github.io.challenges.inventory.progress.MultipleCollectedItemStack;
import wand555.github.io.challenges.inventory.progress.SingleCollectedItemStack;
import wand555.github.io.challenges.types.death.DeathData;
import wand555.github.io.challenges.types.death.DeathMessage;

import java.util.List;
import java.util.ResourceBundle;

public class DeathGoalCollectedInventory extends CollectedInventory<DeathData, DeathMessage> {
    public DeathGoalCollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<DeathMessage> enumType) {
        super(context, collectables, enumType);
    }

    @Override
    public String getNameInResourceBundle() {
        return null;
    }

    @Override
    public ResourceBundle getSpecificBundle() {
        return null;
    }

    @Override
    protected SingleCollectedItemStack<DeathMessage> createSingle(DeathMessage about, Collect collect) {
        return null;
    }

    @Override
    protected MultipleCollectedItemStack<DeathMessage> createMultiple(DeathMessage about, Collect collect) {
        return null;
    }
}
