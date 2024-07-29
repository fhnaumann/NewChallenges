package wand555.github.io.challenges;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigValues {

    public static final Value<Boolean> DEBUG = of("debug", false);
    public static final Value<List<String>> WORLDS = of("worlds", List.of("world", "world_nether", "world_the_end"));

    public static final Value<String> MLG_WORLD = of("mlgWorld", "mlgWorld");

    public static void addDefaults(FileConfiguration fileConfiguration) {
        addDefault(fileConfiguration, DEBUG);
        addDefault(fileConfiguration, WORLDS);
        addDefault(fileConfiguration, MLG_WORLD);
    }

    private static void addDefault(FileConfiguration fileConfiguration, Value<?> value) {
        fileConfiguration.addDefault(value.name(), value.defaultValue());
    }

    public static <T> Value<T> of(String name, T defaultValue) {
        return new Value<>(name, defaultValue);
    }

    public record Value<T>(String name, T defaultValue) {

    }
}
