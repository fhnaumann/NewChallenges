package wand555.github.io.challenges.criteria.rules.nomobkill;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.criteria.rules.PunishableRule;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.generated.NoMobKillRuleConfig;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.mob.MobData;
import wand555.github.io.challenges.types.mob.MobType;

import java.util.HashSet;
import java.util.Set;

public class NoMobKillRule extends PunishableRule implements Triggable<MobData>, Storable<NoMobKillRuleConfig> {

    private final MobType mobType;
    private final NoMobKillRuleMessageHelper messageHelper;

    private final Set<Material> exemptions;

    public NoMobKillRule(Context context, NoMobKillRuleConfig config, NoMobKillRuleMessageHelper messageHelper) {
        super(context);
        this.mobType = new MobType(context, triggerCheck(), trigger());
        this.messageHelper = messageHelper;
        this.exemptions = new HashSet<>(ModelMapper.str2Mat(config.getExemptions(), material -> true));
    }

    @Override
    public void addToGeneratedConfig(EnabledRules config) {
        config.setNoMobKill(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }

    @Override
    public NoMobKillRuleConfig toGeneratedJSONClass() {
        return null;
    }

    @Override
    public TriggerCheck<MobData> triggerCheck() {
        return null;
    }

    @Override
    public Trigger<MobData> trigger() {
        return null;
    }
}
