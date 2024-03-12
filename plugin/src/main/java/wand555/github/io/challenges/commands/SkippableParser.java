package wand555.github.io.challenges.commands;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.ArgumentParseException;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import wand555.github.io.challenges.criteria.goals.BaseGoal;
import wand555.github.io.challenges.criteria.goals.Goal;
import wand555.github.io.challenges.criteria.goals.Skippable;

import java.util.List;
import java.util.Optional;

public class SkippableParser<C> implements ArgumentParser<C, Skippable> {

    private final List<BaseGoal> goals;

    public SkippableParser(List<BaseGoal> goals) {
        this.goals = goals;
    }

    @Override
    public @NonNull ArgumentParseResult<@NonNull Skippable> parse(@NonNull CommandContext<@NonNull C> commandContext, @NonNull CommandInput commandInput) {
        String userInput = commandInput.peekString();
        Optional<Skippable> optionalSkippable = goals.stream()
                .filter(Skippable.class::isInstance)
                .map(Skippable.class::cast)
                .filter(skippable -> skippable.getSkipNameInCommand().equals(userInput))
                .findFirst();
        if(optionalSkippable.isPresent()) {
           commandInput.readString(); // remove string from the input
            return ArgumentParseResult.success(optionalSkippable.get());
        }
        else {
            return ArgumentParseResult.failure(new RuntimeException()); // todo throw proper error
        }
    }
}
