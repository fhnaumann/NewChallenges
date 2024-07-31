package wand555.github.io.challenges.inventory.progress;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;

import java.util.ResourceBundle;
import java.util.function.BiFunction;

public class CollectedInventoryHelper {

    public static <T extends Translatable> BiFunction<Context, T, ItemStack> getDefaultItemStackCreator() {
        return (ignoredForNow, t) -> {
            ItemStack itemStack = new ItemStack(Material.PAPER);
            itemStack.editMeta(itemMeta -> itemMeta.displayName(Component.translatable(t)));
            return itemStack;
        };
    }

    public static final BiFunction<Context, Material, ItemStack> DEFAULT_ITEM_2_ITEMSTACK_CREATOR = (ignoredForNow, material) -> {
        if(material.isItem()) {
            return new ItemStack(material);
        } else {
            return getDefaultItemStackCreator().apply(ignoredForNow, material);
        }
    };

    public static <T extends Keyed> SingleCollectedItemStack<T> createSingleCollectedItemStack(Context context, String nameInResourceBundle, ResourceBundle specificBundle, Collect collect, T about, BiFunction<Context, T, ItemStack> aboutMapper) {
        return new SingleCollectedItemStack<>(context, collect, about) {
            @Override
            protected ItemStack getBaseItemStack() {
                return aboutMapper.apply(context, getAbout());
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

    public static SingleCollectedItemStack<Material> createSingleCollectedItemStack(Context context, String nameInResourceBundle, ResourceBundle specificBundle, Collect collect, Material about) {
        return createSingleCollectedItemStack(context,
                                              nameInResourceBundle,
                                              specificBundle,
                                              collect,
                                              about,
                                              DEFAULT_ITEM_2_ITEMSTACK_CREATOR
        );
    }

    public static <T extends Keyed> MultipleCollectedItemStack<T> createMultipleCollectedItemStack(Context context, String nameInResourceBundle, ResourceBundle specificBundle, Collect collect, T about, BiFunction<Context, T, ItemStack> aboutMapper) {
        return new MultipleCollectedItemStack<>(context, collect, about) {
            @Override
            protected ItemStack getBaseItemStack() {
                return aboutMapper.apply(context, about);
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
        return createMultipleCollectedItemStack(context,
                                                nameInResourceBundle,
                                                specificBundle,
                                                collect,
                                                about,
                                                DEFAULT_ITEM_2_ITEMSTACK_CREATOR
        );
    }
}
