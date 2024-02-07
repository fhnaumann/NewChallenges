package wand555.github.io.challenges.types.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record ItemData(ItemStack itemStackInteractedWith, Player player) {
}
