package wand555.github.io.challenges.types.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.types.Data;

/**
 * The amount within the ItemStack is ignored. Only the @amount variable is being used to determine "how many" are involved.
 *
 * @param itemStackInteractedWith
 * @param amount
 * @param player
 */
public record ItemData(ItemStack itemStackInteractedWith, int amount, Player player) implements Data<Material> {

    public ItemData(ItemStack itemStackInteractedWith, Player player) {
        this(itemStackInteractedWith, 1, player);
    }

    @Override
    public Material mainDataInvolved() {
        return itemStackInteractedWith.getType();
    }
}
