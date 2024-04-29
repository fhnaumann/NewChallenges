package wand555.github.io.challenges.criteria.goals.itemgoal;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.inventory.progress.CollectedInventory;
import wand555.github.io.challenges.inventory.progress.MultipleCollectedItemStack;
import wand555.github.io.challenges.inventory.progress.SingleCollectedItemStack;
import wand555.github.io.challenges.types.item.ItemData;

import java.util.List;
import java.util.ResourceBundle;

public class ItemGoalCollectedInventory extends CollectedInventory<ItemData, Material> {

    public ItemGoalCollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<Material> enumType) {
        super(context, collectables, enumType);
    }

    @Override
    protected SingleCollectedItemStack<Material> createSingle(Material about, Collect collect) {
        return new ItemGoalSingleCollectedItemStack(context, collect, about);
    }

    @Override
    protected MultipleCollectedItemStack<Material> createMultiple(Material about, Collect collect) {
        return new ItemGoalMultipleCollectedItemStack(context, collect, about);
    }

    @Override
    public String getNameInResourceBundle() {
        return "itemgoal";
    }

    @Override
    public ResourceBundle getSpecificBundle() {
        return context.resourceBundleContext().goalResourceBundle();
    }

    private static class ItemGoalSingleCollectedItemStack extends SingleCollectedItemStack<Material> {
        public ItemGoalSingleCollectedItemStack(Context context, Collect collect, Material about) {
            super(context, collect, about);
        }

        @Override
        protected ItemStack getBaseItemStack() {
            return DEFAULT_ITEMSTACK_CREATOR.apply(context, getAbout());
        }

        @Override
        public String getNameInResourceBundle() {
            return "itemgoal";
        }

        @Override
        public ResourceBundle getSpecificBundle() {
            return context.resourceBundleContext().goalResourceBundle();
        }
    }

    private static class ItemGoalMultipleCollectedItemStack extends MultipleCollectedItemStack<Material> {
        public ItemGoalMultipleCollectedItemStack(Context context, Collect collect, Material about) {
            super(context, collect, about);
        }

        @Override
        protected ItemStack getBaseItemStack() {
            return DEFAULT_ITEMSTACK_CREATOR.apply(context, getAbout());
        }

        @Override
        public String getNameInResourceBundle() {
            return "itemgoal";
        }

        @Override
        public ResourceBundle getSpecificBundle() {
            return context.resourceBundleContext().goalResourceBundle();
        }
    }
}
