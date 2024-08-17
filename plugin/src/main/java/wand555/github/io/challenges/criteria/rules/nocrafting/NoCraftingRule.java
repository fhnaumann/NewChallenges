package wand555.github.io.challenges.criteria.rules.nocrafting;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.criteria.rules.PunishableRule;
import wand555.github.io.challenges.criteria.rules.RuleMessageHelper;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.generated.NoCraftingRuleConfig;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.mapping.DataSourceJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.types.crafting.CraftingData;
import wand555.github.io.challenges.types.crafting.CraftingType;

import java.util.HashSet;
import java.util.Set;

public class NoCraftingRule extends PunishableRule<CraftingData<?>, CraftingTypeJSON> implements Storable<NoCraftingRuleConfig> {

    private final CraftingType craftingType;

    private final NoCraftingRuleConfig config;
    private final Set<CraftingTypeJSON> exemptions;

    public NoCraftingRule(Context context, NoCraftingRuleConfig config, NoCraftingRuleMessageHelper messageHelper) {
        super(context, config.getPunishments(), messageHelper);
        this.config = config;
        this.exemptions = config.getExemptions() != null
                          ? new HashSet<>(ModelMapper.str2CraftingTypeJSONs(context.dataSourceContext().craftingTypeJSONList(),
                                                                            config.getExemptions()
        ))
                          : new HashSet<>();
        this.craftingType = new CraftingType(context,
                                             triggerCheck(),
                                             trigger()
        );
    }

    @Override
    public void addToGeneratedConfig(EnabledRules config) {
        config.setNoCrafting(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }

    @Override
    public void unload() {
        craftingType.unload();
    }

    @Override
    public TriggerCheck<CraftingData<?>> triggerCheck() {
        return data -> {
            if(exemptions.contains(data.mainDataInvolved())) {
                return false;
            }
            if(data.internallyCrafted()) {
                return !config.isInternalCrafting();
            }
            return !switch(data.craftingTypeJSON().getRecipeType()) {
                case CRAFTING ->
                        config.isWorkbenchCrafting(); // 2x2 internal crafting was handled earlier with the additional flag
                case FURNACE, BLASTING, SMOKING -> config.isFurnaceSmelting();
                case CAMPFIRE -> config.isCampfireCooking();
                case SMITHING -> config.isSmithing();
                case STONECUTTING -> config.isStonecutter();
            };
        };
    }

    @Override
    public NoCraftingRuleConfig toGeneratedJSONClass() {
        return new NoCraftingRuleConfig(
                config.isCampfireCooking(),
                exemptions.stream().map(DataSourceJSON::toCode).sorted().toList(),
                config.isFurnaceSmelting(),
                config.isInternalCrafting(),
                toPunishmentsConfig(),
                config.isSmithing(),
                config.isStonecutter(),
                config.isWorkbenchCrafting()
        );
    }
}
