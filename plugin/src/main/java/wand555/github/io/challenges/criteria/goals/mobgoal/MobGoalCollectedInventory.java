package wand555.github.io.challenges.criteria.goals.mobgoal;

import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.inventory.BaseCollectedItemStack;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.inventory.MultipleCollectedItemStack;
import wand555.github.io.challenges.types.mob.MobData;

import java.util.List;

public class MobGoalCollectedInventory extends CollectedInventory<MobData, EntityType> {

    public MobGoalCollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<EntityType> enumType) {
        super(context, collectables, enumType);
    }

    @Override
    protected BaseCollectedItemStack createSingle(EntityType data, long secondsSinceStart) {
        return null;
    }

    @Override
    protected MultipleCollectedItemStack<?> createMultiple(EntityType data, long secondsSinceStart) {
        return null;
    }
}
