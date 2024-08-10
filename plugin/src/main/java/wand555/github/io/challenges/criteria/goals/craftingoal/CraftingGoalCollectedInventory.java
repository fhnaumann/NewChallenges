package wand555.github.io.challenges.criteria.goals.craftingoal;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.inventory.progress.CollectedInventory;
import wand555.github.io.challenges.inventory.progress.CollectedInventoryHelper;
import wand555.github.io.challenges.inventory.progress.MultipleCollectedItemStack;
import wand555.github.io.challenges.inventory.progress.SingleCollectedItemStack;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.types.crafting.CraftingData;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.List;
import java.util.ResourceBundle;
import java.util.function.BiFunction;

public class CraftingGoalCollectedInventory extends CollectedInventory<CraftingData, CraftingTypeJSON> {
    public CraftingGoalCollectedInventory(Context context, List<CollectableEntryConfig> collectables, Class<CraftingTypeJSON> enumType) {
        super(context, collectables, enumType);
    }

    @Override
    public String getNameInResourceBundle() {
        return CraftingGoal.NAME_IN_RB;
    }

    @Override
    public ResourceBundle getSpecificBundle() {
        return context.resourceBundleContext().goalResourceBundle();
    }

    private static ItemStack itemStackCreator(Context context, CraftingTypeJSON craftingTypeJSON) {
        ItemStack itemStack = new ItemStack(Material.PAPER);
        Component craftingResult = ResourcePackHelper.getMaterialUnicodeMapping(craftingTypeJSON.getCraftingResult());
        Component recipeType = ResourcePackHelper.getMaterialUnicodeMapping(CraftingTypeJSON.recipeType2MaterialDisplay(craftingTypeJSON.getRecipeType()));
        // TODO: "from" is hardcoded in english
        Component displayName = craftingResult.appendSpace().append(Component.text("from")).appendSpace().append(recipeType);
        itemStack.editMeta(itemMeta -> itemMeta.displayName(displayName));
        return itemStack;
    }

    @Override
    protected SingleCollectedItemStack<CraftingTypeJSON> createSingle(CraftingTypeJSON about, Collect collect) {
        return CollectedInventoryHelper.createSingleCollectedItemStack(
                context,
                getNameInResourceBundle(),
                getSpecificBundle(),
                collect,
                about,
                CraftingGoalCollectedInventory::itemStackCreator
        );
    }

    @Override
    protected MultipleCollectedItemStack<CraftingTypeJSON> createMultiple(CraftingTypeJSON about, Collect collect) {
        return CollectedInventoryHelper.createMultipleCollectedItemStack(
                context,
                getNameInResourceBundle(),
                getSpecificBundle(),
                collect,
                about,
                CraftingGoalCollectedInventory::itemStackCreator
        );
    }
}
