package wand555.github.io.challenges.criteria.goals.blockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.Map;
import java.util.function.Function;

public class BlockBreakGoalMessageHelper extends GoalMessageHelper<BlockBreakData, Material> {
    public BlockBreakGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return "blockbreakgoal";
    }

    @Override
    protected Map<String, Component> additionalBossBarPlaceholders(Material data) {
        return Map.of(
                "block", ResourcePackHelper.getMaterialUnicodeMapping(data)
        );
    }

    @Override
    protected Map<String, Component> additionalStepPlaceholders(BlockBreakData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "block", Component.translatable(data.broken())
        );
    }
}
