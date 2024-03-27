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

    private Context context;
    private boolean ranOnce;

    public URLReminder(Context context) {
        this.context = context;
        this.ranOnce = false;
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void start() {
        try {
            context.plugin().getServer().getScheduler().runTaskTimerAsynchronously(context.plugin(), this, 5*20L, 30*20L);
        } catch (Exception ignored) {
            // MockBukkit throws UnimplentedOperationException for async timers
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
