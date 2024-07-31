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

public abstract class MultipleCollectedItemStack<K extends Keyed> extends BaseCollectedItemStack<K> {

    public MultipleCollectedItemStack(Context context, Collect collect, K about) {
        super(context, collect, about);
    }

    protected ItemStack renderComplete() {
        ItemStack itemStack = getBaseItemStack();
        List<Component> lore = new ArrayList<>();
        lore.add(getCollectedInTime());
        lore.addAll(getContributorsComponent());
        itemStack.lore(lore);
        return itemStack;
    }

    protected ItemStack renderOngoing() {
        ItemStack itemStack = getBaseItemStack();
        List<Component> lore = new ArrayList<>();
        lore.add(getIncompleteFirstMessage());
        lore.addAll(getContributorsComponent());
        itemStack.lore(lore);
        return itemStack;
    }

    private Component getIncompleteFirstMessage() {
        return ComponentUtil.formatChatMessage(
                context.plugin(),
                getSpecificBundle(),
                "%s.progress.multiple.incomplete.message".formatted(getNameInResourceBundle()),
                Map.of(
                        "amount", Component.text(getCollect().getCurrentAmount()),
                        "total_amount", Component.text(getCollect().getAmountNeeded())
                ),
                false
        );
    }

    private Component getCollectedInTime() {
        return ComponentUtil.formatChatMessage(
                context.plugin(),
                getSpecificBundle(),
                "%s.progress.multiple.complete.message".formatted(getNameInResourceBundle()),
                Map.of(
                        "time", ComponentUtil.formatTimer(
                                context.plugin(),
                                context.resourceBundleContext().miscResourceBundle(),
                                "timer.format",
                                TimerUtil.format(getWhenCollectedSeconds())
                        )
                ),
                false
        );
    }

    private List<Component> getContributorsComponent() {
        return getContributors().entrySet().stream()
                                .map(this::contributorComponent)
                                .toList();
    }

    private Component contributorComponent(Map.Entry<String, Integer> entry) {
        return ComponentUtil.formatChatMessage(
                context.plugin(),
                getSpecificBundle(),
                "%s.progress.multiple.collector".formatted(getNameInResourceBundle()),
                Map.of(
                        "player", Component.text(entry.getKey()),
                        "amount", Component.text(entry.getValue())
                ),
                false
        );
    }
}
