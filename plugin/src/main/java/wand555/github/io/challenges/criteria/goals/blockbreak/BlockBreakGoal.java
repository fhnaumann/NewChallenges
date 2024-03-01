package wand555.github.io.challenges.criteria.goals.blockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.MapGoal;
import wand555.github.io.challenges.generated.BlockBreakGoalConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.types.blockbreak.BlockBreakType;

import java.util.*;
import java.util.function.Function;

public class BlockBreakGoal extends MapGoal<Material, BlockBreakData> implements Storable<BlockBreakGoalConfig> {

    private BlockBreakType blockBreakType;

    public BlockBreakGoal(Context context, BlockBreakGoalConfig config, BlockBreakGoalMessageHelper messageHelper) {
        super(context, config.getComplete(), config.getFixedOrder(), config.getShuffled(), config.getBroken(), Material.class, messageHelper);
        this.blockBreakType = new BlockBreakType(context, triggerCheck(), trigger());

    }

    @Override
    public void addToGeneratedConfig(GoalsConfig config) {
        config.setBlockbreakGoal(toGeneratedJSONClass());
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
                this.fixedOrder,
                true // anything that is stored from the server will already be shuffled (once)
        );
    }

    @Override
    protected Function<BlockBreakData, Material> data2MainElement() {
        return BlockBreakData::broken;
    }
}
