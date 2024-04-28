package wand555.github.io.challenges.criteria.goals.mobgoal;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.goals.*;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.MobGoalConfig;
import wand555.github.io.challenges.types.mob.MobData;
import wand555.github.io.challenges.types.mob.MobType;
import wand555.github.io.challenges.utils.RandomUtil;

import java.util.Map;

public class MobGoal extends MapGoal<MobData, EntityType> implements Storable<MobGoalConfig>, Skippable {

    private final MobType mobType;

    public MobGoal(Context context, MobGoalConfig config, GoalCollector<EntityType> goalCollector, MobGoalMessageHelper messageHelper, MobGoalBossBarHelper bossBarHelper, MobGoalCollectedInventory collectedInventory) {
        super(context, config.isComplete(), goalCollector, messageHelper, bossBarHelper, collectedInventory);
        this.mobType = new MobType(context, triggerCheck(), trigger());
    }

    @Override
    public MobGoalConfig toGeneratedJSONClass() {
        return new MobGoalConfig(
                isComplete(),
                isFixedOrder(),
                goalCollector.toGeneratedJSONClass(),
                true
        );
    }

    public Map<EntityType, Collect> getToKill() {
        return goalCollector.getToCollect();
    }

    @Override
    public void addToGeneratedConfig(GoalsConfig config) {
        config.setMobGoal(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        Component mobGoalName = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "mobgoal.name",
                false
        ).append(ComponentUtil.COLON).color(ComponentUtil.getPrefixColor(context.plugin(), context.resourceBundleContext().goalResourceBundle()));
        Component entities = Component.empty().appendNewline();
        for (Map.Entry<EntityType, Collect> entry : goalCollector.getToCollect().entrySet()) {
            EntityType entityType = entry.getKey();
            Collect collect = entry.getValue();
            Component entityCollectInfo = Component.empty()
                    .appendSpace()
                    .appendSpace()
                    .appendSpace()
                    .appendSpace()
                    .append(ComponentUtil.formatChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().goalResourceBundle(),
                    "mobgoal.statusinfo.mob",
                    Map.of(
                            "entity", ComponentUtil.translate(entityType),
                            "amount", Component.text(collect.getCurrentAmount()),
                            "total_amount", Component.text(collect.getAmountNeeded())
                    ),
                    false
            ));
            entities = entities.append(entityCollectInfo).appendNewline();
        }
        return mobGoalName.append(entities);
    }

    @Override
    protected MobData createSkipData(Map.Entry<EntityType, Collect> toSkip, Player player) {
        return new MobData(toSkip.getKey(), player);
    }

    @Override
    public String getNameInCommand() {
        return "mob";
    }
}
