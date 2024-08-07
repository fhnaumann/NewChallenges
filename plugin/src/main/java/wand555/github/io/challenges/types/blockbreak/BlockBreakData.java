package wand555.github.io.challenges.types.blockbreak;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.types.Data;

import java.util.UUID;

public record BlockBreakData(Material broken, int amount, Player player) implements Data<Material> {

    public BlockBreakData(Material broken, Player player) {
        this(broken, 1, player);
    }

    @Override
    public UUID playerUUID() {
        return player.getUniqueId();
    }

    @Override
    public Material mainDataInvolved() {
        return broken;
    }

}
