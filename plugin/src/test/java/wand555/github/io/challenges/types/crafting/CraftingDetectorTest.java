package wand555.github.io.challenges.types.crafting;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.inventory.SmithingRecipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public abstract class CraftingDetectorTest {

    protected ServerMock server;
    protected Challenges plugin;
    protected PlayerMock player;

    protected CraftingType craftingType;

    protected TriggerCheck<CraftingData> mockedTriggerCheck;
    protected Trigger<CraftingData> mockedTrigger;
    protected static List<CraftingTypeJSON> craftingTypeJSONList;

    @BeforeAll
    public static void beforeAll() throws IOException {
        craftingTypeJSONList = CriteriaUtil.loadCraftingTypes().getData();
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer("dummy");
        mockedTriggerCheck = mock(TriggerCheck.class);
        when(mockedTriggerCheck.applies(any(CraftingData.class))).thenReturn(true);
        mockedTrigger = mock(Trigger.class);
        doNothing().when(mockedTrigger).actOnTriggered(any(CraftingData.class));
        ChallengeManager manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);
        when(manager.canTakeEffect(any(), any())).thenReturn(true);
        Context mockedContext = mock(Context.class);
        when(mockedContext.plugin()).thenReturn(plugin);
        when(mockedContext.challengeManager()).thenReturn(manager);
        DataSourceContext mockedDataSourceContext = mock(DataSourceContext.class);
        when(mockedDataSourceContext.craftingTypeJSONList()).thenReturn(craftingTypeJSONList);
        when(mockedContext.dataSourceContext()).thenReturn(mockedDataSourceContext);

        craftingType = spy(new CraftingType(mockedContext, mockedTriggerCheck, mockedTrigger));
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }
}
