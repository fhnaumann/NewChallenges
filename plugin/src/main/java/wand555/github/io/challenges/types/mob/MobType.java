package wand555.github.io.challenges.types.mob;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.types.EventContainer;
import wand555.github.io.challenges.types.Type;

import java.util.Map;

public class MobType extends Type<MobData> {

    public MobType(Context context, TriggerCheck<MobData> triggerCheck, Trigger<MobData> whenTriggered) {
        this(context, triggerCheck, whenTriggered, event -> {});
    }

    public MobType(Context context, TriggerCheck<MobData> triggerCheck, Trigger<MobData> whenTriggered, EventContainer<EntityDeathEvent> onMobDeath) {
        super(context, triggerCheck, whenTriggered, Map.of(EntityDeathEvent.class, onMobDeath));
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        if(!context.challengeManager().isRunning()) {
            return;
        }
        Player killer = event.getEntity().getKiller();
        if(killer == null) {
            return;
        }
        MobData mobData = new MobData(event.getEntityType(), killer);
        triggerIfCheckPasses(mobData, event);
    }
}
