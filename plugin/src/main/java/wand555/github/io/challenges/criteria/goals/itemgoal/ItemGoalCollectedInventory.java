package wand555.github.io.challenges.criteria.goals.itemgoal;

import org.bukkit.Material;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.inventory.BaseCollectedItemStack;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.inventory.MultipleCollectedItemStack;
import wand555.github.io.challenges.types.item.ItemData;

import java.util.List;

public class ItemGoalCollectedInventory extends CollectedInventory<ItemData, Material> {

    public ItemGoalCollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<Material> enumType) {
        super(context, collectables, enumType);
    }

    @Override
    protected BaseCollectedItemStack createSingle(Material data, long secondsSinceStart) {
        return null;
    }

    @Override
    protected MultipleCollectedItemStack<?> createMultiple(Material data, long secondsSinceStart) {
        return null;
    }
}
