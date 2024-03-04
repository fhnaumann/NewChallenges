package wand555.github.io.challenges.types;

import org.bukkit.Keyed;
import org.bukkit.entity.Player;

public interface Data<K extends Keyed> {

    Player player();
    K mainDataInvolved();
}
