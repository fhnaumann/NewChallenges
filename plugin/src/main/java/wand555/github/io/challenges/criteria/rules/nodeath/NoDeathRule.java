package wand555.github.io.challenges.criteria.rules.nodeath;

import net.kyori.adventure.text.Component;
import org.bukkit.damage.DeathMessageType;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.criteria.rules.PunishableRule;
import wand555.github.io.challenges.criteria.rules.RuleMessageHelper;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.generated.NoDeathRuleConfig;
import wand555.github.io.challenges.types.death.DeathData;
import wand555.github.io.challenges.types.death.DeathType;

public class NoDeathRule extends PunishableRule<DeathData, DeathMessageType> implements Storable<NoDeathRuleConfig> {

    private final DeathType deathType;

    public NoDeathRule(Context context, NoDeathRuleConfig config, NoDeathRuleMessageHelper messageHelper) {
        super(context, config.getPunishments(), PunishableRule.Result.fromJSONString(config.getResult().value()), messageHelper);
        this.deathType = new DeathType(context, triggerCheck(), trigger());
    }

    @Override
    public void addToGeneratedConfig(EnabledRules config) {
        config.setNoDeath(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }

    @Override
    public TriggerCheck<DeathData> triggerCheck() {
        return data -> {
            int x = 3;
            return true;
        };
    }

    @Override
    public NoDeathRuleConfig toGeneratedJSONClass() {
        return new NoDeathRuleConfig(toPunishmentsConfig(), NoDeathRuleConfig.Result.fromValue(result.getValue()));
    }
}
