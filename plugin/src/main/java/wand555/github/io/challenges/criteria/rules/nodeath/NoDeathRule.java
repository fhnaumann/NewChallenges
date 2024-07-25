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
import wand555.github.io.challenges.types.death.DeathMessage;
import wand555.github.io.challenges.types.death.DeathType;

public class NoDeathRule extends PunishableRule<DeathData, DeathMessage> implements Storable<NoDeathRuleConfig> {

    private final DeathType deathType;
    private final boolean ignoreTotem;

    public NoDeathRule(Context context, NoDeathRuleConfig config, NoDeathRuleMessageHelper messageHelper) {
        super(context, config.getPunishments(), PunishableRule.Result.fromJSONString(config.getResult().value()), messageHelper);
        this.deathType = new DeathType(context, triggerCheck(), trigger());
        this.ignoreTotem = config.isIgnoreTotem();
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
            // TODO: check if totem was used and test if flag is enabled
            return true;
        };
    }

    @Override
    public NoDeathRuleConfig toGeneratedJSONClass() {
        return new NoDeathRuleConfig(ignoreTotem, toPunishmentsConfig(), NoDeathRuleConfig.Result.fromValue(result.getValue()));
    }

    @Override
    public void unload() {
        deathType.unload();
    }
}
