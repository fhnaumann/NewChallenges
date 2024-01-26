package wand555.github.io.challenges.goals;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.CollectableEntryConfig;
import wand555.github.io.challenges.generated.MobGoalConfig;

import java.util.Map;
import java.util.logging.Level;

public class MobGoal extends Goal implements Storable<MobGoalConfig>, Listener {

    private final Map<EntityType, Collect> toKill;

    public MobGoal(Context context, Map<EntityType, Collect> toKill) {
        this(context, false, toKill);
    }

    public MobGoal(Context context, boolean complete, Map<EntityType, Collect> toKill) {
        super(context, complete);
        this.toKill = toKill;
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
        Challenges.getPlugin(Challenges.class).getLogger().info("registering event");
    }

    public Map<EntityType, Collect> getToKill() {
        return toKill;
    }

    @Override
    public void onComplete() {
        setComplete(true);
        // TODO: display messages

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
        Challenges.getPlugin(Challenges.class).getLogger().log(Level.ALL, "entity death event");
        System.out.println(killer);
        if(killer == null) {
            return;
        }
        newEntityKilled(killer, event.getEntityType());
    }

    private void newEntityKilled(Player killer, EntityType killed) {
        getToKill().computeIfPresent(killed, (entityType, collect) -> {
            collect.setCurrentAmount(collect.getCurrentAmount()+1);
            return collect;
        });
        if(determineComplete()) {
            onComplete();
        }
    }

    private boolean determineComplete() {
        return toKill.values().stream().allMatch(Collect::isComplete);
    }
}
