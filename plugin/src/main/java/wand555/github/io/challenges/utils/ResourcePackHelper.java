package wand555.github.io.challenges.utils;

import org.bukkit.Material;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.mapping.ModelMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ResourcePackHelper {

    public static final Map<Material, String> UNICODE_MAPPING = loadUnicodeMappingsFromFile();

    private static Map<Material, String> loadUnicodeMappingsFromFile() {
        Map<Material, String> map = new HashMap<>();
        try(Scanner scanner = new Scanner(Challenges.class.getResourceAsStream("unicode_mapping.csv"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split(",");
                map.put(ModelMapper.str2Mat(split[0], material -> true), split[1]);
            }
        }
        return map;
    }
}
