package wand555.github.io.challenges.types.blockplace;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.types.Data;

import java.util.UUID;

public record BlockPlaceData(Material placed, int amount, Player player) implements Data<Material> {

    public BlockPlaceData(Material placed, Player player) {
        this(placed, 1, player);
    }

    @Override
    public UUID playerUUID() {
        return player.getUniqueId();
    }

    @Override
    public Material mainDataInvolved() {
        return placed;
    }
}
