package wand555.github.io.challenges.types.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.types.Data;

public record ItemData(ItemStack itemStackInteractedWith, Player player) implements Data<Material> {
    @Override
    public Material mainDataInvolved() {
        return itemStackInteractedWith.getType();
    }
}
