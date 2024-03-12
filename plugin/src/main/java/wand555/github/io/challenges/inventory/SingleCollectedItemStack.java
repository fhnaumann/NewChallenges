package wand555.github.io.challenges.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.generated.CompletionConfig;
import wand555.github.io.challenges.generated.ContributorsConfig;
import wand555.github.io.challenges.utils.CollectionUtil;
import wand555.github.io.challenges.utils.TimerUtil;

import java.util.List;
import java.util.Map;

public abstract class SingleCollectedItemStack<E> extends BaseCollectedItemStack {

    protected final E about;

    public SingleCollectedItemStack(Context context, E about, long secondsSinceStart) {
        super(context, secondsSinceStart);
        this.about = about;
    }

    public SingleCollectedItemStack(Context context, CompletionConfig completionConfig, E about) {
        super(context, completionConfig);
        this.about = about;
    }

    @Override
    public CompletionConfig toGeneratedJSONClass() {
        return null;
    }

    @Override
    public final ItemStack render() {
        ItemStack itemStack = getBaseItemStack();
        itemStack.lore(List.of(getCollectedByInTime()));
        return itemStack;
    }

    private Component getCollectedByInTime() {
        return ComponentUtil.formatChatMessage(
                context.plugin(),
                getSpecificBundle(),
                "%s.progress.single.message".formatted(getNameInResourceBundle()),
                Map.of(
                        "time", ComponentUtil.formatTimer(
                                context.plugin(),
                                context.resourceBundleContext().miscResourceBundle(),
                                "timer.format",
                                TimerUtil.format(whenCollectedSeconds)
                        ),
                        "player", Component.text(contributors.keySet().stream().findFirst().orElse("-"))
                ),
                false
        );
    }
}
