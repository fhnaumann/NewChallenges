package wand555.github.io.challenges.criteria.goals.blockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.MapGoal;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarPart;
import wand555.github.io.challenges.generated.BlockBreakGoalConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.types.blockbreak.BlockBreakType;
import wand555.github.io.challenges.utils.RandomUtil;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import javax.annotation.Nullable;
import java.util.Map;

public class BlockBreakGoal extends MapGoal<BlockBreakData, Material> implements Storable<BlockBreakGoalConfig> {

    private BlockBreakType blockBreakType;

    public BlockBreakGoal(Context context, BlockBreakGoalConfig config, GoalCollector<Material> goalCollector, BlockBreakGoalMessageHelper messageHelper, BlockBreakCollectedInventory collectedInventory, @Nullable Timer timer) {
        super(context, config.isComplete(), goalCollector, messageHelper, collectedInventory, timer);
        this.blockBreakType = new BlockBreakType(context, triggerCheck(), trigger());
    }

    @Override
    public void addToGeneratedConfig(GoalsConfig config) {
        config.setBlockBreakGoal(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return Component.text("BlockBreakGoal TODO");
    }

    @Override
    public BlockBreakGoalConfig toGeneratedJSONClass() {
        return new BlockBreakGoalConfig(
                goalCollector.toGeneratedJSONClass(),
                isComplete(),
                isFixedOrder(),
                timer != null ? timer.toGeneratedJSONClass() : null,
                true // anything that is stored from the server will already be shuffled (once)
        );
    }

    @Override
    protected BossBarPart.GoalInformation<Material> constructGoalInformation() {
        return new BossBarPart.GoalInformation<>(getNameInResourceBundle(), data -> Map.of("block", ResourcePackHelper.getMaterialUnicodeMapping(data)));
    }

    @Override
    protected BlockBreakData createSkipData(Map.Entry<Material, Collect> toSkip, Player player) {
        return new BlockBreakData(toSkip.getKey(), toSkip.getValue().getRemainingToCollect(), player);
    }

    @Override
    public String getNameInCommand() {
        return "blockbreak";
    }

    @Override
    public String getNameInResourceBundle() {
        return "blockbreakgoal";
    }
}
