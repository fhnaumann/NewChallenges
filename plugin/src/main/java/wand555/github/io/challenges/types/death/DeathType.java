package wand555.github.io.challenges.types.death;

import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.types.EventContainer;
import wand555.github.io.challenges.types.Type;

import java.util.Map;

public class DeathType extends Type<DeathData> {

    public DeathType(Context context, TriggerCheck<DeathData> triggerCheck, Trigger<DeathData> whenTriggered) {
        super(context, triggerCheck, whenTriggered, Map.of());
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageEvent event) {
        if(!isPlayerDead(event)) {
            return;
        }
        triggerIfCheckPasses(new DeathData((Player) event.getEntity(), null), event);
    }

    private boolean isPlayerDead(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player player)) {
            return false;
        }
        return player.getHealth() - event.getFinalDamage() <= 0;
    }
}
