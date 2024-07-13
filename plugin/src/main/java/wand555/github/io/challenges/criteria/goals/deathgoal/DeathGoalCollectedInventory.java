package wand555.github.io.challenges.criteria.goals.deathgoal;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.inventory.progress.*;
import wand555.github.io.challenges.types.death.DeathData;
import wand555.github.io.challenges.types.death.DeathMessage;

import java.util.List;
import java.util.ResourceBundle;

public class DeathGoalCollectedInventory extends CollectedInventory<DeathData, DeathMessage> {
    public DeathGoalCollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<DeathMessage> enumType) {
        super(context, collectables, enumType);
    }

    @Override
    protected SingleCollectedItemStack<DeathMessage> createSingle(DeathMessage about, Collect collect) {
        return CollectedInventoryHelper.createSingleCollectedItemStack(
                context,
                getNameInResourceBundle(),
                getSpecificBundle(),
                collect,
                about,
                CollectedInventoryHelper.getDefaultItemStackCreator()
        );
    }

    @Override
    protected MultipleCollectedItemStack<DeathMessage> createMultiple(DeathMessage about, Collect collect) {
        return CollectedInventoryHelper.createMultipleCollectedItemStack(
                context,
                getNameInResourceBundle(),
                getSpecificBundle(),
                collect,
                about,
                CollectedInventoryHelper.getDefaultItemStackCreator()
        );
    }

    @Override
    public String getNameInResourceBundle() {
        return DeathGoal.NAME_IN_RB;
    }

    @Override
    public ResourceBundle getSpecificBundle() {
        return context.resourceBundleContext().goalResourceBundle();
    }

}
