package wand555.github.io.challenges.criteria.rules.noblockplacerule;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.rules.Rule;
import wand555.github.io.challenges.criteria.rules.noblockbreak.BlockBreakRule;
import wand555.github.io.challenges.criteria.rules.noblockbreak.BlockBreakRuleMessageHelper;
import wand555.github.io.challenges.criteria.rules.noblockplace.NoBlockPlaceRule;
import wand555.github.io.challenges.criteria.rules.noblockplace.NoBlockPlaceRuleMessageHelper;
import wand555.github.io.challenges.generated.Model;
import wand555.github.io.challenges.generated.NoBlockBreakRuleConfig;
import wand555.github.io.challenges.generated.NoBlockPlaceRuleConfig;
import wand555.github.io.challenges.mapping.CriteriaMapper;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.types.blockplace.BlockPlaceData;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class NoBlockPlaceRuleTest {
    private ServerMock server;
    private Challenges plugin;

    private NoBlockPlaceRule rule;

    private PlayerMock player;
    private Block toBeBroken;

    private static Context context;
    private NoBlockPlaceRuleMessageHelper messageHelper;


    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.ruleResourceBundle()).thenReturn(CriteriaUtil.loadRuleResourceBundle());
        DataSourceContext dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.materialJSONList()).thenReturn(CriteriaUtil.loadMaterials().getData());
        ChallengeManager manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);

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

        messageHelper = spy(new NoBlockPlaceRuleMessageHelper(context));
        String noblockPlaceRuleJSON =
                """
                {
                    "exemptions": ["stone"]
                }
                """;
        rule = new NoBlockPlaceRule(context,
                                    new ObjectMapper().readValue(noblockPlaceRuleJSON, NoBlockPlaceRuleConfig.class),
                                    messageHelper
        );

    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testIsMappedByCriteriaMapper() {
        NoBlockPlaceRuleConfig config = new NoBlockPlaceRuleConfig();
        CriteriaUtil.mockARule(
                model -> when(model.getRules().getEnabledRules().getNoBlockPlace()).thenReturn(config),
                context,
                new NoBlockPlaceRule(context, config, messageHelper)
        );
    }

    @Test
    public void testNoBlockPlaceTriggerCheck() {
        assertTrue(rule.triggerCheck().applies(new BlockPlaceData(Material.DIRT, player)));
        assertFalse(rule.triggerCheck().applies(new BlockPlaceData(Material.STONE, player)));
    }

    @Test
    public void testNoBlockPlaceTrigger() {
        rule.trigger().actOnTriggered(new BlockPlaceData(Material.DIRT, player));
        verify(messageHelper, times(1)).sendViolationAction(new BlockPlaceData(Material.DIRT, player));
    }

    @Test
    public void testIsInExemptionsOnBlockPlaceEvent() {
        toBeBroken.setType(Material.STONE);
        player.simulateBlockPlace(Material.STONE, player.getLocation());
        //rule.onBlockBreak(new BlockBreakEvent(toBeBroken, player));
        verify(messageHelper, never()).sendViolationAction(any());
    }
}
