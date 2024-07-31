package wand555.github.io.challenges.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.exceptions.MissingSoundException;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Sound;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class ActionHelper {

    private static final Map<String, Sound> ALL_SOUNDS = Stream.of(Sound.values())
                                                               .collect(Collectors.toMap(
                                                                       sound -> sound.getKey().getKey(),
                                                                       Function.identity()
                                                               ));

    public static void showAllTitle(@NotNull Component title) {
        showAllTitle(title, null, null);
    }

    public static void showAllTitle(@NotNull Component title, @Nullable Title.Times times) {
        showAllTitle(title, null, times);
    }

    public static void showAllTitle(@NotNull Component title, @Nullable Component subtitle) {
        showAllTitle(title, subtitle, null);
    }

    public static void showAllTitle(@NotNull Component title, @Nullable Component subtitle, @Nullable Title.Times times) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.showTitle(Title.title(title, subtitle != null ? subtitle : Component.empty(), times));
        });
    }

    public static void sendAndPlaySound(@NotNull Challenges plugin, @NotNull Component componentToSend, @NotNull ResourceBundle resourceBundle, @NotNull String keyInBundle) {
        String soundToPlayKey = ResourceBundleHelper.getFromBundle(plugin, resourceBundle, keyInBundle);

        plugin.getServer().broadcast(componentToSend);
        try {
            Sound soundToPlay = str2Sound(soundToPlayKey);
            plugin.getServer().getOnlinePlayers().forEach(onlinePlayer -> {
                onlinePlayer.playSound(onlinePlayer.getLocation(), soundToPlay, 1f, 1f);
            });
        } catch(MissingSoundException e) {
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
