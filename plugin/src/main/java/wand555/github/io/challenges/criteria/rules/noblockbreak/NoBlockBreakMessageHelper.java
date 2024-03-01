package wand555.github.io.challenges.criteria.rules.noblockbreak;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.rules.RuleMessageHelper;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.criteria.MessageHelper;

import java.util.Map;

public class NoBlockBreakMessageHelper extends RuleMessageHelper<BlockBreakData, Material> {

    public NoBlockBreakMessageHelper(Context context) {
        super(context);
    }

    @Override
    public Map<String, Component> additionalViolationPlaceholders(BlockBreakData data) {
        return Map.of(
                "player", Component.text(data.player().getName()),
                "block", Component.translatable(data.broken())
        );
    }

    @Override
    protected String getRuleNameInResourceBundle() {
        return "noblockbreak";
    }
}
