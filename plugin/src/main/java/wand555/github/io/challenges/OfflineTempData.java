package wand555.github.io.challenges;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class OfflineTempData {

    private final Map<String, Object> data;
    private final ObjectMapper objectMapper;
    private final File file;

    public OfflineTempData(JavaPlugin plugin) {
        objectMapper = new ObjectMapper();
        file = Paths.get(plugin.getDataFolder().getAbsolutePath(), "offline_temp", "offline_temp.json").toFile();
        if(!file.exists()) {
            data = new HashMap<>();
            try {
                Files.createDirectories(file.getParentFile().toPath());
                Files.createFile(file.toPath());
                Files.write(file.toPath(), "{}".getBytes());
                plugin.getLogger().fine("Created offline_temp.json file and parent folders");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                data = objectMapper.readValue(file, new TypeReference<>() {
                });
            } catch (IOException e) {
                throw new RuntimeException("Failed to read offline_temp.json! Is it valid JSON syntax?", e);
            }
        }
    }

    public void addAndSave(String where, Object what) {
        data.put(where, what);
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T get(String where, Class<T> clazz) {
        return clazz.cast(data.get(where));
    }
}
