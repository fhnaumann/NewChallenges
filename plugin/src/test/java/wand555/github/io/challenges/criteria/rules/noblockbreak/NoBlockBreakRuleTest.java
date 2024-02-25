package wand555.github.io.challenges.criteria.rules.noblockbreak;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.generated.NoBlockBreakRuleConfig;
import wand555.github.io.challenges.generated.ChallengesSchema;
import wand555.github.io.challenges.mapping.MaterialDataSource;
import wand555.github.io.challenges.mapping.MaterialJSON;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.criteria.rules.noblockbreak.NoBlockBreakRule;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class NoBlockBreakRuleTest {

    private static ResourceBundleContext resourceBundleContext;
    private static JsonNode schemaRoot;
    private static DataSourceContext dataSourceContext;

    private ServerMock server;
    private Challenges plugin;

    private NoBlockBreakRule rule;

    private PlayerMock player;
    private Block toBeBroken;

    private Context context;



    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("rules", Locale.ENGLISH, UTF8ResourceBundleControl.get());
        resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.ruleResourceBundle()).thenReturn(bundle);
        schemaRoot = new ObjectMapper().readTree(Challenges.class.getResource("/challenges_schema.json"));
        List<MaterialJSON> materialJSONS = new ObjectMapper().readValue(FileManager.class.getResourceAsStream("/materials.json"), MaterialDataSource.class).getData();
        dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.materialJSONList()).thenReturn(materialJSONS);
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer("dummy");
        toBeBroken = player.getWorld().getBlockAt(0, 0, 0);
        ChallengeManager challengeManager = new ChallengeManager();
        context = new Context(plugin, resourceBundleContext, dataSourceContext, schemaRoot, challengeManager);
        challengeManager.setContext(context);
        rule = spy(new NoBlockBreakRule(context, new NoBlockBreakRuleConfig(List.of(Material.STONE.toString()), null)));

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
        verify(rule, times(1)).enforcePunishments(any(Player.class));
    }

    @Test
    public void testIsInExemptionsOnBlockBreakEvent() {
        toBeBroken.setType(Material.STONE);
        player.simulateBlockBreak(toBeBroken);
        //rule.onBlockBreak(new BlockBreakEvent(toBeBroken, player));
        //verify(rule, never()).enforcePunishments(any(Player.class));
    }

    @Test
    public void testIsNotInExemptionsOnBlockBreakEvent() {
        toBeBroken.setType(Material.DIRT);
        //rule.onBlockBreak(new BlockBreakEvent(toBeBroken, player));
        //verify(rule, times(1)).enforcePunishments(any(Player.class));
    }

    @Test
    public void testEmptyNoBlockBreakRuleJSON2Model() throws JsonProcessingException {
        String emptyNoBlockBreakJSON =
                """
                {
                  "rules": {
                    "enabledRules": {
                      "noBlockBreak": {
                      }
                    }
                  }
                }
                """;
        ChallengesSchema ChallengesSchema = new ObjectMapper().readValue(emptyNoBlockBreakJSON, ChallengesSchema.class);
        NoBlockBreakRuleConfig emptyNoBlockBreakRuleConfig = ChallengesSchema.getRules().getEnabledRules().getNoBlockBreak();

        assertTrue(emptyNoBlockBreakRuleConfig.getExemptions().isEmpty());

        NoBlockBreakRule emptyNoBlockBreakRule = new NoBlockBreakRule(context, emptyNoBlockBreakRuleConfig);
        ModelMapper.map2ModelClasses(context, ChallengesSchema);

        assertEquals(emptyNoBlockBreakRule, context.challengeManager().getRules().get(0));
    }

    @Test
    public void testComplexNoBlockBreakRuleJSON2Model() throws JsonProcessingException {
        String complexNoBlockBreakJSON =
                """
                {
                  "rules": {
                    "enabledRules": {
                      "noBlockBreak": {
                        "exemptions": ["STONE", "DIRT"]
                      }
                    }
                  }
                }
                """;
        ChallengesSchema ChallengesSchema = new ObjectMapper().readValue(complexNoBlockBreakJSON, ChallengesSchema.class);
        NoBlockBreakRuleConfig complexNoBlockBreakRuleConfig = ChallengesSchema.getRules().getEnabledRules().getNoBlockBreak();

        assertEquals(List.of("STONE", "DIRT"), complexNoBlockBreakRuleConfig.getExemptions());

        NoBlockBreakRule compelxNoBlockBreakRule = new NoBlockBreakRule(context, complexNoBlockBreakRuleConfig);
        ModelMapper.map2ModelClasses(context, ChallengesSchema);

        assertEquals(compelxNoBlockBreakRule, context.challengeManager().getRules().get(0));
    }

    @Test
    public void testInvalidExemptionsJSON2Model() {
        ObjectMapper objectMapper = new ObjectMapper();

        String additionalPropertyJSON =
                """
                {
                  "rules": {
                    "enabledRules": {
                      "noBlockBreak": {
                        "gibberish": "moreGibberish"
                      }
                    }
                  }
                }
                """;
        assertThrows(JsonMappingException.class, () -> objectMapper.readValue(additionalPropertyJSON, ChallengesSchema.class));

        String invalidStringInExemptionListJSON =
                """
                {
                  "rules": {
                    "enabledRules": {
                      "noBlockBreak": {
                        "exemptions": ["NOT_A_VALID_MATERIAL"]
                      }
                    }
                  }
                }
                """;
        assertThrows(RuntimeException.class, () -> {
            ChallengesSchema ChallengesSchema = objectMapper.readValue(invalidStringInExemptionListJSON, ChallengesSchema.class);
            ModelMapper.map2ModelClasses(context, ChallengesSchema);
        });

        String invalidMaterialInExemptionListJSON =
                """
                {
                  "rules": {
                    "enabledRules": {
                      "noBlockBreak": {
                        "exemptions": ["CARROT"]
                      }
                    }
                  }
                }
                """;
        // TODO: this should be caught in the validator, not AFTER the validator once it has reached the model.
        /*assertThrows(RuntimeException.class, () -> {
            ChallengesSchema ChallengesSchema = objectMapper.readValue(invalidMaterialInExemptionListJSON, ChallengesSchema.class);
            ModelMapper.map2ModelClasses(context, ChallengesSchema);
        });*/
    }

    @Test
    public void testNoBlockBreakRuleModel2JSON() {
        // When comparing NoBlockBreakRuleConfig, it internally compares the two exemption lists.
        // But #toGeneratedJSONClass converts from a set to a list, therefore any ordering is lost, hence it is always being sorted.
        List<String> exemptions = Stream.of("STONE", "DIRT").sorted().toList();
        NoBlockBreakRuleConfig config = new NoBlockBreakRuleConfig(exemptions, null);
        NoBlockBreakRule noBlockBreakRule = new NoBlockBreakRule(context, config);
        assertEquals(config, noBlockBreakRule.toGeneratedJSONClass());
    }
}
