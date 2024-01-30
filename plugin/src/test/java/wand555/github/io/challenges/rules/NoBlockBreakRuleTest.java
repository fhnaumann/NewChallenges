package wand555.github.io.challenges.rules;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.ResourceBundleContext;
import wand555.github.io.challenges.generated.NoBlockBreakRuleConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.generated.TestOutputSchema;
import wand555.github.io.challenges.mapping.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class NoBlockBreakRuleTest {

    private static ResourceBundleContext resourceBundleContext;
    private static JsonNode schemaRoot;

    private ServerMock server;
    private Challenges plugin;

    private NoBlockBreakRule rule;

    private PlayerMock player;
    private Block toBeBroken;

    private Context context;



    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get());
        resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.ruleResourceBundle()).thenReturn(bundle);
        schemaRoot = new ObjectMapper().readTree(Challenges.class.getResource("/test-output-schema.json"));
    }

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer("dummy");
        toBeBroken = player.getWorld().getBlockAt(0, 0, 0);
        context = new Context(plugin, resourceBundleContext, schemaRoot, new ChallengeManager());
        rule = spy(new NoBlockBreakRule(context, new NoBlockBreakRuleConfig(List.of(Material.STONE.toString()), null)));

    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testIsInExemptionsOnBlockBreakEvent() {
        toBeBroken.setType(Material.STONE);
        rule.onBlockBreak(new BlockBreakEvent(toBeBroken, player));
        verify(rule, never()).enforcePunishments(any(Player.class));
    }

    @Test
    public void testIsNotInExemptionsOnBlockBreakEvent() {
        toBeBroken.setType(Material.DIRT);
        rule.onBlockBreak(new BlockBreakEvent(toBeBroken, player));
        verify(rule, times(1)).enforcePunishments(any(Player.class));
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
        TestOutputSchema testOutputSchema = new ObjectMapper().readValue(emptyNoBlockBreakJSON, TestOutputSchema.class);
        NoBlockBreakRuleConfig emptyNoBlockBreakRuleConfig = testOutputSchema.getRules().getEnabledRules().getNoBlockBreak();

        assertTrue(emptyNoBlockBreakRuleConfig.getExemptions().isEmpty());

        NoBlockBreakRule emptyNoBlockBreakRule = new NoBlockBreakRule(context, emptyNoBlockBreakRuleConfig);
        ModelMapper.map2ModelClasses(context, testOutputSchema);

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
        TestOutputSchema testOutputSchema = new ObjectMapper().readValue(complexNoBlockBreakJSON, TestOutputSchema.class);
        NoBlockBreakRuleConfig complexNoBlockBreakRuleConfig = testOutputSchema.getRules().getEnabledRules().getNoBlockBreak();

        assertEquals(List.of("STONE", "DIRT"), complexNoBlockBreakRuleConfig.getExemptions());

        NoBlockBreakRule compelxNoBlockBreakRule = new NoBlockBreakRule(context, complexNoBlockBreakRuleConfig);
        ModelMapper.map2ModelClasses(context, testOutputSchema);

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
        assertThrows(JsonMappingException.class, () -> objectMapper.readValue(additionalPropertyJSON, TestOutputSchema.class));

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
            TestOutputSchema testOutputSchema = objectMapper.readValue(invalidStringInExemptionListJSON, TestOutputSchema.class);
            ModelMapper.map2ModelClasses(context, testOutputSchema);
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
        assertThrows(RuntimeException.class, () -> {
            TestOutputSchema testOutputSchema = objectMapper.readValue(invalidMaterialInExemptionListJSON, TestOutputSchema.class);
            ModelMapper.map2ModelClasses(context, testOutputSchema);
        });
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
