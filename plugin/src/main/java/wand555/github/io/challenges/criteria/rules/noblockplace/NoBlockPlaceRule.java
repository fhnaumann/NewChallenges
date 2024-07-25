package wand555.github.io.challenges.criteria.rules.noblockplace;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.criteria.rules.PunishableRule;
import wand555.github.io.challenges.criteria.rules.noblockbreak.BlockBreakRule;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.generated.NoBlockBreakRuleConfig;
import wand555.github.io.challenges.generated.NoBlockPlaceRuleConfig;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.blockplace.BlockPlaceData;
import wand555.github.io.challenges.types.blockplace.BlockPlaceType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class NoBlockPlaceRule extends PunishableRule<BlockPlaceData, Material> implements Triggable<BlockPlaceData>, Storable<NoBlockPlaceRuleConfig> {

    private static final Logger logger = ChallengesDebugLogger.getLogger(NoBlockPlaceRule.class);

    private final Set<Material> exemptions;
    private final BlockPlaceType blockPlaceType;

    public NoBlockPlaceRule(Context context, NoBlockPlaceRuleConfig config, NoBlockPlaceRuleMessageHelper messageHelper) {
        super(context, config.getPunishments(), PunishableRule.Result.fromJSONString(config.getResult().value()), messageHelper);
        this.exemptions = config.getExemptions() != null ? new HashSet<>(ModelMapper.str2Materials(context.dataSourceContext().materialJSONList(), config.getExemptions())) : new HashSet<>();

        this.blockPlaceType = new BlockPlaceType(context, triggerCheck(), trigger(), cancelIfDeny());
        logger.fine("Created %s instance.".formatted(blockPlaceType.getClass().getSimpleName()));
    }

    @Override
    public void addToGeneratedConfig(EnabledRules config) {
        config.setNoBlockPlace(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }

    @Override
    public NoBlockPlaceRuleConfig toGeneratedJSONClass() {
        return new NoBlockPlaceRuleConfig(
                exemptions.stream().map(DataSourceJSON::toCode).sorted().toList(),
                toPunishmentsConfig(),
                NoBlockPlaceRuleConfig.Result.fromValue(getResult().getValue())
        );
    }

    @Override
    public void unload() {
        blockPlaceType.unload();
    }

    @Override
    public TriggerCheck<BlockPlaceData> triggerCheck() {
        return TriggerCheck.ignoreIfContains(exemptions);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NoBlockPlaceRule that = (NoBlockPlaceRule) o;
        return Objects.equals(exemptions, that.exemptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), exemptions);
    }
}
