package wand555.github.io.challenges.types.blockbreak;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.types.Data;

public record BlockBreakData(Material broken, Player player) implements Data<Material> {
    @Override
    public Material mainDataInvolved() {
        return broken;
    }

}
