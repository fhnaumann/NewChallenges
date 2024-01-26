package wand555.github.io.challenges.rules;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.command.ConsoleCommandSenderMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.generated.NoBlockBreakRuleConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.mapping.Mapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class NoBlockBreakRuleTest {

    private ServerMock server;
    private Challenges plugin;

    private NoBlockBreakRule rule;

    private PlayerMock player;
    private Block toBeBroken;

    private ResourceBundle bundle;

    private Mapper mapper;

    @BeforeEach
    public void setUp() throws IOException {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Challenges.class);
        Set<Material> exemptions = Set.of(
                Material.STONE
        );
        player = server.addPlayer("dummy");
        toBeBroken = player.getWorld().getBlockAt(0, 0, 0);
        bundle = ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get());
        rule = spy(new NoBlockBreakRule(plugin, bundle, exemptions));
        JsonNode schemaRoot = new ObjectMapper().readTree(Challenges.class.getResource("/test-output-schema.json"));
        mapper = new Mapper(plugin, bundle, schemaRoot);
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
        Component oneLine = MiniMessage.miniMessage().deserialize(
                "<lang:block.minecraft.dirt> was broken"
        );
        //Component expected = MiniMessage.miniMessage().deserialize();
        //player.assertSaid(MiniMessage.miniMessage().serialize(oneLine));
        //player.assertSaid("abc");
        Component nested = MiniMessage.miniMessage().deserialize("<dirt> was broken", Placeholder.component("dirt", Component.translatable("block.minecraft.dirt")));
        //Component nested = Component.translatable("block.minecraft.dirt").append(Component.text(" was broken"));
        Component oneLine2 = MiniMessage.miniMessage().deserialize(
                "<lang:block.minecraft.dirt> was broken"
        );
        //Assertions.assertEquals(MiniMessage.miniMessage().serialize(nested), MiniMessage.miniMessage().serialize(oneLine2));
    }

    @Test
    public void testEmptyNoBlockBreakRuleJSON2Model() throws JsonProcessingException {
        String emptyNoBlockBreak =
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
        NoBlockBreakRule noBlockBreakRule = new NoBlockBreakRule(plugin, bundle, Set.of());
        assertDoesNotThrow(() -> mapper.mapToGeneratedClasses(emptyNoBlockBreak));
        ChallengeManager challengeManager = mapper.mapToGeneratedClasses(emptyNoBlockBreak);
        assertEquals(1, challengeManager.getRules().size());
        assertEquals(noBlockBreakRule, challengeManager.getRules().get(0));
    }

    @Test
    public void testComplexNoBlockBreakRuleJSON2Model() throws JsonProcessingException {
        String complexNoBlockBreak =
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
        NoBlockBreakRule noBlockBreakRule = new NoBlockBreakRule(plugin, bundle, Set.of(Material.STONE, Material.DIRT));
        assertDoesNotThrow(() -> mapper.mapToGeneratedClasses(complexNoBlockBreak));
        ChallengeManager challengeManager = mapper.mapToGeneratedClasses(complexNoBlockBreak);
        assertEquals(1, challengeManager.getRules().size());
        assertEquals(noBlockBreakRule, challengeManager.getRules().get(0));
    }

    @Test
    public void testInvalidExemptionsJSON2Model() {
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
        assertThrows(JsonMappingException.class, () -> mapper.mapToGeneratedClasses(additionalPropertyJSON));

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
        assertThrows(RuntimeException.class, () -> mapper.mapToGeneratedClasses(invalidStringInExemptionListJSON));

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
        assertThrows(RuntimeException.class, () -> mapper.mapToGeneratedClasses(invalidMaterialInExemptionListJSON));
    }

    @Test
    public void testNoBlockBreakRuleModel2JSON() {
        Set<Material> exemptions = Set.of(Material.STONE, Material.DIRT);
        NoBlockBreakRule noBlockBreakRule = new NoBlockBreakRule(plugin, bundle, exemptions);
        // funky behaviour:
        // When comparing NoBlockBreakRuleConfig, it internally compares the two exemption lists.
        // But #toGeneratedJSONClass converts from a set to a list, therefore any ordering is lost, hence it is always being sorted.
        assertEquals(new NoBlockBreakRuleConfig(Stream.of("STONE", "DIRT").sorted().toList(), new PunishmentsConfig()), noBlockBreakRule.toGeneratedJSONClass());
    }
}
