package wand555.github.io.challenges.criteria.rules.noitem;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.rules.RuleMessageHelper;
import wand555.github.io.challenges.types.item.ItemData;

import java.util.Map;

public class NoItemRuleMessageHelper extends RuleMessageHelper<ItemData, Material> {
    public NoItemRuleMessageHelper(Context context) {
        super(context);
    }

    @Override
    public Map<String, Component> additionalViolationPlaceholders(ItemData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "item", Component.translatable(data.itemStackInteractedWith().getType())
        );
    }

    @Override
    protected String getRuleNameInResourceBundle() {
        return "noitem";
    }
}
