package wand555.github.io.challenges.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public class MyLoadCommand extends ChallengeCommand {
    public MyLoadCommand(String baseCmdName) {
        super(baseCmdName);
    }

    @Override
    protected void handleCommand(CommandSender commandSender, List<String> args) {

    }

    @Override
    protected List<String> handleTabComplete(CommandSender commandSender, List<String> args) {
        return null;
    }
}
