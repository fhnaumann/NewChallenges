package wand555.github.io.challenges.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.json.JSONArray;
import org.json.JSONObject;
import wand555.github.io.challenges.Challenges;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ResourcePackHelper {

    private static final Map<Material, String> MATERIAL_UNICODE_MAPPING = fillMatUnicodeMappings();
    public static final Map<EntityType, String> ENTITY_UNICODE_MAPPING = fillEntityUnicodeMappings();

    public static <E extends Enum<E>> Component getUnicodeMapping(E from) {
        if(from instanceof Material material) {
            return getMaterialUnicodeMapping(material);
        }
        if(from instanceof EntityType entityType) {
            return getEntityTypeUnicodeMapping(entityType);
        }
        throw new RuntimeException("getUnicodeMapping called with '%s' and that is not an entity type or a material.".formatted(from));
    }

    public static Component getMaterialUnicodeMapping(Material from) {
        String unicode = MATERIAL_UNICODE_MAPPING.get(from);
        if(unicode != null) {
            return Component.text(unicode).append(Component.text(" (").append(EnumConverterHelper.enum2Comp(from, true)).append(Component.text(")")));
        }
        else {
            return EnumConverterHelper.enum2Comp(from, null, true);
        }
    }

    public static Component getEntityTypeUnicodeMapping(EntityType from) {
        String unicode = ENTITY_UNICODE_MAPPING.get(from);
        if(unicode != null) {
            return Component.text(unicode).append(Component.text(" (").append(EnumConverterHelper.enum2Comp(from, true)).append(Component.text(")")));
        }
        else {
            return EnumConverterHelper.enum2Comp(from, null, true);
        }
    }

    public static JSONObject createFontDefaultJSON() {
        JSONObject jsonObject = new JSONObject();
        JSONArray providers = new JSONArray();
        for(Map.Entry<Material, String> entry : MATERIAL_UNICODE_MAPPING.entrySet()) {
            JSONObject providerEntry = createProviderEntry(entry.getKey(), entry.getValue(), null);
            providers.put(providerEntry);
        }
        for(Map.Entry<EntityType, String> entry : ENTITY_UNICODE_MAPPING.entrySet()) {
            JSONObject providerEntry = createProviderEntry(entry.getKey(), entry.getValue(), "challenges");
            providers.put(providerEntry);
        }
        jsonObject.put("providers", providers);
        return jsonObject;
    }

    private static <T extends Keyed> JSONObject createProviderEntry(T key, String unicode, @Nullable String customNameSpace) {
        JSONObject providerEntry = new JSONObject();

        System.out.println(unicode);

        String fileName = (customNameSpace == null ? key.key().asString() : customNameSpace + ":" + key.key().value()) + ".png";
        providerEntry.put("file", fileName);
        JSONArray chars = new JSONArray();
        chars.put(Integer.toHexString(Integer.parseInt(unicode)));
        providerEntry.put("chars", chars);
        providerEntry.put("height", 10);
        providerEntry.put("ascent", 8);
        providerEntry.put("type", "bitmap");

        return providerEntry;
    }

    private static Map<Material, String> fillMatUnicodeMappings() {
        Map<Material, String> map = new HashMap<>();
        try(Scanner scanner = new Scanner(Challenges.class.getResourceAsStream("/material_unicode_mapping.csv"), "UTF-8")) {
            scanner.nextLine(); // skip first row
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split(",");
                //String unescapedUnicode = split[1].replace("'", "");
                String hexConvertedUnicode = new String(Character.toChars(Integer.parseInt(split[1])));
                map.put(EnumConverterHelper.str2Enum(split[0], Material.class), hexConvertedUnicode);
            }
        }
        return map;
    }

    private static Map<EntityType, String> fillEntityUnicodeMappings() {
        // TODO: use python script to create unicode mapping and then load them here (just like with materials)
        /*
        Map<EntityType, String> map = new HashMap<>();
        List<EntityType> entityTypes = Stream.of(EntityType.values()).filter(entityType -> entityType != EntityType.UNKNOWN).toList();
        for(int i=0; i<entityTypes.size(); i++) {
            unicodeCounter += i;
            map.put(entityTypes.get(i), Character.toString(unicodeCounter));
        }
        return map;

         */
        return Map.of();
    }
}
