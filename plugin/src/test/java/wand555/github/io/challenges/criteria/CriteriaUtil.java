package wand555.github.io.challenges.criteria;

import be.seeseemelk.mockbukkit.ServerMock;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.event.Event;
import wand555.github.io.challenges.FileManager;
import wand555.github.io.challenges.mapping.EntityTypeDataSource;
import wand555.github.io.challenges.mapping.MaterialDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class CriteriaUtil {

    public static ResourceBundle loadPunishmentResourceBundle() {
        return ResourceBundle.getBundle("punishments", Locale.ENGLISH, UTF8ResourceBundleControl.get());
    }

    public static ResourceBundle loadGoalResourceBundle() {
        return ResourceBundle.getBundle("goals", Locale.ENGLISH, UTF8ResourceBundleControl.get());
    }

    public static ResourceBundle loadRuleResourceBundle() {
        return ResourceBundle.getBundle("rules", Locale.ENGLISH, UTF8ResourceBundleControl.get());
    }

    public static InputStream loadJSONSchemaStream() {
        return CriteriaUtil.class.getResourceAsStream("/challenges_schema.json");
    }

    public static InputStream loadSchematronStream() {
        return CriteriaUtil.class.getResourceAsStream("/constraints.sch");
    }

    public static EntityTypeDataSource loadEntities() throws IOException {
        return new ObjectMapper().readValue(FileManager.class.getResourceAsStream("/entity_types.json"), EntityTypeDataSource.class);
    }

    public static MaterialDataSource loadMaterials() throws IOException {
        return new ObjectMapper().readValue(CriteriaUtil.class.getResourceAsStream("/materials.json"), MaterialDataSource.class);
    }

    public static void callEvent(ServerMock server, Event event, int n) {
        IntStream.range(0, n).forEach(ignored -> {
            server.getPluginManager().callEvent(event);
        });
    }
}
