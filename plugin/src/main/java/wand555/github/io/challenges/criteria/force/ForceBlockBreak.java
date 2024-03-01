package wand555.github.io.challenges.criteria.force;

import net.kyori.adventure.bossbar.BossBar;
import wand555.github.io.challenges.BossBarDisplay;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.criteria.goals.Skippable;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;

public class ForceBlockBreak extends BaseForce implements Triggable<BlockBreakData>, Storable<ForceBlockBreak> {
    @Override
    public ForceBlockBreak toGeneratedJSONClass() {
        return null;
    }

    @Override
    public TriggerCheck<BlockBreakData> triggerCheck() {
        return null;
    }

    @Override
    public Trigger<BlockBreakData> trigger() {
        return null;
    }
}
