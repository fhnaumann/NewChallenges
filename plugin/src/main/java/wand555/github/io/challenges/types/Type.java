package wand555.github.io.challenges.types;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.generated.MCEventAlias;
import wand555.github.io.challenges.teams.Team;

import java.util.Map;
import java.util.logging.Logger;

public abstract class Type<T extends Data<?, ?>> implements Listener {

    private static final Logger logger = ChallengesDebugLogger.getLogger(Type.class);

    protected final Context context;

    protected final TriggerCheck<T> triggerCheck;
    protected final Trigger<T> whenTriggered;
    protected final MCEventAlias.EventType eventType;

    public static boolean sameTeamCheck(Context context, BaseGoal baseGoal, Player player) {
        Team team = Team.getTeamPlayerIn(context, player.getUniqueId());
        return team == Team.ALL_TEAM || team.getGoals().contains(baseGoal);
    }

    public Type(Context context, TriggerCheck<T> triggerCheck, Trigger<T> whenTriggered, MCEventAlias.EventType eventType) {
        this.context = context;
        this.triggerCheck = triggerCheck;
        this.whenTriggered = whenTriggered;
        this.eventType = eventType;
    }

    public void unload() {
        HandlerList.unregisterAll(this);
    }

    public <E extends Event> void triggerIfCheckPasses(T data, E event) {
        if(triggerCheck.applies(data)) {
            logger.fine("Trigger check applies for %s with data '%s'.".formatted(event.getEventName(), data));
            whenTriggered.actOnTriggered(data);
            sendEventNotification(data);
        }
    }

    private void sendEventNotification(T data) {
        int timestamp = (int) context.challengeManager().getTime();
        context.liveService().eventProvider().sendEvent(timestamp, eventType, data.constructMCEventData());
    }
}
