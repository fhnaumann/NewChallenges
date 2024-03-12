package wand555.github.io.challenges.criteria.goals.blockbreak;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.CompletionConfig;
import wand555.github.io.challenges.inventory.BaseCollectedItemStack;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.inventory.MultipleCollectedItemStack;
import wand555.github.io.challenges.inventory.SingleCollectedItemStack;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;

import java.util.List;
import java.util.ResourceBundle;

public class BlockBreakCollectedInventory extends CollectedInventory<BlockBreakData, Material> {

    public BlockBreakCollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<Material> enumType) {
        super(context, collectables, enumType);
    }

    @Override
    protected BaseCollectedItemStack createSingle(Material data, long secondsSinceStart) {
        return new BlockBreakSingleCollectedItemStack(context, data, secondsSinceStart);
    }

    @Override
    protected MultipleCollectedItemStack<?> createMultiple(Material data, long secondsSinceStart) {
        return new BlockBreakMultipleCollectedItemStack(context, data, secondsSinceStart);
    }

    private static class BlockBreakSingleCollectedItemStack extends SingleCollectedItemStack<Material> {

        public BlockBreakSingleCollectedItemStack(Context context, Material about, long secondsSinceStart) {
            super(context, about, secondsSinceStart);
        }

        @Override
        protected ItemStack getBaseItemStack() {
            return new ItemStack(about);
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


        public BlockBreakMultipleCollectedItemStack(Context context, Material about, long secondsSinceStart) {
            super(context, about, secondsSinceStart);
        }

        public BlockBreakMultipleCollectedItemStack(Context context, CompletionConfig completionConfig, Material about) {
            super(context, completionConfig, about);
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
            return new ItemStack(about);
        }
    }
}
