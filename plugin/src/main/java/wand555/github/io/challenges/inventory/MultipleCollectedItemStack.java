package wand555.github.io.challenges.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.CompletionConfig;
import wand555.github.io.challenges.generated.ContributorsConfig;
import wand555.github.io.challenges.utils.CollectionUtil;
import wand555.github.io.challenges.utils.TimerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class MultipleCollectedItemStack<T extends Enum<T>> extends BaseCollectedItemStack {

    protected final T about;
    protected Collect collect;

    public MultipleCollectedItemStack(Context context, CompletionConfig completionConfig, Collect collect, T about) {
        super(context, completionConfig);
        this.collect = collect;
        this.about = about;
    }

    public ItemStack update(Collect newCollect) {
        if(newCollect.isComplete() && whenCollectedSeconds == -1) {
            throw new RuntimeException("collect is completed, but no completion time has been set!");
        }
        this.collect = newCollect;
        return render();
    }

    @Override
    public final ItemStack render() {
        if(collect.isComplete()) {
            return renderComplete();
        }
        else {
            return renderOngoing();
        }
    }

    private ItemStack renderComplete() {
        ItemStack itemStack = getBaseItemStack();
        List<Component> lore = new ArrayList<>();
        lore.add(getCollectedInTime());
        lore.addAll(getContributorsComponent());
        itemStack.lore(lore);
        return itemStack;
    }

    private ItemStack renderOngoing() {
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
                        "amount", Component.text(collect.getCurrentAmount()),
                        "total_amount", Component.text(collect.getAmountNeeded())
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
                                TimerUtil.format(whenCollectedSeconds)
                        )
                ),
                false
        );
    }

    private List<Component> getContributorsComponent() {
        return contributors.entrySet().stream()
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

    public Collect getCollect() {
        return collect;
    }

    public Map<String, Integer> getContributors() {
        return contributors;
    }
}
