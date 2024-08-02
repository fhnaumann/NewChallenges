package wand555.github.io.challenges;

import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitTask;
import wand555.github.io.challenges.teams.Team;
import wand555.github.io.challenges.utils.TimerUtil;

import java.util.Map;
import java.util.function.Consumer;

public class TimerRunnable implements Consumer<BukkitTask>, Storable<Integer> {
    private final Context context;
    private BukkitTask task;

    private long timer;

    public TimerRunnable(Context context) {
        this.context = context;
        timer = context.schemaRoot().at("/definitions/Model/properties/timer").asLong(0L);
    }

    public TimerRunnable(Context context, long timer) {
        this.context = context;
        this.timer = timer;
    }

    public void start() {
        context.plugin().getServer().getScheduler().runTaskTimer(context.plugin(), this, 0L, 20L);
    }

    public void shutdown() {
        // task may be null under specific circumstances:
        // 1. A previous challenge was unloaded without ever being started.
        if(task != null) {
            task.cancel();
        }

    }

    @Override
    public void accept(BukkitTask bukkitTask) {
        if(task == null) {
            task = bukkitTask;
        }
        if(context.plugin().urlReminder != null) {
            // don't do anything until the urlReminder is stopped
            return;
        }

        Component formattedTime;
        if(context.challengeManager().isRunning()) {
            timer += 1;
            Map<TimerUtil.TimeParts, String> mappedTime = TimerUtil.format(timer);
            formattedTime = ComponentUtil.formatTimer(context.plugin(),
                                                      context.resourceBundleContext().miscResourceBundle(),
                                                      "timer.format",
                                                      mappedTime
            );
        } else if(context.challengeManager().isSetup()) {
            // don't do anything in this case, as the URLReminder is showing something already
            return;
        } else {
            formattedTime = ComponentUtil.formatChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "timer.paused",
                    false
            );
        }
        context.plugin().getServer().getOnlinePlayers().forEach(player -> {
            player.sendActionBar(formattedTime);
        });

        if(context.challengeManager().isRunning()) {
            // handle timers for goals (if they exist)
            Team.goalsWithSameOrderNumberAcrossAllTeams(context.challengeManager()).forEach((team, goals) -> {
                goals.forEach(goal -> {
                    goal.getTimer().decrementTime();
                    if(goal.getTimer().isFailedDueTimeLimit()) {
                        context.challengeManager().failChallengeFor(team);
                    }
                    else {
                        goal.getBossBarHelper().updateBossBar();
                    }

                });
            });
        }
    }


    public long getTimer() {
        return timer;
    }

    @Override
    public Integer toGeneratedJSONClass() {
        return (int) timer;
    }
}
