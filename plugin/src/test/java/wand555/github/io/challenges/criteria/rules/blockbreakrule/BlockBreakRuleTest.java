package wand555.github.io.challenges.criteria.rules.blockbreakrule;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.rules.noblockbreak.BlockBreakRule;
import wand555.github.io.challenges.criteria.rules.noblockbreak.BlockBreakRuleMessageHelper;
import wand555.github.io.challenges.generated.NoBlockBreakRuleConfig;
import wand555.github.io.challenges.punishments.CancelPunishment;
import wand555.github.io.challenges.punishments.HealthPunishment;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

public class BlockBreakRuleTest {

    private static ResourceBundleContext resourceBundleContext;
    private static DataSourceContext dataSourceContext;

    private ServerMock server;
    private Challenges plugin;

    private BlockBreakRule rule;

    private PlayerMock player;
    private Block toBeBroken;

    private static Context context;
    private BlockBreakRuleMessageHelper messageHelper;


    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.ruleResourceBundle()).thenReturn(CriteriaUtil.loadRuleResourceBundle());
        DataSourceContext dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.materialJSONList()).thenReturn(CriteriaUtil.loadMaterials().getData());
        ChallengeManager manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);
        when(manager.canTakeEffect(any(), any())).thenReturn(true);

        context = mock(Context.class);
        when(context.dataSourceContext()).thenReturn(dataSourceContext);
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        when(context.challengeManager()).thenReturn(manager);
    }

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        player = server.addPlayer("dummy");
        toBeBroken = new BlockMock(Material.DIRT);

        messageHelper = spy(new BlockBreakRuleMessageHelper(context));
        String blockBreakRuleJSON =
                """
                {
                    "exemptions": ["stone"]
                }
                """;
        rule = new BlockBreakRule(context,
                                  new ObjectMapper().readValue(blockBreakRuleJSON, NoBlockBreakRuleConfig.class),
                                  messageHelper
        );

    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testNoBlockBreakTriggerCheck() {
        assertTrue(rule.triggerCheck().applies(new BlockBreakData(Material.DIRT, player)));
        assertFalse(rule.triggerCheck().applies(new BlockBreakData(Material.STONE, player)));
    }

    @Test
    public void testNoBlockBreakTrigger() {
        rule.trigger().actOnTriggered(new BlockBreakData(Material.DIRT, player));
        verify(messageHelper, times(1)).sendViolationAction(new BlockBreakData(Material.DIRT, player));
    }

    @Test
    public void testIsInExemptionsOnBlockBreakEvent() {
        toBeBroken.setType(Material.STONE);
        player.simulateBlockBreak(toBeBroken);
        //rule.onBlockBreak(new BlockBreakEvent(toBeBroken, player));
        verify(messageHelper, never()).sendViolationAction(any());
    }

    @Test
    public void testIsAllowedIfNoCancelPunishment() {
        rule.setPunishments(List.of(mock(HealthPunishment.class)));
        BlockBreakEvent event = player.simulateBlockBreak(toBeBroken);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testIsDisallowedIfCancelPunishment() {
        rule.setPunishments(List.of(mock(CancelPunishment.class)));
        BlockBreakEvent event = player.simulateBlockBreak(toBeBroken);
        assertTrue(event.isCancelled());
    }
}
