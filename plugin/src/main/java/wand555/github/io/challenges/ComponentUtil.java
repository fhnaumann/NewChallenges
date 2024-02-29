package wand555.github.io.challenges;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import wand555.github.io.challenges.utils.ResourceBundleHelper;
import wand555.github.io.challenges.utils.TimerUtil;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentUtil {

    public static final Component COLON = Component.text(":");

    private static Map<String, Component> time2Placeholders(Map<TimerUtil.TimeParts, String> mappedTime) {
        Map<String, Component> placeholders = new HashMap<>();
        if(mappedTime.containsKey(TimerUtil.TimeParts.DAYS)) {
            placeholders.put("days", Component.text(mappedTime.get(TimerUtil.TimeParts.DAYS)));
        }
        if(mappedTime.containsKey(TimerUtil.TimeParts.HOURS)) {
            placeholders.put("hours", Component.text(mappedTime.get(TimerUtil.TimeParts.HOURS)));
        }
        if(mappedTime.containsKey(TimerUtil.TimeParts.MINUTES)) {
            placeholders.put("minutes", Component.text(mappedTime.get(TimerUtil.TimeParts.MINUTES)));
        }
        placeholders.put("seconds", Component.text(mappedTime.get(TimerUtil.TimeParts.SECONDS)));
        return placeholders;
    }

    public static Component formatTimer(@NotNull Challenges plugin, @NotNull ResourceBundle bundle, @NotNull String key, @NotNull Map<TimerUtil.TimeParts, String> mappedTime) {
        String rawText = ResourceBundleHelper.getFromBundle(plugin, bundle, key);

        String[] split = rawText.split(" ");
        Map<String, Component> placeholders = new HashMap<>();
        if(mappedTime.containsKey(TimerUtil.TimeParts.DAYS)) {
            placeholders.put("days", Component.text(mappedTime.get(TimerUtil.TimeParts.DAYS)));
        }
        else {
            split[0] = null;
        }
        if(mappedTime.containsKey(TimerUtil.TimeParts.HOURS)) {
            placeholders.put("hours", Component.text(mappedTime.get(TimerUtil.TimeParts.HOURS)));
        }
        else {
            split[1] = null;
        }
        if(mappedTime.containsKey(TimerUtil.TimeParts.MINUTES)) {
            placeholders.put("minutes", Component.text(mappedTime.get(TimerUtil.TimeParts.MINUTES)));
        }
        else {
            split[2] = null;
        }
        placeholders.put("seconds", Component.text(mappedTime.get(TimerUtil.TimeParts.SECONDS)));

        String removedUnneededPlaceholders = Arrays.stream(split).filter(Objects::nonNull).collect(Collectors.joining(" "));
        TagResolver.Single[] mappedPlaceholders = mapPlaceHolders(placeholders, ""); // no highlight color
        Component mappedPlaceHolderComponent = MiniMessage.miniMessage().deserialize(removedUnneededPlaceholders, mappedPlaceholders);
        return mappedPlaceHolderComponent;
    }

    public static Component formatBossBarMessage(@NotNull Challenges plugin, @NotNull ResourceBundle bundle, @NotNull String key, @NotNull Map<String, Component> placeholdersWithHighlightColor, @NotNull Map<String, Component> placeholdersNoHighlightColor) {
        String rawText = ResourceBundleHelper.getFromBundle(plugin, bundle, key);

        String defaultColor = ResourceBundleHelper.getFromBundle(plugin, bundle, "chat.color.default");
        String highlightColor = ResourceBundleHelper.getFromBundle(plugin, bundle, "chat.color.highlight");

        TagResolver.Single[] mappedPlaceholdersWithHighlightColor = mapPlaceHolders(placeholdersWithHighlightColor, highlightColor);
        TagResolver.Single[] mappedPlaceholdersNoHighlightColor = mapPlaceHolders(placeholdersNoHighlightColor, "");
        TagResolver.Single[] placeholders = Stream.of(mappedPlaceholdersWithHighlightColor, mappedPlaceholdersNoHighlightColor).flatMap(Stream::of).toArray(TagResolver.Single[]::new);
        Component mappedPlaceHolderComponent = MiniMessage.miniMessage().deserialize(rawText, placeholders);
        return mappedPlaceHolderComponent.color(TextColor.fromHexString(defaultColor));
    }

    public static TextColor getPrefixColor(@NotNull Challenges plugin, ResourceBundle bundle) {
        String hexColor = ResourceBundleHelper.getFromBundle(plugin, bundle, "chat.color.prefix");
        return TextColor.fromHexString(hexColor);
    }

    public static Component formatChatMessage(@NotNull Challenges plugin, @NotNull ResourceBundle bundle, @NotNull String key, boolean prefix) {
        return formatChatMessage(plugin, bundle, key, Map.of(), prefix);
    }

    public static Component formatChatMessage(@NotNull Challenges plugin, @NotNull ResourceBundle bundle, @NotNull String key, @NotNull Map<String, Component> placeholders) {
        return formatChatMessage(plugin, bundle, key, placeholders, true);
    }

    public static Component formatChatMessage(@NotNull Challenges plugin, @NotNull ResourceBundle bundle, @NotNull String key) {
        return formatChatMessage(plugin, bundle, key, Map.of());
    }
    @NotNull
    public static Component formatChatMessage(@NotNull Challenges plugin, @NotNull ResourceBundle bundle, @NotNull String key, @NotNull Map<String, Component> placeholders, boolean prefix) {
        Objects.requireNonNull(bundle);
        Objects.requireNonNull(key);
        Objects.requireNonNull(placeholders);

        String rawText = ResourceBundleHelper.getFromBundle(plugin, bundle, key);

        String prefixColor = ResourceBundleHelper.getFromBundle(plugin, bundle, "chat.color.prefix");
        String defaultColor = ResourceBundleHelper.getFromBundle(plugin, bundle, "chat.color.default");
        String highlightColor = ResourceBundleHelper.getFromBundle(plugin, bundle, "chat.color.highlight");

        TagResolver.Single[] mappedPlaceholders = mapPlaceHolders(placeholders, highlightColor);

        Component mappedPlaceHolderComponent = MiniMessage.miniMessage().deserialize(rawText, mappedPlaceholders);

        if(prefix) {
            Component baseName = getBaseName(plugin, bundle, key, prefixColor);
            return baseName.append(mappedPlaceHolderComponent.color(TextColor.fromHexString(defaultColor)));
        }
        else {
            return mappedPlaceHolderComponent.color(TextColor.fromHexString(defaultColor));
        }
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

    @Deprecated
    public static  <E extends Enum<E>> Component translate(Collection<E> enumCollectionToTranslate) {
        return enumCollectionToTranslate.stream().map(ComponentUtil::translate).reduce(Component.empty(), Component::append);
    }

    @Deprecated()
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
