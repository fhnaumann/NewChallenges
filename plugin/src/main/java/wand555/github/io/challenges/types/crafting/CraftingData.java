package wand555.github.io.challenges.types.crafting;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.types.Data;
import wand555.github.io.challenges.types.TriggableWithoutPlayerOnline;

import java.util.Objects;
import java.util.UUID;

public record CraftingData<E extends Event>(E event, UUID playerUUID, int amount, CraftingTypeJSON craftingTypeJSON, boolean internallyCrafted) implements Data<E, CraftingTypeJSON>, TriggableWithoutPlayerOnline {

    /*
    There is no way to distinguish between internal crafting and workbench crafting in CraftingTypeJSON, because both
    use the exact same recipes. If the flag 'internallyCrafted' is false, that does not automatically indicate that the
    recipe was used in the workbench. It may be used in a furnace, smithing table, etc. It just explicitly differentiates
    between internal crafting and workbench crafting
     */

    public CraftingData(E event, Player player, CraftingTypeJSON craftingTypeJSON, boolean internallyCrafted) {
        this(event, player.getUniqueId(), craftingTypeJSON, internallyCrafted);
    }

    public CraftingData(E event, UUID playerUUID, CraftingTypeJSON craftingTypeJSON, boolean internallyCrafted) {
        this(event, playerUUID, 1, craftingTypeJSON, internallyCrafted);
    }

    public CraftingData(E event, Player player, CraftingTypeJSON craftingTypeJSON) {
        this(event, player, craftingTypeJSON, false);
    }

    @Override
    @Nullable
    public Player player() {
        return Bukkit.getPlayer(playerUUID) != null ? Bukkit.getPlayer(playerUUID) : null;
    }

    @Override
    public CraftingTypeJSON mainDataInvolved() {
        return craftingTypeJSON;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        CraftingData<?> that = (CraftingData<?>) o;
        return amount == that.amount && internallyCrafted == that.internallyCrafted && Objects.equals(playerUUID,
                                                                                                      that.playerUUID
        ) && Objects.equals(craftingTypeJSON, that.craftingTypeJSON);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerUUID, amount, craftingTypeJSON, internallyCrafted);
    }
}
