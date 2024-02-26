package wand555.github.io.challenges.criteria.goals.blockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
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
    public TriggerCheck<BlockBreakData> triggerCheck() {
        return data -> {
            if(isComplete()) {
                return false;
            }
            Collect toCollect = goalCollector.getToCollect().get(data.broken());
            if(toCollect == null || toCollect.isComplete()) {
                return false;
            }
            if(fixedOrder) {
                return goalCollector.getCurrentlyToCollect().getKey() == data.broken();
            }
            return true;
        };
    }

    @Override
    protected Collect updateCollect(BlockBreakData data) {
        return goalCollector.getToCollect().computeIfPresent(data.broken(), (material, collect) -> {
            collect.setCurrentAmount(collect.getCurrentAmount()+1);
            return collect;
        });
    }
}
