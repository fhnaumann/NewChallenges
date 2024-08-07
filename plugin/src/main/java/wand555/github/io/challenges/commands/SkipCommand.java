package wand555.github.io.challenges.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Commandable;
import wand555.github.io.challenges.criteria.goals.MapGoal;
import wand555.github.io.challenges.criteria.goals.Skippable;
import wand555.github.io.challenges.exceptions.UnskippableException;
import wand555.github.io.challenges.teams.Team;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static wand555.github.io.challenges.commands.CommandUtil.failWith;
import static wand555.github.io.challenges.commands.CommandUtil.failWrapperWith;

public class SkipCommand {

    private static final String BASE_CMD = "skip";
    private static final String CMD_NODE_GOAL_NAME = "goalname";

    public static void registerSkipCommand(Context context) {
        new CommandAPICommand(BASE_CMD)
                .withOptionalArguments(customGoalArgument(context))
                .executesPlayer((sender, args) -> {
                    if(!situationAllowsSkip(context.challengeManager())) {
                        throw failWrapperWith(context, "skip.not_in_challenge");
                    }
                    Skippable skippable = args.getByClass(CMD_NODE_GOAL_NAME, Skippable.class);
                    if(skippable == null) {
                        List<Skippable> skippables = skippables(Team.getTeamPlayerIn(context,
                                                                                     sender.getUniqueId()
                        ).getGoals()).toList();
                        if(skippables.isEmpty()) {
                            throw failWrapperWith(context, "skip.no_skippables");
                        } else if(skippables.size() > 1) {
                            throw failWrapperWith(context, "skip.more_than_one_skippable");
                        }
                        skippable = skippables.getFirst();
                    }
                    try {
                        skippable.onSkip(sender);
                    } catch(UnskippableException e) {
                        throw new RuntimeException(
                                "Goal is not skippable, but unskippable goals should have been filtered out earlier!");
                    }
                })
                .register();
    }

    private static Argument<Skippable> customGoalArgument(Context context) {
        return new CustomArgument<>(new StringArgument(CMD_NODE_GOAL_NAME), getSkippableFromName(context))
                .replaceSuggestions(ArgumentSuggestions.stringsAsync(senderSuggestionInfo -> loadToolTips(context,
                                                                                                          senderSuggestionInfo
                )));
    }

    private static CustomArgument.CustomArgumentInfoParser<Skippable, String> getSkippableFromName(Context context) {
        return info -> {
            if(!(info.sender() instanceof Player player)) {
                throw failWith(context, "misc.not_a_player");
            }
            if(!situationAllowsSkip(context.challengeManager())) {
                throw failWith(context, "skip.not_in_challenge");
            }
            String userInput = info.input();
            List<BaseGoal> goals = Team.getTeamPlayerIn(context, player.getUniqueId()).getGoals();
            return findMatchingGoal(context, userInput, goals);
        };
    }

    private static CompletableFuture<String[]> loadToolTips(Context context, SuggestionInfo<CommandSender> senderSuggestionInfo) {
        return CompletableFuture.supplyAsync(() -> {
            if(!(senderSuggestionInfo.sender() instanceof Player player)) {
                throw new RuntimeException("Not a player");
            }
            List<BaseGoal> goals = Team.getTeamPlayerIn(context, player.getUniqueId()).getGoals();
            return suggestGoals(goals);
        });
    }

    private static String[] suggestGoals(List<BaseGoal> goals) {
        return skippables(goals)
                .map(Commandable::getNameInCommand)
                .toArray(String[]::new);
    }

    private static Skippable findMatchingGoal(Context context, String userInput, List<BaseGoal> source) throws CustomArgument.CustomArgumentException {
        Skippable skippable = skippables(source)
                .filter(skippable1 -> skippable1.getNameInCommand().equals(userInput))
                .findFirst()
                .orElseThrow(() -> failWith(context, "skip.unknown"));
        if(!skippable.isFixedOrder()) {
            throw failWith(context, "skip.not_in_fixed_order");
        }
        return skippable;
    }

    private static Stream<Skippable> skippables(List<BaseGoal> goals) {
        return goals.stream()
                    .filter(baseGoal -> !baseGoal.isComplete())
                    .filter(Skippable.class::isInstance)
                    .map(Skippable.class::cast);
    }

    private static boolean situationAllowsSkip(ChallengeManager manager) {
        return manager.isValid() && manager.getGameState() == ChallengeManager.GameState.RUNNING || manager.getGameState() == ChallengeManager.GameState.PAUSED;
    }
}
