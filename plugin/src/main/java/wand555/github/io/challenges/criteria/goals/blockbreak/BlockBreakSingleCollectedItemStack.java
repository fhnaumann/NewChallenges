package wand555.github.io.challenges.criteria.goals.blockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.generated.CompletionConfig;
import wand555.github.io.challenges.generated.ContributorsConfig;
import wand555.github.io.challenges.inventory.SingleCollectedItemStack;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.utils.ResourceBundleHelper;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class BlockBreakSingleCollectedItemStack extends SingleCollectedItemStack<Material> {
    public BlockBreakSingleCollectedItemStack(Context context, CompletionConfig completionConfig, Material about) {
        super(context, completionConfig, about);
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
