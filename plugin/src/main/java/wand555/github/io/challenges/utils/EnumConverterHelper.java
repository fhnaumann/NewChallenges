package wand555.github.io.challenges.utils;

import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.key.InvalidKeyException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class EnumConverterHelper {

    public static <E extends  Enum<E> & Keyed & Translatable> Component enum2Comp(E e, boolean translate) {
        return enum2Comp(e, null, translate);
    }

    public static <E extends  Enum<E> & Keyed & Translatable> Component enum2Comp(E e, TextColor enumTextColor, boolean translate) {
        if(translate) {
            return Component.translatable(e.translationKey()).color(enumTextColor);
        }
        else {
            return Component.text(enum2Str(e, false)).color(enumTextColor);
        }
    }

    public static <E extends Enum<E> & Keyed & Translatable> Component enum2Comp(Collection<E> enumCollection, TextColor enumTextColor, Component prefix, Component suffix, Component delimiter, boolean translate) {
        Component component = Component.empty().append(prefix);
        Iterator<E> iterator = enumCollection.iterator();
        while (iterator.hasNext()) {
            Component enumAsComp = enum2Comp(iterator.next(), enumTextColor, translate);
            component = component.append(enumAsComp);
            if(iterator.hasNext()) {
                component = component.append(delimiter);
            }
        }
        component = component.append(suffix);
        return component;
    }

    public static <E extends Enum<E> & Keyed> String enum2Str(E e, boolean includeNamespace) {
        if(includeNamespace) {
            return e.getKey().asString();
        }
        else {
            return e.getKey().asMinimalString();
        }
    }

    public static <E extends Enum<E> & Keyed> Collection<String> enum2Str(Collection<E> enums, boolean includeNamespace) {
        return enums.stream().map(e -> enum2Str(e, includeNamespace)).toList();
    }

    public static <E extends Enum<E>> E str2Enum(String enumString, Class<E> enumClazz) throws RuntimeException {
        if(enumClazz == Material.class) {
            if(enumString.startsWith("minecraft_")) {
                enumString = enumString.replace("minecraft_", "minecraft:");
            }
            Material matched = Material.matchMaterial(enumString);
            if(matched == null) {
                throw new RuntimeException(String.format("Failed to map '%s' to material enum.", enumString));
            }
            return (E) matched;
        }
        try {
            Key key = Key.key(enumString);
            return Enum.valueOf(enumClazz, key.value());
        } catch (InvalidKeyException | IllegalArgumentException e) {
            throw new RuntimeException(String.format("Failed to map '%s' to enum class '%s'.", enumString, enumClazz), e);
        }
    }

    public static <E extends Enum<E>> Collection<E> str2Enum(Collection<String> enumStrings, Class<E> enumClazz) {
        return enumStrings.stream().map(s -> str2Enum(s, enumClazz)).toList();
    }
}
