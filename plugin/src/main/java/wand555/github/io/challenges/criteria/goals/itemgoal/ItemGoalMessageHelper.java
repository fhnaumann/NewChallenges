package wand555.github.io.challenges.criteria.goals.itemgoal;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.types.item.ItemData;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.Map;

public class ItemGoalMessageHelper extends GoalMessageHelper<ItemData, Material> {
    public ItemGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return "itemgoal";
    }

    @Override
    public Map<String, Component> additionalBossBarPlaceholders(Material data) {
        return Map.of(
                "item", ResourcePackHelper.getMaterialUnicodeMapping(data)
        );
    }

    @Override
    protected Map<String, Component> additionalStepPlaceholders(ItemData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "item", ComponentUtil.translate(data.itemStackInteractedWith().getType())
        );
    }
}
