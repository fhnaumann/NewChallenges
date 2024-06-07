package wand555.github.io.challenges;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.utils.ResourceBundleHelper;
import wand555.github.io.challenges.utils.TimerUtil;

import java.util.HashMap;
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
        task.cancel();
    }

    @Override
    public void accept(BukkitTask bukkitTask) {
        if(task == null) {
            task = bukkitTask;
        }
        Component formattedTime;
        if(context.challengeManager().isRunning()) {
            timer += 1;
            Map<TimerUtil.TimeParts, String> mappedTime = TimerUtil.format(timer);
            formattedTime = ComponentUtil.formatTimer(context.plugin(), context.resourceBundleContext().miscResourceBundle(), "timer.format", mappedTime);
        }
        else if(context.challengeManager().isSetup()) {
            // don't do anything in this case, as the URLReminder is showing something already
            return;
        }
        else {
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
            context.challengeManager().goalsWithSameOrderNumber().forEach(goal -> {
                if(goal.getTimer().isFailedDueTimeLimit()) {
                    context.challengeManager().endChallenge(false);
                }
                else {
                    goal.getBossBarHelper().updateBossBar();
                }
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
