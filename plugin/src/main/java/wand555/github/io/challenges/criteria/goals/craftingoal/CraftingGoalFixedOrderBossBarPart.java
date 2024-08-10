package wand555.github.io.challenges.criteria.goals.craftingoal;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.bossbar.FixedOrderBossBarPart;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.HashMap;
import java.util.Map;

public class CraftingGoalFixedOrderBossBarPart extends FixedOrderBossBarPart<CraftingTypeJSON> {
    public CraftingGoalFixedOrderBossBarPart(Context context, GoalInformation<CraftingTypeJSON> goalInformation, GoalCollector<CraftingTypeJSON> goalCollector) {
        super(context, goalInformation, goalCollector);
    }

    @Override
    public Component buildPart() {
        CraftingTypeJSON data = goalCollector.getCurrentlyToCollect().getKey();
        Collect collect = goalCollector.getCurrentlyToCollect().getValue();
        String key = "%s.bossbar.fixedOrder.without_source.message".formatted(goalInformation.goalNameInResourceBundle());
        Map<String, Component> placeholdersWithHighlight = new HashMap<>();
        Map<String, Component> placeholdersWithoutHighlight = new HashMap<>();
        placeholdersWithHighlight.put("amount", Component.text(collect.getCurrentAmount()));
        placeholdersWithHighlight.put("total_amount", Component.text(collect.getAmountNeeded()));
        placeholdersWithoutHighlight.putAll(goalInformation.additionalPlaceholderInformation().additionalPlaceholders(data));
        if(data.getSource() != null) {
            key = "%s.bossbar.fixedOrder.with_source.message".formatted(goalInformation.goalNameInResourceBundle());
            placeholdersWithoutHighlight.put("recipe_source", ResourcePackHelper.getMaterialUnicodeMapping(data.getSource()));
        }
        return ComponentUtil.formatBossBarMessage(
                context.plugin(),
                context.resourceBundleContext().goalResourceBundle(),
                key,
                placeholdersWithHighlight,
                placeholdersWithoutHighlight
        );
    }
}
