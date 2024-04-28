package wand555.github.io.challenges.inventory.progress;

import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.utils.TimerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SingleCollectedItemStack<K extends Keyed> extends BaseCollectedItemStack<K> {

    public SingleCollectedItemStack(Context context, Collect collect, K about) {
        super(context, collect, about);
    }

    @Override
    protected ItemStack renderComplete() {
        ItemStack itemStack = getBaseItemStack();
        List<Component> lore = new ArrayList<>();
        lore.add(getCollectedByInTime());
        itemStack.lore(lore);
        return itemStack;
    }

    @Override
    protected ItemStack renderOngoing() {
        ItemStack itemStack = getBaseItemStack();
        List<Component> lore = new ArrayList<>();
        lore.add(getNotYetCollected());
        itemStack.lore(lore);
        return itemStack;
    }

    private Component getNotYetCollected() {
        return ComponentUtil.formatChatMessage(
                context.plugin(),
                getSpecificBundle(),
                "%s.progress.single.incomplete.message".formatted(getNameInResourceBundle()),
                false
        );
    }

    private Component getCollectedByInTime() {
        return ComponentUtil.formatChatMessage(
                context.plugin(),
                getSpecificBundle(),
                "%s.progress.single.complete.message".formatted(getNameInResourceBundle()),
                Map.of(
                        "time", ComponentUtil.formatTimer(
                                context.plugin(),
                                context.resourceBundleContext().miscResourceBundle(),
                                "timer.format",
                                TimerUtil.format(getWhenCollectedSeconds())
                        ),
                        "player", Component.text(getContributors().keySet().stream().findFirst().orElse("-"))
                ),
                false
        );
    }
}
