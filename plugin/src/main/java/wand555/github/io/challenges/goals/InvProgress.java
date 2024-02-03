package wand555.github.io.challenges.goals;

import org.bukkit.entity.Player;
import wand555.github.io.challenges.inventory.CollectedInventory;

import javax.validation.constraints.NotNull;

public interface InvProgress {

    @NotNull
    public CollectedInventory getCollectedInventory();

    default void openCollectedInv(@NotNull Player player) {
        getCollectedInventory().show(player);
    }
}
