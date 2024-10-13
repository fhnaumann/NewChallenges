package wand555.github.io.challenges.criteria;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.files.FileManager;
import wand555.github.io.challenges.criteria.rules.Rule;
import wand555.github.io.challenges.generated.EnabledRules;
import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.generated.PlayerConfig;
import wand555.github.io.challenges.generated.RulesConfig;
import wand555.github.io.challenges.live.ChallengeUploader;
import wand555.github.io.challenges.live.EventProvider;
import wand555.github.io.challenges.live.LiveService;
import wand555.github.io.challenges.mapping.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class CriteriaUtil {

    public static LiveService mockLiveService() {
        LiveService liveService = mock(LiveService.class);
        when(liveService.challengeUploader()).thenReturn(mock(ChallengeUploader.class));
        when(liveService.eventProvider()).thenReturn(mock(EventProvider.class));
        return liveService;
    }

    public static Model mockModel(Function<Model, Object> test, Class<?> mockedPart) {
        Model mockedModel = mock(Model.class);
        when(test.apply(mockedModel)).thenReturn(mock(mockedPart));
        return mockedModel;
    }

    public static Model mockARule() {
        Model modelMock = mock(Model.class);
        RulesConfig rulesConfigMock = mock(RulesConfig.class);
        EnabledRules enabledRulesMock = mock(EnabledRules.class);
        when(rulesConfigMock.getEnabledRules()).thenReturn(enabledRulesMock);
        when(modelMock.getRules()).thenReturn(rulesConfigMock);
        return modelMock;
    }


    public static void mockARule(Consumer<Model> setWhen, Context context, Rule rule) {
        Model mockModel = CriteriaUtil.mockARule();
        setWhen.accept(mockModel);
        CriteriaMapper.Criterias criterias = CriteriaMapper.mapCriterias(context, mockModel);
        if(criterias.rules().isEmpty()) {
            fail("Rules list is empty after mapping. Did you forget to add the mapping from the JSON code to the Java classes?");
        }
        assertEquals(criterias.rules().getFirst(), rule);
    }

    public static ResourceBundle loadPunishmentResourceBundle() {
        return ResourceBundle.getBundle("punishments", Locale.ENGLISH, UTF8ResourceBundleControl.get());
    }

    public static ResourceBundle loadGoalResourceBundle() {
        return ResourceBundle.getBundle("goals", Locale.ENGLISH, UTF8ResourceBundleControl.get());
    }

    public static ResourceBundle loadRuleResourceBundle() {
        return ResourceBundle.getBundle("rules", Locale.ENGLISH, UTF8ResourceBundleControl.get());
    }

    public static ResourceBundle loadCommandResourceBundle() {
        return ResourceBundle.getBundle("commands", Locale.ENGLISH, UTF8ResourceBundleControl.get());
    }

    public static ResourceBundle loadMiscResourceBundle() {
        return ResourceBundle.getBundle("misc", Locale.ENGLISH, UTF8ResourceBundleControl.get());
    }

    public static InputStream loadJSONSchemaStream() {
        return CriteriaUtil.class.getResourceAsStream("/challenges_schema.json");
    }

    public static JsonNode loadJSONSchemaStreamAsJSONNode() {
        try {
            return new ObjectMapper().readTree(loadJSONSchemaStream());
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static InputStream loadSchematronStream() {
        return CriteriaUtil.class.getResourceAsStream("/constraints.sch");
    }

    public static EntityTypeDataSource loadEntities() throws IOException {
        return new ObjectMapper().readValue(FileManager.class.getResourceAsStream("/entity_types.json"),
                                            EntityTypeDataSource.class
        );
    }

    public static MaterialDataSource loadMaterials() throws IOException {
        return new ObjectMapper().readValue(CriteriaUtil.class.getResourceAsStream("/materials.json"),
                                            MaterialDataSource.class
        );
    }

    public static DeathMessageDataSource loadDeathMessages() throws IOException {
        return new ObjectMapper().readValue(CriteriaUtil.class.getResourceAsStream(
                "/death_messages_as_data_source_JSON.json"), DeathMessageDataSource.class);
    }

    public static CraftingTypeDataSource loadCraftingTypes() throws IOException {
        return new ObjectMapper().readValue(CriteriaUtil.class.getResourceAsStream("/craftables.json"), CraftingTypeDataSource.class);
    }

    public static void callEvent(ServerMock server, Event event, int n) {
        IntStream.range(0, n).forEach(ignored -> {
            server.getPluginManager().callEvent(event);
        });
    }

    public static void reconnect(ServerMock serverMock, PlayerMock player, Consumer<Player> rightBeforeLeave) {
        rightBeforeLeave.accept(player);
        boolean disconnected = player.disconnect();
        if(!disconnected) {
            fail("Failed to disconnect! Player already disconnected?");
        }
        serverMock.getScheduler().performOneTick();
        boolean reconnected = player.reconnect();
        if(!reconnected) {
            fail("Failed to join back!");
        }
        serverMock.getScheduler().performOneTick();
    }

    public static PlayerMock addWand555PlayerMock(ServerMock serverMock) {
        PlayerMock playerMock = new PlayerMock(serverMock, "wand555", UUID.fromString("c41c9dcf-20cb-406d-aacd-bde2320283c6"));
        try {
            playerMock.getPlayerProfile().getTextures().setSkin(URI.create("http://textures.minecraft.net/texture/78c0ae51af17c299a4cff889054f04db731f490483614fa14588c45822fb6970").toURL());
        } catch(MalformedURLException e) {
            fail("Failed to set skin URL for fake wand555 player.", e);
        }
        serverMock.addPlayer(playerMock);
        return playerMock;
    }

    public static PlayerConfig constructWand555PlayerConfig() {
        return new PlayerConfig(
                "wand555",
                "c41c9dcf-20cb-406d-aacd-bde2320283c6",
                "http://textures.minecraft.net/texture/78c0ae51af17c299a4cff889054f04db731f490483614fa14588c45822fb6970"
        );
    }

    public static void createChallengeFile(JavaPlugin plugin, File sourceFile) {
        File folderContainingChallenges = Paths.get(plugin.getDataFolder().getAbsolutePath(), "settings").toFile();
        if(!folderContainingChallenges.exists()) {
            folderContainingChallenges.mkdirs();
        }
        try {
            Files.copy(sourceFile.toPath(), new File(folderContainingChallenges, sourceFile.getName()).toPath());
        } catch(IOException e) {
            fail(e);
        }
    }
}
