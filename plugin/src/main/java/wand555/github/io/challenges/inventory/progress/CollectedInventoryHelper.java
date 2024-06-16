package wand555.github.io.challenges.inventory.progress;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;

import java.util.ResourceBundle;

public class CollectedInventoryHelper {

    public static SingleCollectedItemStack<Material> createSingleCollectedItemStack(Context context, String nameInResourceBundle, ResourceBundle specificBundle, Collect collect, Material about) {
        return new SingleCollectedItemStack<>(context, collect, about) {

            @Override
            protected ItemStack getBaseItemStack() {
                return DEFAULT_ITEMSTACK_CREATOR.apply(context, getAbout());
            }

            @Override
            public String getNameInResourceBundle() {
                return nameInResourceBundle;
            }

            @Override
            public ResourceBundle getSpecificBundle() {
                return specificBundle;
            }
        };
    }

    public static MultipleCollectedItemStack<Material> createMultipleCollectedItemStack(Context context, String nameInResourceBundle, ResourceBundle specificBundle, Collect collect, Material about) {
        return new MultipleCollectedItemStack<>(context, collect, about) {

            @Override
            protected ItemStack getBaseItemStack() {
                return DEFAULT_ITEMSTACK_CREATOR.apply(context, getAbout());
            }

            @Override
            public String getNameInResourceBundle() {
                return nameInResourceBundle;
            }

            @Override
            public ResourceBundle getSpecificBundle() {
                return specificBundle;
            }
        };
    }
}
