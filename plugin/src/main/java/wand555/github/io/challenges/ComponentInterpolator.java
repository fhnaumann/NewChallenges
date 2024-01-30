package wand555.github.io.challenges;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.utils.ResourceBundleHelper;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.logging.Level;

public class ComponentInterpolator {

    public static Component interpolate(@NotNull Challenges plugin, @NotNull ResourceBundle bundle, @NotNull String key) {
        return interpolate(plugin, bundle, key, Map.of());
    }
    @NotNull
    public static Component interpolate(@NotNull Challenges plugin, @NotNull ResourceBundle bundle, @NotNull String key, @NotNull Map<String, Component> placeholders) {
        Objects.requireNonNull(bundle);
        Objects.requireNonNull(key);
        Objects.requireNonNull(placeholders);

        String rawText = ResourceBundleHelper.getFromBundle(plugin, bundle, key);

        String prefixColor = ResourceBundleHelper.getFromBundle(plugin, bundle, "chat.color.prefix");
        String defaultColor = ResourceBundleHelper.getFromBundle(plugin, bundle, "chat.color.default");
        String highlightColor = ResourceBundleHelper.getFromBundle(plugin, bundle, "chat.color.highlight");

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
        String baseDisplayName = ResourceBundleHelper.getFromBundle(plugin, bundle, String.format("%s.name", baseName));
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


    public static <E extends Enum<E>> Component translate(E enumToTranslate) {
        String prefix = null;
        if(enumToTranslate instanceof Material material) {
            if(material.isBlock()) {
                prefix = "block";
            }
            else if (material.isItem()) {
                prefix = "item";
            }
        }
        else if(enumToTranslate instanceof EntityType) {
            prefix = "entity";
        }
        if(prefix == null) {
            throw new RuntimeException(String.format("Failed to map enum %s to translatable component!", enumToTranslate));
        }
        return Component.translatable(String.format("%s.minecraft.%s", prefix, enumToTranslate.toString().toLowerCase()));
    }
}
