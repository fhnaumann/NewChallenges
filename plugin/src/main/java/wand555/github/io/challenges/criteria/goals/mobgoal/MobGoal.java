package wand555.github.io.challenges.criteria.goals.mobgoal;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.InvProgress;
import wand555.github.io.challenges.criteria.goals.Skippable;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.MobGoalConfig;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.mob.MobData;
import wand555.github.io.challenges.types.mob.MobType;
import wand555.github.io.challenges.utils.ActionHelper;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import javax.validation.constraints.NotNull;
import java.util.Map;

public class MobGoal extends BaseGoal implements Triggable<MobData>, Storable<MobGoalConfig>, BossBarDisplay, InvProgress, Skippable {

    private final MobType mobType;
    private final MobGoalMessageHelper messageHelper;

    private final Map<EntityType, Collect> toKill;
    private final CollectedInventory collectedInventory;
    private final BossBar bossBar;

    public MobGoal(Context context, MobGoalConfig config, MobGoalMessageHelper messageHelper) {
        super(context, config.getComplete());
        this.toKill = ModelMapper.str2Collectable(config.getMobs().getAdditionalProperties(), EntityType.class);
        this.collectedInventory = new CollectedInventory(context.plugin());
        this.bossBar = createBossBar();

        this.mobType = new MobType(context, triggerCheck(), trigger());
        context.plugin().getServer().getPluginManager().registerEvents(mobType, context.plugin());
        this.messageHelper = messageHelper;
    }

    public Map<EntityType, Collect> getToKill() {
        return toKill;
    }

    @Override
    public void onComplete() {
        setComplete(true);
        messageHelper.sendAllReachedAction();
        notifyManager();
    }

    @Override
    public MobGoalConfig toGeneratedJSONClass() {
        CollectableEntryConfig collectableEntryConfig = new CollectableEntryConfig();
        toKill.forEach((entityType, collect) -> collectableEntryConfig.setAdditionalProperty(entityType.toString(), collect.toGeneratedJSONClass()));
        return new MobGoalConfig(
                isComplete(),
                null, // TODO change constructor and set value here
                collectableEntryConfig
        );
    }

    private void newEntityKilled(MobData data) {
        Collect updatedCollect = getToKill().computeIfPresent(data.entityInteractedWith(), (entityType, collect) -> {
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

    public boolean determineComplete() {
        return toKill.values().stream().allMatch(Collect::isComplete);
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
        for (Map.Entry<EntityType, Collect> entry : toKill.entrySet()) {
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
    public BossBar createBossBar() {
        Component carrotUnicode = ResourcePackHelper.getMaterialUnicodeMapping(Material.CARROT);
        String manual = "\ue441";
        // new String(Character.toChars(58433)), new String(Character.toChars(0xe441))
        BossBar bossBar = BossBar.bossBar(Component.text("DUMMY TEST").append(carrotUnicode), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        return bossBar;
    }

    @Override
    public BossBar getBossBar() {
        return bossBar;
    }

    private void refreshBossBar(EntityType entityType, Collect collect) {

    }

    @Override
    public BossBarPriority getBossBarPriority() {
        return BossBarPriority.INFO;
    }

    @Override
    public @NotNull CollectedInventory getCollectedInventory() {
        return collectedInventory;
    }

    @Override
    public String getBaseCommand() {
        return "mobgoal";
    }

    @Override
    public void onShowInvProgressCommand() {

    }

    @Override
    public void onSkip() {

    }

    @Override
    public TriggerCheck<MobData> triggerCheck() {
        return data -> getToKill().containsKey(data.entityInteractedWith());
    }

    @Override
    public Trigger<MobData> trigger() {
        return this::newEntityKilled;
    }
}
