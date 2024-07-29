package wand555.github.io.challenges.offline_temp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import wand555.github.io.challenges.ChallengesDebugLogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class OfflineTempData {

    private static final Logger logger = ChallengesDebugLogger.getLogger(OfflineTempData.class);

    private final Map<String, Object> data;
    private final ObjectMapper objectMapper;
    private final File file;

    public OfflineTempData(JavaPlugin plugin) {
        Map<String, Object> data1;
        objectMapper = new ObjectMapper();
        file = Paths.get(plugin.getDataFolder().getAbsolutePath(), "offline_temp", "offline_temp.json").toFile();
        if(!file.exists()) {
            data1 = new HashMap<>();
            create();
        }
        else {
            try {
                data1 = objectMapper.readValue(file, new TypeReference<>() {
                });
            } catch (IOException e) {
                logger.warning("Failed to read offline_temp.json! Is it valid JSON syntax?");
                logger.fine(e.getMessage());
                data1 = new HashMap<>();
                create();
            }
        }
        data = data1;
    }

    private void create() {
        try {
            Files.createDirectories(file.getParentFile().toPath());
            Files.createFile(file.toPath());
            Files.write(file.toPath(), "{}".getBytes());
            logger.fine("Created offline_temp.json file and parent folders");
        } catch (IOException e) {
            throw new RuntimeException(e);
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
