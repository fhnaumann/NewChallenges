package wand555.github.io.challenges.goals;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.ComponentInterpolator;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.generated.MobGoalConfig;
import wand555.github.io.challenges.inventory.CollectedInventory;
import wand555.github.io.challenges.inventory.CollectedItemStack;
import wand555.github.io.challenges.utils.ActionHelper;

import java.util.Map;
import java.util.logging.Level;

public class MobGoal extends Goal implements Storable<MobGoalConfig>, Listener {

    private final Map<EntityType, Collect> toKill;
    private final CollectedInventory collectedInventory;

    public MobGoal(Context context, Map<EntityType, Collect> toKill) {
        this(context, false, toKill);
    }

    public MobGoal(Context context, boolean complete, Map<EntityType, Collect> toKill) {
        super(context, complete);
        this.toKill = toKill;
        this.collectedInventory = new CollectedInventory(context.plugin());
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
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

        Component toSend = ComponentInterpolator.interpolate(
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
            toSend = ComponentInterpolator.interpolate(
                    context.plugin(),
                    context.resourceBundleContext().goalResourceBundle(),
                    "mobgoal.single.reached.message",
                    Map.of(
                            "entity", ComponentInterpolator.translate(killed)
                    )
            );
            soundInBundleKey = "mobgoal.single.reached.sound";
        }
        else {
            toSend = ComponentInterpolator.interpolate(
                    context.plugin(),
                    context.resourceBundleContext().goalResourceBundle(),
                    "mobgoal.single.step.message",
                    Map.of(
                            "player", Component.text(killer.getName()),
                            "entity", ComponentInterpolator.translate(killed),
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
}
