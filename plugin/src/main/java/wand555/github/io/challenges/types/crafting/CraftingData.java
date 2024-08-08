package wand555.github.io.challenges.types.crafting;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.types.Data;
import wand555.github.io.challenges.types.TriggableWithoutPlayerOnline;

import java.util.UUID;

public record CraftingData(UUID playerUUID, int amount, CraftingTypeJSON craftingTypeJSON, boolean internallyCrafted) implements Data<CraftingTypeJSON>, TriggableWithoutPlayerOnline {

    /*
    There is no way to distinguish between internal crafting and workbench crafting in CraftingTypeJSON, because both
    use the exact same recipes. If the flag 'internallyCrafted' is false, that does not automatically indicate that the
    recipe was used in the workbench. It may be used in a furnace, smithing table, etc. It just explicitly differentiates
    between internal crafting and workbench crafting
     */

    public CraftingData(Player player, CraftingTypeJSON craftingTypeJSON, boolean internallyCrafted) {
        this(player.getUniqueId(), craftingTypeJSON, internallyCrafted);
    }

    public CraftingData(UUID playerUUID, CraftingTypeJSON craftingTypeJSON, boolean internallyCrafted) {
        this(playerUUID, 1, craftingTypeJSON, internallyCrafted);
    }

    public CraftingData(Player player, CraftingTypeJSON craftingTypeJSON) {
        this(player, craftingTypeJSON, false);
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
}
