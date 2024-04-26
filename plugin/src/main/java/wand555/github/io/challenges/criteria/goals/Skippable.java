package wand555.github.io.challenges.criteria.goals;

import org.bukkit.entity.Player;
import wand555.github.io.challenges.exceptions.UnskippableException;

public interface Skippable extends Commandable {

    public void onSkip(Player player) throws UnskippableException;
}
