package wand555.github.io.challenges.criteria.rules.noitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.criteria.rules.PunishableRule;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.generated.NoItemCollectRuleConfig;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.item.ItemData;
import wand555.github.io.challenges.types.item.ItemType;

import java.util.HashSet;
import java.util.Set;

public class NoItemRule extends PunishableRule<ItemData, Material> implements Storable<NoItemCollectRuleConfig> {

    private final ItemType itemType;
    private final Set<Material> exemptions;

    public NoItemRule(Context context, NoItemCollectRuleConfig config, NoItemRuleMessageHelper messageHelper) {
        super(context, config.getPunishments(), PunishableRule.Result.fromJSONString(config.getResult().value()), messageHelper);
        this.itemType = new ItemType(context, triggerCheck(), trigger(), cancelIfDeny(), cancelIfDeny());
        this.exemptions = config.getExemptions() != null ? new HashSet<>(ModelMapper.str2Materials(context.dataSourceContext().materialJSONList(), config.getExemptions())) : new HashSet<>();
    }

    @Override
    public void addToGeneratedConfig(EnabledRules config) {
        config.setNoItem(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }

    @Override
    public NoItemCollectRuleConfig toGeneratedJSONClass() {
        return new NoItemCollectRuleConfig(
                exemptions.stream().map(DataSourceJSON::toCode).sorted().toList(),
                toPunishmentsConfig(),
                NoItemCollectRuleConfig.Result.fromValue(result.getValue())
        );
    }

    @Override
    public TriggerCheck<ItemData> triggerCheck() {
        return data -> !exemptions.contains(data.itemStackInteractedWith().getType());
    }

    @Override
    public void unload() {
        itemType.unload();
    }
}
