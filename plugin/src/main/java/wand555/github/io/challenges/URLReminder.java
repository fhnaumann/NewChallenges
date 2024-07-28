package wand555.github.io.challenges;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.function.Consumer;

public class URLReminder implements Consumer<BukkitTask>, Listener {

    private class URLActionBar implements Consumer<BukkitTask> {

        private BukkitTask task;

        @Override
        public void accept(BukkitTask bukkitTask) {
            if(task == null) {
                task = bukkitTask;
            }
            if(context.challengeManager().isRunning() || context.plugin().urlReminder == null) {
                task.cancel();
            }

            if(context.challengeManager().isSetup()) {
                Component message = ComponentUtil.formatChatMessage(
                        context.plugin(),
                        context.resourceBundleContext().miscResourceBundle(),
                        "challenge.builder.chat.short",
                        Map.of("url", ComponentUtil.BUILDER_LINK),
                        false
                );
                context.plugin().getServer().getOnlinePlayers().forEach(player -> player.sendActionBar(message));
            }
        }
    }

    private Context context;
    private boolean ranOnce;

    private BukkitTask task;
    private final URLActionBar urlActionBar;

    public URLReminder(Context context) {
        this.context = context;
        this.ranOnce = false;
        this.urlActionBar = new URLActionBar();
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void start() {
        try {
            context.plugin().getServer().getScheduler().runTaskTimerAsynchronously(context.plugin(), this, 5*20L, 30*20L);
            context.plugin().getServer().getScheduler().runTaskTimer(context.plugin(), urlActionBar, 0L, 20L);
        } catch (Exception ignored) {
            // MockBukkit throws UnimplentedOperationException for async timers
        }

    }

    public void stop() {
        // both tasks may be null when the reminder is basically skipped entirely when a previous session is loaded at server launch
        if(task != null) {
            task.cancel();
        }
        if(urlActionBar.task != null) {
            urlActionBar.task.cancel();
        }
    }

    private Component builderLink() {
        return ComponentUtil.formatChallengesPrefixChatMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenge.builder.chat",
                Map.of("url", ComponentUtil.BUILDER_LINK)
        );
    }

    @Override
    public void accept(BukkitTask bukkitTask) {
        if(task == null) {
            task = bukkitTask;
        }
        if(context.challengeManager().isRunning() || context.plugin().urlReminder == null) {
            task.cancel();
        }

        if(!context.challengeManager().isSetup() && ranOnce) {
            // only send link to website in setup phase
            return;
        }
        Bukkit.broadcast(builderLink());
        ranOnce = true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!context.challengeManager().isSetup() && ranOnce) {
            // only send link to website in setup phase
            return;
        }
        event.getPlayer().sendMessage(builderLink());
    }
}
