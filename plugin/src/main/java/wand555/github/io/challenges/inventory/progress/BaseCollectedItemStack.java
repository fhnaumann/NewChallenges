package wand555.github.io.challenges.inventory.progress;

import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.ResourceBundleNarrowable;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.generated.ContributorsConfig;

import java.util.Map;
import java.util.ResourceBundle;

public abstract class BaseCollectedItemStack<K extends Keyed> implements ResourceBundleNarrowable {

    public static final BaseCollectedItemStack<?> AIR = new BaseCollectedItemStack<>(null, null, null) {
        @Override
        protected ItemStack getBaseItemStack() {
            return null;
        }

        @Override
        protected ItemStack renderComplete() {
            return null;
        }

        @Override
        protected ItemStack renderOngoing() {
            return null;
        }

        @Override
        public String getNameInResourceBundle() {
            return null;
        }

        @Override
        public ResourceBundle getSpecificBundle() {
            return null;
        }
    };

    protected final Context context;
    private Collect collect;
    private final K about;

    public BaseCollectedItemStack(Context context, Collect collect, K about) {
        this.context = context;
        this.collect = collect;
        this.about = about;
    }

    /**
     * Get the base ItemStack (no lore, etc.) from the data information that is supplied in the subclasses respectively.
     * @return An ItemStack with just a Material (and possibly an amount), but no lore.
     */
    protected abstract ItemStack getBaseItemStack();

    public final ItemStack render() {
        if(this == AIR) {
            return null;
        }
        if(getCollect().isComplete()) {
            return renderComplete();
        }
        else {
            return renderOngoing();
        }
    }

    protected abstract ItemStack renderComplete();

    protected abstract ItemStack renderOngoing();

    public void update(Collect collect) {
        this.collect = collect;
    }

    public Collect getCollect() {
        return collect;
    }

    public K getAbout() {
        return about;
    }

    public int getWhenCollectedSeconds() {
        return getCollect().getCompletionConfig().getWhenCollectedSeconds();
    }

    public Map<String, Integer> getContributors() {
        if(getCollect().getCompletionConfig().getContributors() == null) {
            getCollect().getCompletionConfig().setContributors(new ContributorsConfig());
        }
        return getCollect().getCompletionConfig().getContributors().getAdditionalProperties();
    }
}
