package wand555.github.io;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.generated.TestOutputSchema;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.rules.NoBlockBreakRule;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
public class FileManagerTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;
    private ResourceBundle bundle;
    private ModelMapper mapper;
    private Context context;

    private ChallengeManager challengeManager;
    private NoBlockBreakRule noBlockBreakRule;

    @BeforeEach
    public void setUp() throws IOException {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer();
        bundle = ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get());
        JsonNode schemaRoot = new ObjectMapper().readTree(new File("src/test/resources/challenges_schema.json"));
        context = new Context(plugin, new ResourceBundleContext(bundle, null, null), schemaRoot, new ChallengeManager(plugin));
        mapper = new ModelMapper(context);



        challengeManager = mock(ChallengeManager.class);

        //noBlockBreakRule = new NoBlockBreakRule(plugin, bundle, Set.of(Material.STONE));
        //when(noBlockBreakRule.toGeneratedJSONClass()).thenAnswer(invocation -> noBlockBreakRule.toGeneratedJSONClass());
        //when(challengeManager.getRules()).thenReturn(List.of(noBlockBreakRule));
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testWriteToFile() {
        StringWriter stringWriter = new StringWriter();
        FileManager.writeToFile(challengeManager, stringWriter);
        //assertEquals("", stringWriter.toString());
    }

    @Test
    public void testReadFromFile() throws IOException {
    }
}
