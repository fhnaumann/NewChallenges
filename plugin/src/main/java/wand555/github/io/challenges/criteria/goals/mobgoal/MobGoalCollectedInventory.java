package wand555.github.io.challenges.criteria.goals.mobgoal;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.inventory.progress.CollectedInventory;
import wand555.github.io.challenges.inventory.progress.MultipleCollectedItemStack;
import wand555.github.io.challenges.inventory.progress.SingleCollectedItemStack;
import wand555.github.io.challenges.types.mob.MobData;

import java.util.List;
import java.util.ResourceBundle;

public class MobGoalCollectedInventory extends CollectedInventory<MobData, EntityType> {

    public MobGoalCollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<EntityType> enumType) {
        super(context, collectables, enumType);
    }

    @Override
    protected SingleCollectedItemStack<EntityType> createSingle(EntityType about, Collect collect) {
        return new MobGoalSingleCollectedItemStack(context, collect, about);
    }

    @Override
    protected MultipleCollectedItemStack<EntityType> createMultiple(EntityType about, Collect collect) {
        return new MobGoalMultipleCollectedItemStack(context, collect, about);
    }


    @Override
    public String getNameInResourceBundle() {
        return "mobgoal";
    }

    @Override
    public ResourceBundle getSpecificBundle() {
        return context.resourceBundleContext().goalResourceBundle();
    }

    private static Material entityType2Material(EntityType entityType) {
        return Material.PAPER; // TODO use custom model data to show actual entity images in item slots
    }

    private static class MobGoalSingleCollectedItemStack extends SingleCollectedItemStack<EntityType> {
        public MobGoalSingleCollectedItemStack(Context context, Collect collect, EntityType about) {
            super(context, collect, about);
        }

        @Override
        protected ItemStack getBaseItemStack() {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            itemStack.editMeta(itemMeta -> itemMeta.displayName(Component.translatable(getAbout())));
            return itemStack;
        }

        @Override
        public String getNameInResourceBundle() {
            return "mobgoal";
        }

        @Override
        public ResourceBundle getSpecificBundle() {
            return context.resourceBundleContext().goalResourceBundle();
        }
    }

    private static class MobGoalMultipleCollectedItemStack extends MultipleCollectedItemStack<EntityType> {
        public MobGoalMultipleCollectedItemStack(Context context, Collect collect, EntityType about) {
            super(context, collect, about);
        }

        @Override
        protected ItemStack getBaseItemStack() {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            itemStack.editMeta(itemMeta -> itemMeta.displayName(Component.translatable(getAbout())));
            return itemStack;
        }

        @Override
        public String getNameInResourceBundle() {
            return "mobgoal";
        }

        @Override
        public ResourceBundle getSpecificBundle() {
            return context.resourceBundleContext().goalResourceBundle();
        }
    }
}
