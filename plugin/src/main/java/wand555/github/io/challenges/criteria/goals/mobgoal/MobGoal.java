package wand555.github.io.challenges.criteria.goals.mobgoal;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.goals.*;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.MobGoalConfig;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.types.mob.MobData;
import wand555.github.io.challenges.types.mob.MobType;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.function.Function;

public class MobGoal extends MapGoal<EntityType, MobData> implements Storable<MobGoalConfig>, Skippable {

    private final MobType mobType;

    public MobGoal(Context context, MobGoalConfig config, MobGoalMessageHelper messageHelper) {
        super(context, config.getComplete(), config.getFixedOrder(), config.getShuffled(), config.getMobs(), EntityType.class, messageHelper);
        this.mobType = new MobType(context, triggerCheck(), trigger());
        context.plugin().getServer().getPluginManager().registerEvents(mobType, context.plugin());
    }

    @Override
    public MobGoalConfig toGeneratedJSONClass() {
        return new MobGoalConfig(
                isComplete(),
                this.fixedOrder,
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
    public void onSkip() {

    }

    @Override
    protected Function<MobData, EntityType> data2MainElement() {
        return MobData::entityInteractedWith;
    }
}
