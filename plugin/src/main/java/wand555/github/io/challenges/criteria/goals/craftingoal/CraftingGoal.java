package wand555.github.io.challenges.criteria.goals.craftingoal;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.MapGoal;
import wand555.github.io.challenges.criteria.goals.Timer;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarPart;
import wand555.github.io.challenges.criteria.goals.bossbar.FixedOrderBossBarPart;
import wand555.github.io.challenges.generated.CraftingGoalConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.MCEventAlias;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.types.crafting.CraftingData;
import wand555.github.io.challenges.types.crafting.CraftingType;

import javax.annotation.Nullable;
import java.util.Map;

public class CraftingGoal extends MapGoal<CraftingData<?>, CraftingTypeJSON> implements Storable<CraftingGoalConfig> {

    public static final String NAME_IN_RB = "craftinggoal";

    private final CraftingType craftingType;

    public CraftingGoal(Context context, CraftingGoalConfig config, GoalCollector<CraftingTypeJSON> goalCollector, CraftingGoalMessageHelper messageHelper, CraftingGoalCollectedInventory collectedInventory, @Nullable Timer timer) {
        super(context, config.isComplete(), goalCollector, messageHelper, collectedInventory, timer);
        this.craftingType = new CraftingType(context, triggerCheck(), trigger(), MCEventAlias.EventType.CRAFTING_GOAL);
    }


    @Override
    public void addToGeneratedConfig(GoalsConfig config) {
        config.setCraftingGoal(toGeneratedJSONClass());
    }

    @Override
    public String getNameInResourceBundle() {
        return NAME_IN_RB;
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }

    @Override
    public CraftingGoalConfig toGeneratedJSONClass() {
        return new CraftingGoalConfig(
                isComplete(),
                goalCollector.toGeneratedJSONClass(),
                isFixedOrder(),
                timer != null ? timer.toGeneratedJSONClass() : null,
                true
        );
    }

    @Override
    public void unload() {
        craftingType.unload();
    }

    @Override
    public String getNameInCommand() {
        return "crafting";
    }

    @Override
    protected FixedOrderBossBarPart<CraftingTypeJSON> fixedOrderBossBarPart(Context context, BossBarPart.GoalInformation<CraftingTypeJSON> goalInformation, GoalCollector<CraftingTypeJSON> goalCollector) {
        return new CraftingGoalFixedOrderBossBarPart(context, goalInformation, goalCollector);
    }

    @Override
    protected CraftingData<?> createSkipData(Map.Entry<CraftingTypeJSON, Collect> toSkip, Player player) {
        return new CraftingData<>(null, player.getUniqueId(), toSkip.getValue().getRemainingToCollect(), toSkip.getKey(), false);
    }
}
