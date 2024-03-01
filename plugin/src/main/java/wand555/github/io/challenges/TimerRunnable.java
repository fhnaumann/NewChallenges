package wand555.github.io.challenges;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import wand555.github.io.challenges.utils.ResourceBundleHelper;
import wand555.github.io.challenges.utils.TimerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TimerRunnable implements Consumer<BukkitTask>, Storable<Integer> {
    private final Context context;

    private long timer;

    public TimerRunnable(Context context) {
        this.context = context;
        timer = context.schemaRoot().at("/definitions/Model/properties/timer").asLong(0L);
    }

    public void start() {
        context.plugin().getServer().getScheduler().runTaskTimer(context.plugin(), this, 0L, 20L);
    }

    @Override
    public void accept(BukkitTask bukkitTask) {
        Component formattedTime;
        if(context.challengeManager().isRunning()) {
            timer += 1;
            Map<TimerUtil.TimeParts, String> mappedTime = TimerUtil.format(timer);
            formattedTime = ComponentUtil.formatTimer(context.plugin(), context.resourceBundleContext().miscResourceBundle(), "timer.format", mappedTime);
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
    }


    public long getTimer() {
        return timer;
    }

    @Override
    public Integer toGeneratedJSONClass() {
        return (int) timer;
    }
}
