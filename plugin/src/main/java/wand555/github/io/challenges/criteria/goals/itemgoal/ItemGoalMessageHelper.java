package wand555.github.io.challenges.criteria.goals.itemgoal;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.types.item.ItemData;
import wand555.github.io.challenges.utils.ActionHelper;

import java.util.Map;

public class ItemGoalMessageHelper extends GoalMessageHelper<ItemData> {
    public ItemGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return "itemgoal";
    }

    @Override
    protected Map<String, Component> additionalPlaceholders(ItemData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "item", ComponentUtil.translate(data.itemStackInteractedWith().getType())
        );
    }
}
