package wand555.github.io.challenges.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.ResourceBundleNarrowable;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CompletionConfig;
import wand555.github.io.challenges.generated.ContributorsConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class BaseCollectedItemStack implements ResourceBundleNarrowable, Storable<CompletionConfig> {

    public static final BaseCollectedItemStack AIR = new BaseCollectedItemStack() {
        @Override
        protected ItemStack getBaseItemStack() {
            return null;
        }

        @Override
        public ItemStack render() {
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
    protected final Map<String, Integer> contributors;
    protected int whenCollectedSeconds;

    private BaseCollectedItemStack() {
        this(null, new CompletionConfig());
    }

    public BaseCollectedItemStack(Context context, long secondsSinceStart) {
        this(context, new CompletionConfig(null, (int) secondsSinceStart));
    }

    public BaseCollectedItemStack(Context context, CompletionConfig completionConfig) {
        this.context = context;
        this.contributors = completionConfig.getContributors() != null ? completionConfig.getContributors().getAdditionalProperties(): new HashMap<>();
        this.whenCollectedSeconds = completionConfig.getWhenCollectedSeconds();
    }

    /**
     * Get the base ItemStack (no lore, etc.) from the data information that is supplied in the subclasses respectively.
     * @return An ItemStack with just a Material (and possibly an amount), but no lore.
     */
    protected abstract ItemStack getBaseItemStack();

    public abstract ItemStack render();

    public int getWhenCollectedSeconds() {
        return whenCollectedSeconds;
    }

    public void setWhenCollectedSeconds(int whenCollectedSeconds) {
        this.whenCollectedSeconds = whenCollectedSeconds;
    }

    @Override
    public CompletionConfig toGeneratedJSONClass() {
        ContributorsConfig contributorsConfig = new ContributorsConfig();
        contributors.forEach(contributorsConfig::setAdditionalProperty);
        return new CompletionConfig(contributorsConfig, whenCollectedSeconds);
    }
}
