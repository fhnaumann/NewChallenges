package wand555.github.io.challenges.criteria.goals.deathgoal;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.criteria.goals.*;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarBuilder;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarHelper;
import wand555.github.io.challenges.criteria.goals.bossbar.BossBarPart;
import wand555.github.io.challenges.generated.DeathGoalConfig;
import wand555.github.io.challenges.generated.GoalsConfig;
import wand555.github.io.challenges.types.death.DeathData;
import wand555.github.io.challenges.types.death.DeathMessage;
import wand555.github.io.challenges.types.death.DeathType;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.ResourceBundle;

public class DeathGoal extends MapGoal<DeathData, DeathMessage> implements Listener, Storable<DeathGoalConfig> {

    public static final String NAME_IN_RB = "deathgoal";
    private final DeathType deathType;
    private final int deathAmount;
    private final boolean countTotem;

    public DeathGoal(Context context, DeathGoalConfig config, GoalCollector<DeathMessage> goalCollector, DeathGoalMessageHelper messageHelper, DeathGoalCollectedInventory collectedInventory, @Nullable Timer timer) {
        super(context, config.isComplete(), goalCollector, messageHelper, collectedInventory, timer);
        this.deathAmount = config.getDeathAmount();
        this.countTotem = config.isCountTotem();
        this.deathType = new DeathType(context, triggerCheck(), trigger());

    }

    @Override
    public void addToGeneratedConfig(GoalsConfig config) {
        config.setDeathGoal(toGeneratedJSONClass());
    }

    @Override
    public String getNameInResourceBundle() {
        return NAME_IN_RB;
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }

    @Override
    public DeathGoalConfig toGeneratedJSONClass() {
        return new DeathGoalConfig(
                isComplete(),
                isCountTotem(),
                getDeathAmount(),
                goalCollector.toGeneratedJSONClass(),
                isFixedOrder(),
                timer != null ? timer.toGeneratedJSONClass() : null,
                true
        );
    }

    @Override
    public void unload() {

    }

    @Override
    public String getNameInCommand() {
        return "death";
    }

    @Override
    protected BossBarPart.GoalInformation<DeathMessage> constructGoalInformation() {
        return new BossBarPart.GoalInformation<>(getNameInResourceBundle(), data -> Map.of("death_type", ResourcePackHelper.getDeathMessageUnicodeMapping(data.toEnum())));
    }

    @Override
    protected DeathData createSkipData(Map.Entry<DeathMessage, Collect> toSkip, Player player) {
        return new DeathData(player, toSkip.getValue().getRemainingToCollect(), toSkip.getKey());
    }

    public int getDeathAmount() {
        return deathAmount;
    }

    public boolean isCountTotem() {
        return countTotem;
    }
}
