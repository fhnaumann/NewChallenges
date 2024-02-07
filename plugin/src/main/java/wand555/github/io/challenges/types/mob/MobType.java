package wand555.github.io.challenges.types.mob;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.types.Type;

public class MobType extends Type<MobData> implements Listener {

    public MobType(Context context, TriggerCheck<MobData> triggerCheck, Trigger<MobData> whenTriggered) {
        super(context, triggerCheck, whenTriggered);
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if(killer == null) {
            return;
        }
        MobData mobData = new MobData(event.getEntityType(), killer);
        triggerIfCheckPasses(mobData);
    }
}
