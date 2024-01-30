package wand555.github.io.challenges.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.exceptions.MissingSoundException;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Sound;

import javax.validation.constraints.NotNull;

public class ActionHelper {

    private static final Map<String, Sound> ALL_SOUNDS = Stream.of(Sound.values())
            .collect(Collectors.toMap(
                    sound -> sound.getKey().getKey(),
                    Function.identity()
            ));

    public static void sendAndPlaySound(@NotNull Challenges plugin, @NotNull Component componentToSend, @NotNull ResourceBundle resourceBundle, @NotNull String keyInBundle) {
        String soundToPlayKey = ResourceBundleHelper.getFromBundle(plugin, resourceBundle, keyInBundle);

        plugin.getServer().broadcast(componentToSend);
        try {
            Sound soundToPlay = str2Sound(soundToPlayKey);
            plugin.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                onlinePlayer.playSound(onlinePlayer.getLocation(), soundToPlay, 1f,1f);
            });
        } catch (MissingSoundException e) {
            plugin.getLogger().warning("missing sound");
        }
    }

    private static Sound str2Sound(String soundKey) throws MissingSoundException {
        Sound sound = ALL_SOUNDS.get(soundKey);
        if(sound == null) {
            throw new MissingSoundException();
        }
        return sound;
    }
}
