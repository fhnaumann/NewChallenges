package wand555.github.io.challenges.criteria.rules.noblockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.types.EventContainer;
import wand555.github.io.challenges.types.blockbreak.BlockBreakType;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.generated.NoBlockBreakRuleConfig;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.criteria.rules.PunishableRule;

import java.util.*;

public class NoBlockBreakRule extends PunishableRule<BlockBreakData> implements Triggable<BlockBreakData>, Storable<NoBlockBreakRuleConfig> {

    private final BlockBreakType blockBreakType;
    private final Set<Material> exemptions;

    public NoBlockBreakRule(Context context, NoBlockBreakRuleConfig config, NoBlockBreakMessageHelper messageHelper) {
        super(context, config.getPunishments(), PunishableRule.Result.fromJSONString(config.getResult().value()), messageHelper);
        this.exemptions = new HashSet<>(ModelMapper.str2Mat(config.getExemptions(), material -> true));

        blockBreakType = new BlockBreakType(context, triggerCheck(), trigger(), cancelIfDeny());
    }
    @Override
    public TriggerCheck<BlockBreakData> triggerCheck() {
        return data -> !exemptions.contains(data.broken());
    }

    @Override
    public Trigger<BlockBreakData> trigger() {
        return data -> {
            messageHelper.sendViolationAction(data);
            enforcePunishments(data.player());
        };
    }

    @Override
    protected Player playerFrom(BlockBreakData data) {
        return data.player();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NoBlockBreakRule that = (NoBlockBreakRule) o;
        return Objects.equals(exemptions, that.exemptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), exemptions);
    }

    @Override
    public NoBlockBreakRuleConfig toGeneratedJSONClass() {
        return new NoBlockBreakRuleConfig(
                exemptions.stream().map(DataSourceJSON::toCode).sorted().toList(), // always sort when moving from set to list
                toPunishmentsConfig(),
                NoBlockBreakRuleConfig.Result.fromValue(getResult().getValue())
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
        ).append(ComponentUtil.COLON).color(ComponentUtil.getPrefixColor(context.plugin(), context.resourceBundleContext().ruleResourceBundle()));
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
}
