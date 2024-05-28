package wand555.github.io.challenges.criteria.goals.itemgoal;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.goals.*;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.ItemGoalConfig;
import wand555.github.io.challenges.types.item.ItemData;
import wand555.github.io.challenges.types.item.ItemType;
import wand555.github.io.challenges.utils.RandomUtil;

import java.util.Map;

public class ItemGoal extends MapGoal<ItemData, Material> implements Storable<ItemGoalConfig>, Skippable {

    private final ItemType itemType;

    public ItemGoal(Context context, ItemGoalConfig config, GoalCollector<Material> goalCollector, ItemGoalMessageHelper messageHelper, ItemGoalBossBarHelper bossBarHelper, ItemGoalCollectedInventory collectedInventory) {
        super(context, config.isComplete(), goalCollector, messageHelper, bossBarHelper, collectedInventory);
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
                isFixedOrder(),
                timer.toGeneratedJSONClass(),
                goalCollector.toGeneratedJSONClass(),
                false
        );
    }

    public Map<Material, Collect> getToCollect() {
        return goalCollector.getToCollect();
    }

    @Override
    protected ItemData createSkipData(Map.Entry<Material, Collect> toSkip, Player player) {
        return new ItemData(new ItemStack(toSkip.getKey()), toSkip.getValue().getRemainingToCollect(), player);
    }

    @Override
    public String getNameInCommand() {
        return "item";
    }
}
