package wand555.github.io.challenges.criteria.goals.blockplace;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.types.blockplace.BlockPlaceData;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.Map;

public class BlockPlaceGoalMessageHelper extends GoalMessageHelper<BlockPlaceData, Material> {
    public BlockPlaceGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return "blockplacegoal";
    }

    @Override
    protected Map<String, Component> additionalBossBarPlaceholders(Material data) {
        return Map.of(
                "block", ResourcePackHelper.getMaterialUnicodeMapping(data)
        );
    }

    @Override
    protected Map<String, Component> additionalStepPlaceholders(BlockPlaceData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "block", Component.translatable(data.placed())
        );
    }
}
