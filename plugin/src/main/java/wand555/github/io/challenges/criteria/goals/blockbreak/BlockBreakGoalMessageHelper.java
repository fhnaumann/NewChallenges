package wand555.github.io.challenges.criteria.goals.blockbreak;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;

import java.util.Map;

public class BlockBreakGoalMessageHelper extends GoalMessageHelper<BlockBreakData> {
    public BlockBreakGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return "blockbreakgoal";
    }

    @Override
    protected Map<String, Component> additionalPlaceholders(BlockBreakData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "block", Component.translatable(data.broken())
        );
    }
}
