package wand555.github.io.challenges.criteria.rules.noblockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.generated.MCEventAlias;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.types.blockbreak.BlockBreakType;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.generated.NoBlockBreakRuleConfig;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.criteria.rules.PunishableRule;

import java.util.*;
import java.util.logging.Logger;

public class BlockBreakRule extends PunishableRule<BlockBreakData, Material> implements Triggable<BlockBreakData>, Storable<NoBlockBreakRuleConfig> {

    private static final Logger logger = ChallengesDebugLogger.getLogger(BlockBreakRule.class);

    private final BlockBreakType blockBreakType;
    private final Set<Material> exemptions;

    public BlockBreakRule(Context context, NoBlockBreakRuleConfig config, BlockBreakRuleMessageHelper messageHelper) {
        super(context,
              config.getPunishments(),
              messageHelper
        );
        this.exemptions = config.getExemptions() != null
                          ? new HashSet<>(ModelMapper.str2Materials(context.dataSourceContext().materialJSONList(),
                                                                    config.getExemptions()
        ))
                          : new HashSet<>();

        blockBreakType = new BlockBreakType(context, triggerCheck(), trigger(), MCEventAlias.EventType.NO_BLOCK_BREAK);
        logger.fine("Created %s instance.".formatted(blockBreakType.getClass().getSimpleName()));
    }

    @Override
    public TriggerCheck<BlockBreakData> triggerCheck() {
        return TriggerCheck.ignoreIfContains(exemptions);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        if(!super.equals(o)) {
            return false;
        }
        BlockBreakRule that = (BlockBreakRule) o;
        return Objects.equals(exemptions, that.exemptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), exemptions);
    }

    @Override
    public NoBlockBreakRuleConfig toGeneratedJSONClass() {
        return new NoBlockBreakRuleConfig(
                exemptions.stream().map(DataSourceJSON::toCode).sorted().toList(),
                // always sort when moving from set to list
                toPunishmentsConfig()
        );
    }

    @Override
    public void addToGeneratedConfig(EnabledRules config) {
        config.setNoBlockBreak(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        Component noBlockBreakRuleName = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().ruleResourceBundle(),
                "noblockbreak.name",
                false
        ).append(ComponentUtil.COLON).color(ComponentUtil.getPrefixColor(context.plugin(),
                                                                         context.resourceBundleContext().ruleResourceBundle()
        ));
        return noBlockBreakRuleName
                .appendNewline()
                .appendSpace()
                .appendSpace()
                .appendSpace()
                .appendSpace()
                .append(
                        ComponentUtil.formatChatMessage(
                                context.plugin(),
                                context.resourceBundleContext().ruleResourceBundle(),
                                "noblockbreak.statusinfo",
                                Map.of(
                                        "exemptions", ComponentUtil.translate(exemptions)
                                ),
                                false
                        )
                );
    }

    @Override
    public void unload() {
        blockBreakType.unload();
    }
}
