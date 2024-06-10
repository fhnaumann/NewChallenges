package wand555.github.io.challenges;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    private static OfflineTempData instance;

    private final Map<String, Object> data;
    private final ObjectMapper objectMapper;
    private final File file;

    private OfflineTempData() {
        data = new HashMap<>();
        objectMapper = new ObjectMapper();
        file = Paths.get(JavaPlugin.getPlugin(Challenges.class).getDataFolder().getAbsolutePath(), "offline_temp", "offline_temp.json").toFile();
        if(!file.exists()) {
            try {
                Files.createFile(file.toPath());
                JavaPlugin.getPlugin(Challenges.class).getLogger().fine("Created offline_temp.json file and parent folders");
            } catch (IOException e) {
                throw new RuntimeException(e);
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

    public static OfflineTempData getInstance() {
        if(instance == null) {
            instance = new OfflineTempData();
        }
        return instance;
    }
}
