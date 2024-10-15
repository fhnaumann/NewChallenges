package wand555.github.io.challenges.integration;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.commands.LiveCommand;
import wand555.github.io.challenges.commands.LoadCommand;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.goals.GoalCollector;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakCollectedInventory;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoal;
import wand555.github.io.challenges.criteria.goals.blockbreak.BlockBreakGoalMessageHelper;
import wand555.github.io.challenges.files.ChallengeFilesHandler;
import wand555.github.io.challenges.generated.*;
import wand555.github.io.challenges.live.*;
import wand555.github.io.challenges.offline_temp.OfflineTempData;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.WebSocket;
import java.nio.file.Paths;
import java.util.concurrent.CompletionException;

@ExtendWith({IntegrationTestSetupExtension.class})
public class BlockBreakGoalIT {

    /*
    Run only unit test:
    mvn test -P e2e
    Run unit test and then integration test:
    mvn verify -P e2e -Dstage=testing
    Run only integration test and not unit test:
    mvn clean test-compile failsafe:integration-test failsafe:verify -P plugin -Dstage=testing
     */

    private static Context context;
    private BlockBreakGoal blockBreakGoal;
    private static BlockBreakGoalMessageHelper messageHelper;
    private static BlockBreakCollectedInventory collectedInventory;

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private ChallengeManager manager;
    private static Model challenge;
    private AWSEventProvider eventProvider;
    private GoalCollector<Material> goalCollector;
    private static BlockBreakGoalConfig config;
    private WebSocket webSocket;

    @BeforeAll
    public static void setUpStatic() throws IOException {
        context = mock(Context.class);
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.goalResourceBundle()).thenReturn(CriteriaUtil.loadGoalResourceBundle());
        when(resourceBundleContext.miscResourceBundle()).thenReturn(CriteriaUtil.loadMiscResourceBundle());
        when(resourceBundleContext.commandsResourceBundle()).thenReturn(CriteriaUtil.loadCommandResourceBundle());
        DataSourceContext dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.materialJSONList()).thenReturn(CriteriaUtil.loadMaterials().getData());
        when(context.dataSourceContext()).thenReturn(dataSourceContext);
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        messageHelper = spy(new BlockBreakGoalMessageHelper(context));
        collectedInventory = mock(BlockBreakCollectedInventory.class);

        challenge = new ObjectMapper().readValue(BlockBreakGoalIT.class.getResource("block_break_goal_it.json"),
                                                 Model.class
        );
        config = challenge.getGoals().getBlockBreakGoal();
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        player = CriteriaUtil.addWand555PlayerMock(server);
        manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);
        when(manager.canTakeEffect(any(), any())).thenReturn(true);
        when(context.challengeManager()).thenReturn(manager);

        eventProvider = spy(new AWSEventProvider());
        LiveService liveService = new LiveService(new S3ChallengeUploader(), eventProvider);
        when(context.liveService()).thenReturn(liveService);

        goalCollector = new GoalCollector<>(
                context,
                config.getBroken(),
                Material.class,
                config.isFixedOrder(),
                config.isShuffled()
        );

        blockBreakGoal = new BlockBreakGoal(context, config, goalCollector, messageHelper, collectedInventory, null);
        webSocket = eventProvider.setChallengeID(challenge.getMetadata().getChallengeID()).join();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        try {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "").join();
        } catch(CompletionException e) {
            // ignored
        }
    }

    @Test
    public void testFileIsStoredInS3() throws URISyntaxException, IOException {
        File toLoad = new File(BlockBreakGoalIT.class.getResource("block_break_goal_it.json").toURI());
        CriteriaUtil.createChallengeFile(plugin, toLoad);
        ChallengeFilesHandler challengeFilesHandler = new ChallengeFilesHandler(mock(OfflineTempData.class),
                                                                                Paths.get(plugin.getDataFolder().getAbsolutePath(),
                                                                                          "settings"
                                                                                ).toFile()
        );
        when(manager.getChallengeMetadata()).thenReturn(challenge.getMetadata());
        LoadCommand.loadFile(context, challengeFilesHandler, toLoad).join();
        LiveCommand.uploadChallenge(context, new ChallengeFilesHandler.ChallengeLoadStatus(toLoad, challenge.getMetadata()), player).join();
        // player.performCommand("/live block_break_goal_it");
        S3Helper.assertFileExists("block_break_goal_it.json");
    }

    @Test
    public void testEventIsStoredInDynamoDB() throws JsonProcessingException, InterruptedException {
        when(manager.getTime()).thenReturn(10);

        player.simulateBlockBreak(new BlockMock(Material.STONE));

        BlockBreakDataConfig expected = new BlockBreakDataConfig(
                1,
                "stone",
                CriteriaUtil.constructWand555PlayerConfig(),
                10
        );
        eventProvider.lastSentEventFuture.join(); // wait until event is sent in websocket
        System.out.println("Waiting 3s");
        // No clue why this is needed. In IntelliJ this is not needed, but when running the Integration Tests from
        // the command line, they fail despite showing in the logs that the event has been sent before continuing on...
        // This can be solved by using consistent reads in DynamoDBHelper class, but that costs twice as much, while
        // running longer tests in GitHub CI is free :)
        Thread.sleep(3000);
        System.out.println("WAITED 3s");
        BlockBreakDataConfig actual = DynamoDBHelper.queryItems(challenge.getMetadata().getChallengeID(),
                                                                eventProvider.getUUIDFromLastEvent().toString(),
                                                                10,
                                                                BlockBreakDataConfig.class
        );
        assertEquals(expected, actual);
    }
}
