package wand555.github.io.challenges.criteria.goals;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.inventory.CollectedInventory;

import javax.validation.constraints.NotNull;

public interface InvProgress extends CommandExecutor {

    @NotNull
    public CollectedInventory getCollectedInventory();

    default void openCollectedInv(@NotNull Player player) {
        getCollectedInventory().show(player);
    }

    @Override
    default boolean onCommand(@org.jetbrains.annotations.NotNull CommandSender sender, @org.jetbrains.annotations.NotNull Command command, @org.jetbrains.annotations.NotNull String label, @NotNull @org.jetbrains.annotations.NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            return false;
        }
        if(!command.getName().equalsIgnoreCase(getBaseCommand())) {
            return false;
        }
        if(args.length != 0) {
            return false;
        }
        onShowInvProgressCommand();
        return true;
    }

    public String getBaseCommand();

    public void onShowInvProgressCommand();
}
