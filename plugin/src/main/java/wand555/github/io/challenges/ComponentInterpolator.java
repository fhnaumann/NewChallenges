package wand555.github.io.challenges;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.logging.Level;

public class ComponentInterpolator {

    @NotNull
    public static Component interpolate(@NotNull Challenges plugin, @NotNull ResourceBundle bundle, @NotNull String key, @NotNull Map<String, Component> placeholders) {
        Objects.requireNonNull(bundle);
        Objects.requireNonNull(key);
        Objects.requireNonNull(placeholders);

        String rawText = getFromBundle(plugin, bundle, key);

        String prefixColor = getFromBundle(plugin, bundle, "chat.color.prefix");
        String defaultColor = getFromBundle(plugin, bundle, "chat.color.default");
        String highlightColor = getFromBundle(plugin, bundle, "chat.color.highlight");

        TagResolver.Single[] mappedPlaceholders = mapPlaceHolders(placeholders, highlightColor);

        Component mappedPlaceHolderComponent = MiniMessage.miniMessage().deserialize(rawText, mappedPlaceholders);

        Component baseName = getBaseName(plugin, bundle, key, prefixColor);
        Component result = baseName.append(mappedPlaceHolderComponent.color(TextColor.fromHexString(defaultColor)));
        return result;
    }

    private static Component getBaseName(@NotNull Challenges plugin, @NotNull ResourceBundle bundle, @NotNull String key, @NotNull String prefixColor) {
        String[] split = key.split("\\.");

        if(split.length == 0) {
            plugin.getLogger().warning(String.format("Base name is missing for key '%s' in resource bundle '%s'.", key, bundle.getBaseBundleName()));
            return Component.empty();
        }
        String baseName = split[0];
        String baseDisplayName = getFromBundle(plugin, bundle, String.format("%s.name", baseName));
        return Component.text(String.format("[%s]: ", baseDisplayName), TextColor.fromHexString(prefixColor));
    }

    private static TagResolver.Single[] mapPlaceHolders(@NotNull Map<String, Component> placeholders, @NotNull String highlightColor) {
        return placeholders.entrySet().stream()
                .map(entry -> Placeholder.component(
                        entry.getKey(),
                        entry.getValue().color(TextColor.fromHexString(highlightColor)))
                )
                .toArray(TagResolver.Single[]::new);
    }

    private static String getFromBundle(@NotNull Challenges plugin, ResourceBundle bundle, String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            // log to ingame
            String msg = String.format("Key '%s' is not found in resource bundle '%s'.", key, bundle.getBaseBundleName());
            plugin.getServer().getLogger().warning(msg);
            plugin.getServer().getLogger().log(Level.WARNING, msg, e);
        } catch (ClassCastException e) {
            // log to ingame
            String msg = String.format("Key '%s' is not a valid string in resource bundle '%s'.", key, bundle.getBaseBundleName());
            plugin.getServer().getLogger().warning(msg);
            plugin.getServer().getLogger().log(Level.WARNING, msg, e);
        }
        return "";
    }
}
