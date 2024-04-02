package wand555.github.io.challenges.types.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.types.Data;

public record ItemData(ItemStack itemStackInteractedWith, int amount, Player player) implements Data<Material> {

    public ItemData(ItemStack itemStackInteractedWith, Player player) {
        this(itemStackInteractedWith, 1, player);
    }

    @Override
    public Material mainDataInvolved() {
        return itemStackInteractedWith.getType();
    }
}
