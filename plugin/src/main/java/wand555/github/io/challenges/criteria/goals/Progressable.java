package wand555.github.io.challenges.criteria.goals;

import org.bukkit.entity.Player;

public interface Progressable extends Commandable {

    void onProgressStatus(Player player);
}
