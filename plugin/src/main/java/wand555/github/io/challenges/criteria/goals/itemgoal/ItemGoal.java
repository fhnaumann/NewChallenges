package wand555.github.io.challenges.criteria.goals.itemgoal;

import com.fasterxml.jackson.databind.JsonNode;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.criteria.goals.*;
import wand555.github.io.challenges.generated.CollectableDataConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.ItemGoalConfig;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.item.ItemData;
import wand555.github.io.challenges.types.item.ItemType;
import wand555.github.io.challenges.utils.ActionHelper;
import wand555.github.io.challenges.utils.RandomUtil;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemGoal extends MapGoal<Material, ItemData> implements Storable<ItemGoalConfig>, InvProgress, Skippable {

    private final ItemType itemType;
    private final CollectedInventory collectedInventory;

    public ItemGoal(Context context, ItemGoalConfig config) {
        super(context, config.getComplete(), config.getFixedOrder(), config.getShuffled(), config.getItems(), Material.class, new ItemGoalMessageHelper(context));
        JsonNode amountNeeded = context.schemaRoot().at("/definitions/CollectableDataConfig/properties/amountNeeded/default");
        if(amountNeeded.isMissingNode()) {
            throw new RuntimeException();
        }
        JsonNode currentAmount = context.schemaRoot().at("/definitions/CollectableDataConfig/properties/currentAmount/default");
        if(currentAmount.isMissingNode()) {
            throw new RuntimeException();
        }
        this.collectedInventory = new CollectedInventory(context.plugin());

        this.itemType = new ItemType(context, triggerCheck(), trigger());
    }

    @Override
    public void addToGeneratedConfig(GoalsConfig config) {
        config.setItemGoal(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return Component.text("ItemGoal TODO");
    }

    @Override
    public ItemGoalConfig toGeneratedJSONClass() {
        return new ItemGoalConfig(
                isComplete(),
                this.fixedOrder,
                goalCollector.toGeneratedJSONClass(),
                false
        );
    }

    public Map<Material, Collect> getToCollect() {
        return goalCollector.getToCollect();
    }

    @Override
    public @NotNull CollectedInventory getCollectedInventory() {
        return collectedInventory;
    }

    @Override
    public String getBaseCommand() {
        return "itemgoal";
    }

    @Override
    public void onShowInvProgressCommand() {

    }

    @Override
    public void onSkip() {

    }

    @Override
    protected Function<ItemData, Material> data2MainElement() {
        return data -> data.itemStackInteractedWith().getType();
    }

    @Override
    protected Collect updateCollect(ItemData data) {
        return getToCollect().computeIfPresent(data.itemStackInteractedWith().getType(), (material, collect) -> {
            // don't overshoot the total amount, because the player might have picked up more items (in the stack) that they needed
            int newCurrentAmount = Math.min(collect.getCurrentAmount()+data.itemStackInteractedWith().getAmount(), collect.getAmountNeeded());
            collect.setCurrentAmount(newCurrentAmount);
            return collect;
        });
    }
}
