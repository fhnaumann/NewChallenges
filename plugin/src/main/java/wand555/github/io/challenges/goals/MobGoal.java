package wand555.github.io.challenges.goals;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import wand555.github.io.challenges.BossBarDisplay;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.MobGoalConfig;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.utils.ActionHelper;

import java.util.Map;

public class MobGoal extends Goal implements Storable<MobGoalConfig>, BossBarDisplay, Listener {

    private final Map<EntityType, Collect> toKill;
    private final CollectedInventory collectedInventory;
    private final BossBar bossBar;

    public MobGoal(Context context, Map<EntityType, Collect> toKill) {
        this(context, false, toKill);
    }

    public MobGoal(Context context, boolean complete, Map<EntityType, Collect> toKill) {
        super(context, complete);
        this.toKill = toKill;
        this.collectedInventory = new CollectedInventory(context.plugin());
        this.bossBar = createBossBar();
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());


        // TODO: remove
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.showBossBar(createBossBar());
        });
    }

    public void openCollectedInv(Player player) {
        collectedInventory.show(player);
    }

    public Map<EntityType, Collect> getToKill() {
        return toKill;
    }

    @Override
    public void onComplete() {
        setComplete(true);

        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                "mobgoal.all.reached.message"
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                context.resourceBundleContext().goalResourceBundle(),
                "mobgoal.all.reached.sound"
        );

        notifyManager();
    }

    @Override
    public MobGoalConfig toGeneratedJSONClass() {
        CollectableEntryConfig collectableEntryConfig = new CollectableEntryConfig();
        toKill.forEach((entityType, collect) -> collectableEntryConfig.setAdditionalProperty(entityType.toString(), collect.toGeneratedJSONClass()));
        return new MobGoalConfig(
                isComplete(),
                collectableEntryConfig
        );
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if(killer == null) {
            return;
        }
        newEntityKilled(killer, event.getEntityType());
    }

    private void newEntityKilled(Player killer, EntityType killed) {
        Collect updatedCollect = getToKill().computeIfPresent(killed, (entityType, collect) -> {
            collect.setCurrentAmount(collect.getCurrentAmount()+1);
            return collect;
        });
        Component toSend = null;
        String soundInBundleKey = null;
        if(updatedCollect.isComplete()) {
            toSend = ComponentUtil.formatChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().goalResourceBundle(),
                    "mobgoal.single.reached.message",
                    Map.of(
                            "entity", ComponentUtil.translate(killed)
                    )
            );
            soundInBundleKey = "mobgoal.single.reached.sound";
        }
        else {
            toSend = ComponentUtil.formatChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().goalResourceBundle(),
                    "mobgoal.single.step.message",
                    Map.of(
                            "player", Component.text(killer.getName()),
                            "entity", ComponentUtil.translate(killed),
                            "amount", Component.text(updatedCollect.getCurrentAmount()),
                            "total_amount", Component.text(updatedCollect.getAmountNeeded())
                    )
            );
            soundInBundleKey = "mobgoal.single.step.sound";
        }
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                context.resourceBundleContext().goalResourceBundle(),
                soundInBundleKey
        );

        if(determineComplete()) {
            onComplete();
        }
    }

    private boolean determineComplete() {
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
        BossBar bossBar = BossBar.bossBar(Component.text("This is a test: \uE000!"), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        return bossBar;
    }

    private void refreshBossBar(EntityType entityType, Collect collect) {

    }

    @Override
    public BossBarPriority getBossBarPriority() {
        return BossBarPriority.INFO;
    }
}
