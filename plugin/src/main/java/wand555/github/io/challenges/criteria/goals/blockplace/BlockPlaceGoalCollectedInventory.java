package wand555.github.io.challenges.criteria.goals.blockplace;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.inventory.progress.CollectedInventory;
import wand555.github.io.challenges.inventory.progress.CollectedInventoryHelper;
import wand555.github.io.challenges.inventory.progress.MultipleCollectedItemStack;
import wand555.github.io.challenges.inventory.progress.SingleCollectedItemStack;
import wand555.github.io.challenges.types.blockplace.BlockPlaceData;

import java.util.List;
import java.util.ResourceBundle;

public class BlockPlaceGoalCollectedInventory extends CollectedInventory<BlockPlaceData, Material> {



    public BlockPlaceGoalCollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<Material> enumType) {
        super(context, collectables, enumType);
    }

    @Override
    public String getNameInResourceBundle() {
        return BlockPlaceGoal.NAME_IN_RB;
    }

    @Override
    public ResourceBundle getSpecificBundle() {
        return context.resourceBundleContext().goalResourceBundle();
    }

    @Override
    protected SingleCollectedItemStack<Material> createSingle(Material about, Collect collect) {
        return CollectedInventoryHelper.createSingleCollectedItemStack(context, getNameInResourceBundle(), getSpecificBundle(), collect, about);
    }

    @Override
    protected MultipleCollectedItemStack<Material> createMultiple(Material about, Collect collect) {
        return CollectedInventoryHelper.createMultipleCollectedItemStack(context, getNameInResourceBundle(), getSpecificBundle(), collect, about);
    }
}
