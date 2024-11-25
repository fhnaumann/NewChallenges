package wand555.github.io.challenges.types.blockbreak;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Trigger;
import wand555.github.io.challenges.TriggerCheck;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.generated.MCEventAlias;
import wand555.github.io.challenges.live.LiveService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BlockBreakTypeTest {

    private ServerMock server;
    private Challenges plugin;
    private PlayerMock player;

    private BlockBreakType blockBreakType;

    private TriggerCheck<BlockBreakData> mockedTriggerCheck;
    private Trigger<BlockBreakData> mockedTrigger;

    private BlockBreakEvent blockBreakEvent;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer("dummy");
        mockedTriggerCheck = mock(TriggerCheck.class);
        when(mockedTriggerCheck.applies(any(BlockBreakData.class))).thenReturn(true);
        mockedTrigger = mock(Trigger.class);
        doNothing().when(mockedTrigger).actOnTriggered(any(BlockBreakData.class));
        blockBreakEvent = new BlockBreakEvent(new BlockMock(Material.DIRT), player);
        ChallengeManager manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);
        when(manager.canTakeEffect(any(), any())).thenReturn(true);
        Context mockedContext = mock(Context.class);
        LiveService mockLiveService = CriteriaUtil.mockLiveService();
        when(mockedContext.liveService()).thenReturn(mockLiveService);
        when(mockedContext.plugin()).thenReturn(plugin);
        when(mockedContext.challengeManager()).thenReturn(manager);
        blockBreakType = spy(new BlockBreakType(mockedContext, mockedTriggerCheck, mockedTrigger, MCEventAlias.EventType.BLOCK_BREAK_GOAL));
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testOnBlockBreak() {
        blockBreakType.onBlockBreak(blockBreakEvent);
        verify(mockedTriggerCheck, times(1)).applies(any(BlockBreakData.class));
        verify(mockedTrigger, times(1)).actOnTriggered(any(BlockBreakData.class));
    }
}
