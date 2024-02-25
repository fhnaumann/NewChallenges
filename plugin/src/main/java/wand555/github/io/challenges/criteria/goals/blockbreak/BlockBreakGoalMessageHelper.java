package wand555.github.io.challenges.criteria.goals.blockbreak;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;

public class BlockBreakGoalMessageHelper extends GoalMessageHelper<BlockBreakData> {
    public BlockBreakGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    public void sendSingleStepAction(BlockBreakData data, Collect collect) {

    }

    @Override
    public void sendSingleReachedAction(BlockBreakData data, Collect collect) {

    }

    @Override
    public void sendAllReachedAction() {

    }
}
