package wand555.github.io.challenges.criteria.goals.blockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.CompletionConfig;
import wand555.github.io.challenges.inventory.MultipleCollectedItemStack;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class BlockBreakMultipleCollectedItemStack extends MultipleCollectedItemStack<Material> {

    public BlockBreakMultipleCollectedItemStack(Context context, CompletionConfig completionConfig, Collect collect, Material about) {
        super(context, completionConfig, collect, about);
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
