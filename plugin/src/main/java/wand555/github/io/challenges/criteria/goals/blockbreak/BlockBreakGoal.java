package wand555.github.io.challenges.criteria.goals.blockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.generated.BlockBreakGoalConfig;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.types.blockbreak.BlockBreakType;

import java.util.*;

public class BlockBreakGoal extends BaseGoal implements Triggable<BlockBreakData>, Storable<BlockBreakGoalConfig> {

    private BlockBreakType blockBreakType;

    private BlockBreakGoalMessageHelper messageHelper;
    private GoalCollector<Material> goalCollector;
    private final boolean fixedOrder;

    public BlockBreakGoal(Context context, BlockBreakGoalConfig config, BlockBreakGoalMessageHelper messageHelper) {
        super(context, config.getComplete());
        this.blockBreakType = new BlockBreakType(context, triggerCheck(), trigger());
        this.fixedOrder = config.getFixedOrder();
        this.messageHelper = messageHelper;
        if(config.getFixedOrder() && !config.getShuffled()) {
            Collections.shuffle(config.getBroken());
        }
        this.goalCollector = new GoalCollector<>(context, config.getBroken(), Material.class);

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
            if(fixedOrder) {
                return goalCollector.getCurrentlyToCollect().getKey() == data.broken();
            }
            else {
                return goalCollector.getToCollect().containsKey(data.broken());
            }
        };
    }

    @Override
    public Trigger<BlockBreakData> trigger() {
        return this::newBlockBroken;
    }

    private void newBlockBroken(BlockBreakData data) {
        Collect updatedCollect = goalCollector.getToCollect().computeIfPresent(data.broken(), (material, collect) -> {
            collect.setCurrentAmount(collect.getCurrentAmount()+1);
            return collect;
        });
        if(updatedCollect.isComplete()) {
            messageHelper.sendSingleReachedAction(data, updatedCollect);
        }
        else {
            messageHelper.sendSingleStepAction(data, updatedCollect);
        }
        if(determineComplete()) {
            onComplete();
        }
    }

    @Override
    public boolean determineComplete() {
        return goalCollector.isComplete();
    }

    @Override
    public void onComplete() {
        setComplete(true);

        messageHelper.sendAllReachedAction();

        notifyManager();
    }
}
