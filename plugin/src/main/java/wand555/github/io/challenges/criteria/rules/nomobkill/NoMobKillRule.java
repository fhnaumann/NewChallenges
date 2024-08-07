package wand555.github.io.challenges.criteria.rules.nomobkill;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.criteria.rules.PunishableRule;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.generated.NoMobKillRuleConfig;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.mob.MobData;
import wand555.github.io.challenges.types.mob.MobType;

import java.util.HashSet;
import java.util.Set;

public class NoMobKillRule extends PunishableRule<MobData, EntityType> implements Storable<NoMobKillRuleConfig> {

    private final MobType mobType;

    private final Set<EntityType> exemptions;

    public NoMobKillRule(Context context, NoMobKillRuleConfig config, NoMobKillRuleMessageHelper messageHelper) {
        super(context, config.getPunishments(), messageHelper);
        this.mobType = new MobType(context, triggerCheck(), trigger(), cancelIfCancelPunishmentActive());
        this.exemptions = config.getExemptions() != null
                          ? new HashSet<>(ModelMapper.str2EntityType(context.dataSourceContext().entityTypeJSONList(),
                                                                     config.getExemptions()
        ))
                          : new HashSet<>();
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
        return new NoMobKillRuleConfig(
                exemptions.stream().map(DataSourceJSON::toCode).sorted().toList(),
                toPunishmentsConfig()
        );
    }

    @Override
    public TriggerCheck<MobData> triggerCheck() {
        return data -> !exemptions.contains(data.entityInteractedWith());
    }

    @Override
    public void unload() {
        mobType.unload();
    }
}
