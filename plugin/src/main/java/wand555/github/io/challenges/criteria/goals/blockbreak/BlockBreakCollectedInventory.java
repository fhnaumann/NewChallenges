package wand555.github.io.challenges.criteria.goals.blockbreak;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.inventory.progress.CollectedInventory;
import wand555.github.io.challenges.inventory.progress.CollectedInventoryHelper;
import wand555.github.io.challenges.inventory.progress.MultipleCollectedItemStack;
import wand555.github.io.challenges.inventory.progress.SingleCollectedItemStack;

import java.util.List;
import java.util.ResourceBundle;

public class BlockBreakCollectedInventory extends CollectedInventory<Material> {

    public BlockBreakCollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<Material> enumType) {
        super(context, collectables, enumType);
    }

    @Override
    protected SingleCollectedItemStack<Material> createSingle(Material about, Collect collect) {
        return new BlockBreakSingleCollectedItemStack(context, collect, about);
    }

    @Override
    protected MultipleCollectedItemStack<Material> createMultiple(Material about, Collect collect) {
        return new BlockBreakMultipleCollectedItemStack(context, collect, about);
    }

    @Override
    public String getNameInResourceBundle() {
        return "blockbreakgoal";
    }

    @Override
    public ResourceBundle getSpecificBundle() {
        return context.resourceBundleContext().goalResourceBundle();
    }

    private static class BlockBreakSingleCollectedItemStack extends SingleCollectedItemStack<Material> {

        public BlockBreakSingleCollectedItemStack(Context context, Collect collect, Material about) {
            super(context, collect, about);
        }

        @Override
        protected ItemStack getBaseItemStack() {
            return CollectedInventoryHelper.DEFAULT_ITEM_2_ITEMSTACK_CREATOR.apply(context, getAbout());
        }

        @Override
        public String getNameInResourceBundle() {
            return "blockbreakgoal";
        }

        @Override
        public ResourceBundle getSpecificBundle() {
            return context.resourceBundleContext().goalResourceBundle();
        }
    }

    private static class BlockBreakMultipleCollectedItemStack extends MultipleCollectedItemStack<Material> {

        public BlockBreakMultipleCollectedItemStack(Context context, Collect collect, Material about) {
            super(context, collect, about);
        }

        @Override
        public String getNameInResourceBundle() {
            return "blockbreakgoal";
        }

        @Override
        public ResourceBundle getSpecificBundle() {
            return context.resourceBundleContext().goalResourceBundle();
        }

        @Override
        protected ItemStack getBaseItemStack() {
            return CollectedInventoryHelper.DEFAULT_ITEM_2_ITEMSTACK_CREATOR.apply(context, getAbout());
        }
    }
}
