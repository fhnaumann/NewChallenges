package wand555.github.io.challenges.criteria.rules.noblockbreak;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.criteria.MessageHelper;

import java.util.Map;

public class NoBlockBreakMessageHelper extends MessageHelper {

    public NoBlockBreakMessageHelper(Context context) {
        super(context);
    }

    public void sendViolationAction(BlockBreakData data) {
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().ruleResourceBundle(),
                "noblockbreak.violation",
                Map.of(
                        "player", Component.text(data.player().getName()),
                        "block", Component.translatable(String.format("block.minecraft.%s", data.broken().toString().toLowerCase()))
                )
        );
        context.plugin().getServer().broadcast(toSend);
    }
}
