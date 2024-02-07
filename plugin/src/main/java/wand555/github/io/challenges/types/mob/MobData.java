package wand555.github.io.challenges.types.mob;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public record MobData(EntityType entityInteractedWith, Player player) {
}
