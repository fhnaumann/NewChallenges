package wand555.github.io.challenges.criteria.rules;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.rules.nodeath.NoDeathRule;
import wand555.github.io.challenges.criteria.rules.nodeath.NoDeathRuleMessageHelper;
import wand555.github.io.challenges.generated.NoDeathRuleConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.types.death.DeathData;
import wand555.github.io.challenges.types.death.DeathMessage;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NoDeathRuleTest {

    private static ResourceBundleContext resourceBundleContext;
    private static DataSourceContext dataSourceContext;

    private ServerMock server;
    private Challenges plugin;

    private NoDeathRule countTotemsRule;
    private NoDeathRule ignoreTotemRule;

    private PlayerMock player;

    private static Context context;
    private static NoDeathRuleMessageHelper messageHelper;


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

        messageHelper = spy(new NoDeathRuleMessageHelper(context));
    }

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        player = server.addPlayer("dummy");


        countTotemsRule = new NoDeathRule(context, new NoDeathRuleConfig(false, new PunishmentsConfig(), NoDeathRuleConfig.Result.DENY), messageHelper);
        ignoreTotemRule = new NoDeathRule(context, new NoDeathRuleConfig(false, new PunishmentsConfig(), NoDeathRuleConfig.Result.DENY), messageHelper);

    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @MethodSource("provideTotemUsages")
    public void testNoDeathRuleTriggerCheckWithTotems(Supplier<NoDeathRule> rule, Function<Player, DeathData> deathData, boolean expected) {
        assertEquals(expected, rule.get().triggerCheck().applies(deathData.apply(player)));
    }

    private static Stream<Arguments> provideTotemUsages() {
        return Stream.of(
                Arguments.of(noDeathRule(true), dummyDeathData(true), false),
                Arguments.of(noDeathRule(true), dummyDeathData(false), true),
                Arguments.of(noDeathRule(false), dummyDeathData(true), true),
                Arguments.of(noDeathRule(false), dummyDeathData(false), true)
        );
    }

    private static Supplier<NoDeathRule> noDeathRule(boolean ignoreTotems) {
        return () -> new NoDeathRule(context, new NoDeathRuleConfig(ignoreTotems, new PunishmentsConfig(), NoDeathRuleConfig.Result.DENY), messageHelper);
    }

    private static Function<Player, DeathData> dummyDeathData(boolean usedTotem) {
        return player1 -> new DeathData(player1, 1, mock(DeathMessage.class), usedTotem);
    }
}
