package wand555.github.io.challenges.criteria;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.offline_temp.OfflineTempData;

import java.util.Random;

import static org.mockito.Mockito.mock;

public abstract class CriteriaTest<C, M extends MessageHelper> {

    protected C criteria;
    protected M messageHelper;
    protected Context context;
    protected ServerMock server;
    protected Challenges plugin;
    protected PlayerMock player;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer();

        criteria = createCriteria();
        messageHelper = createMessageHelper();
        ChallengeManager manager = mock(ChallengeManager.class);
        context = new Context(plugin, createResourceBundleContext(), createDataSourceContext(), null, manager, new Random(), new OfflineTempData(plugin));
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    protected abstract C createCriteria();

    protected abstract M createMessageHelper();

    protected abstract ResourceBundleContext createResourceBundleContext();
    protected abstract DataSourceContext createDataSourceContext();
}
