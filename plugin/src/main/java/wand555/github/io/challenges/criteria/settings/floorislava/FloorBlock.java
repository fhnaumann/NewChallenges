package wand555.github.io.challenges.criteria.settings.floorislava;

import org.bukkit.Material;

public record FloorBlock(FloorBlockStatus status, Material previousMaterial) {

    public enum FloorBlockStatus {
        REGULAR, MAGMA, LAVA
    }
}
