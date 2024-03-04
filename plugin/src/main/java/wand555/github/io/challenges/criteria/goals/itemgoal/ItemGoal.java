package wand555.github.io.challenges.criteria.goals.itemgoal;

import com.fasterxml.jackson.databind.JsonNode;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.goals.*;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.ItemGoalConfig;
import wand555.github.io.challenges.types.item.ItemData;
import wand555.github.io.challenges.types.item.ItemType;

import java.util.Map;

public class ItemGoal extends MapGoal<ItemData, Material> implements Storable<ItemGoalConfig>, Skippable {

    private final ItemType itemType;

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
    protected Collect updateCollect(ItemData data) {
        return getToCollect().computeIfPresent(data.itemStackInteractedWith().getType(), (material, collect) -> {
            // don't overshoot the total amount, because the player might have picked up more items (in the stack) that they needed
            int newCurrentAmount = Math.min(collect.getCurrentAmount()+data.itemStackInteractedWith().getAmount(), collect.getAmountNeeded());
            collect.setCurrentAmount(newCurrentAmount);
            return collect;
        });
    }
}
