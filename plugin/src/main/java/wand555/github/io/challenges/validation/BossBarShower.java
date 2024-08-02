package wand555.github.io.challenges.validation;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import wand555.github.io.challenges.BossBarDisplay;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.teams.Team;

public class BossBarShower implements Listener {

    private final Context context;
    private final ChallengeManager manager;

    public BossBarShower(Context context) {
        this.context = context;
        this.manager = context.challengeManager();
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(manager.isSetup() || manager.getGameState() == ChallengeManager.GameState.ENDED) {
            return;
        }
        Team team = Team.getTeamPlayerIn(context, event.getPlayer().getUniqueId());
        team.getGoals().stream()
               .filter(baseGoal -> !baseGoal.hasTimer() || baseGoal.hasTimer() && baseGoal.getTimer().getOrder() == team.getCurrentOrder())
               .filter(baseGoal -> baseGoal instanceof BossBarDisplay).map(baseGoal -> (BossBarDisplay) baseGoal)
               .forEach(bossBarDisplay -> bossBarDisplay.showBossBar(event.getPlayer()));
    }
}
