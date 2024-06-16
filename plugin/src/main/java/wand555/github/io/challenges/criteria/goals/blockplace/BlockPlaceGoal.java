package wand555.github.io.challenges.criteria.goals.blockplace;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.criteria.goals.*;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakCollectedInventory;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoalMessageHelper;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarPart;
import wand555.github.io.challenges.generated.BlockBreakGoalConfig;
import wand555.github.io.challenges.generated.BlockPlaceGoalConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.inventory.progress.CollectedInventory;
import wand555.github.io.challenges.types.blockplace.BlockPlaceData;
import wand555.github.io.challenges.types.blockplace.BlockPlaceType;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import javax.annotation.Nullable;
import java.util.Map;

public class BlockPlaceGoal extends MapGoal<BlockPlaceData, Material> implements Storable<BlockPlaceGoalConfig> {

    public static final String NAME_IN_RB = "blockplacegoal";

    private final BlockPlaceType blockPlaceType;

    public BlockPlaceGoal(Context context, BlockPlaceGoalConfig config, GoalCollector<Material> goalCollector, BlockPlaceGoalMessageHelper messageHelper, BlockPlaceGoalCollectedInventory collectedInventory, @Nullable Timer timer) {
        super(context, config.isComplete(), goalCollector, messageHelper, collectedInventory, timer);
        this.blockPlaceType = new BlockPlaceType(context, triggerCheck(), trigger());
    }

    @Override
    public void addToGeneratedConfig(GoalsConfig config) {
        config.setBlockPlaceGoal(toGeneratedJSONClass());
    }

    @Override
    public String getNameInResourceBundle() {
        return NAME_IN_RB;
    }

    @Override
    public Component getCurrentStatus() {
        return Component.text("BlockPlaceGoal TODO");
    }

    @Override
    public BlockPlaceGoalConfig toGeneratedJSONClass() {
        return new BlockPlaceGoalConfig(
                isComplete(),
                isFixedOrder(),
                timer != null ? timer.toGeneratedJSONClass() : null,
                goalCollector.toGeneratedJSONClass(),
                true
        );
    }

    @Override
    public void unload() {
        blockPlaceType.unload();
    }

    @Override
    public String getNameInCommand() {
        return "blockplace";
    }

    @Override
    protected BossBarPart.GoalInformation<Material> constructGoalInformation() {
        return new BossBarPart.GoalInformation<>(getNameInResourceBundle(), data -> Map.of("block", ResourcePackHelper.getMaterialUnicodeMapping(data)));
    }

    @Override
    protected BlockPlaceData createSkipData(Map.Entry<Material, Collect> toSkip, Player player) {
        return new BlockPlaceData(toSkip.getKey(), toSkip.getValue().getRemainingToCollect(), player);
    }
}
