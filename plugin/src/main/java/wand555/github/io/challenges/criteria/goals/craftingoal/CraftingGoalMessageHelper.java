package wand555.github.io.challenges.criteria.goals.craftingoal;

import net.kyori.adventure.text.Component;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.Collect;
import wand555.github.io.challenges.criteria.goals.GoalMessageHelper;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.types.crafting.CraftingData;
import wand555.github.io.challenges.utils.ActionHelper;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import java.util.HashMap;
import java.util.Map;

public class CraftingGoalMessageHelper extends GoalMessageHelper<CraftingData<?>, CraftingTypeJSON> {
    public CraftingGoalMessageHelper(Context context) {
        super(context);
    }

    @Override
    protected String getGoalNameInResourceBundle() {
        return CraftingGoal.NAME_IN_RB;
    }

    @Override
    public void sendSingleStepAction(CraftingData<?> data, Collect collect) {
        String key = "%s.single.step.without_source.message".formatted(getGoalNameInResourceBundle());
        Map<String, Component> placeholders = new HashMap<>();
        Map<String, Component> commonPlaceholder = Map.of(
                "amount", Component.text(collect.getCurrentAmount()),
                "total_amount", Component.text(collect.getAmountNeeded())
        );
        placeholders.putAll(commonPlaceholder);
        placeholders.putAll(additionalStepPlaceholders(data));
        if(data.mainDataInvolved().getSource() != null) {
            key = "%s.single.step.with_source.message".formatted(getGoalNameInResourceBundle());
            placeholders.put("recipe_source", ResourcePackHelper.getMaterialUnicodeMapping(data.mainDataInvolved().getSource()));
        }
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                getGoalBundle(),
                key,
                placeholders
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                getGoalBundle(),
                "%s.single.step.sound".formatted(getGoalNameInResourceBundle())
        );
    }

    @Override
    public void sendSingleReachedAction(CraftingData<?> data, Collect collect) {
        String key = "%s.single.reached.without_source.message".formatted(getGoalNameInResourceBundle());
        Map<String, Component> placeholders = new HashMap<>(additionalStepPlaceholders(data));
        if(data.mainDataInvolved().getSource() != null) {
            key = "%s.single.reached.with_source.message".formatted(getGoalNameInResourceBundle());
            placeholders.put("recipe_source", ResourcePackHelper.getMaterialUnicodeMapping(data.mainDataInvolved().getSource()));
        }
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                getGoalBundle(),
                key,
                placeholders
        );
        ActionHelper.sendAndPlaySound(
                context.plugin(),
                toSend,
                getGoalBundle(),
                "%s.single.reached.sound".formatted(getGoalNameInResourceBundle())
        );
    }

    @Override
    public Map<String, Component> additionalBossBarPlaceholders(CraftingTypeJSON data) {
        return Map.of(
                "recipe_result",
                ResourcePackHelper.getMaterialUnicodeMapping(data.getCraftingResult()),
                "recipe_type",
                ResourcePackHelper.getMaterialUnicodeMapping(CraftingTypeJSON.recipeType2MaterialDisplay(data.getRecipeType()))
        );
    }

    @Override
    protected Map<String, Component> additionalStepPlaceholders(CraftingData<?> data) {
        return Map.of(
                "player",
                Component.text(data.playerName()),
                "recipe_result",
                ResourcePackHelper.getMaterialUnicodeMapping(data.mainDataInvolved().getCraftingResult()),
                "recipe_type",
                ResourcePackHelper.getMaterialUnicodeMapping(CraftingTypeJSON.recipeType2MaterialDisplay(data.mainDataInvolved().getRecipeType()))
        );
    }
}
