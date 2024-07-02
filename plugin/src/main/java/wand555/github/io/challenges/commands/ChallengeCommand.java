package wand555.github.io.challenges.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class ChallengeCommand implements CommandExecutor, TabCompleter {

    protected final String baseCmdName;

    public ChallengeCommand(String baseCmdName) {
        this.baseCmdName = baseCmdName;
    }

    protected abstract void handleCommand(CommandSender commandSender, List<String> args);

    protected abstract List<String> handleTabComplete(CommandSender commandSender, List<String> args);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @javax.validation.constraints.NotNull @NotNull String[] args) {
        if(command.getName().equals(baseCmdName)) {
            handleCommand(sender, Arrays.stream(args).toList());
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @javax.validation.constraints.NotNull @NotNull String[] args) {
        if(command.getName().equals(baseCmdName)) {
            return handleTabComplete(sender, Arrays.stream(args).toList());
        }
        else {
            return List.of();
        }
    }
}
