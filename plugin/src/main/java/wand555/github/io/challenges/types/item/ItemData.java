package wand555.github.io.challenges.types.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import wand555.github.io.challenges.types.Data;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

/**
 * The amount within the ItemStack is ignored. Only the @amount variable is being used to determine "how many" are involved.
 *
 * @param itemStackInteractedWith
 * @param amount
 * @param player
 */
public record ItemData<E extends Event>(E event, ItemStack itemStackInteractedWith, int amount, Player player) implements Data<E, Material> {

    public ItemData(E event, ItemStack itemStackInteractedWith, Player player) {
        this(event, itemStackInteractedWith, 1, player);
    }

    @Override
    public UUID playerUUID() {
        return player.getUniqueId();
    }

    @Override
    public Material mainDataInvolved() {
        return itemStackInteractedWith.getType();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        ItemData<?> itemData = (ItemData<?>) o;
        return amount == itemData.amount && Objects.equals(itemStackInteractedWith,
                                                           itemData.itemStackInteractedWith
        ) && Objects.equals(player, itemData.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStackInteractedWith, amount, player);
    }
}
