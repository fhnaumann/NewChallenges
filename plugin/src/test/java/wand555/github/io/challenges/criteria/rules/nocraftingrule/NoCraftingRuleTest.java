package wand555.github.io.challenges.criteria.rules.nocraftingrule;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.CriteriaUtil;
import wand555.github.io.challenges.criteria.rules.nocrafting.NoCraftingRule;
import wand555.github.io.challenges.criteria.rules.nocrafting.NoCraftingRuleMessageHelper;
import wand555.github.io.challenges.generated.CancelPunishmentConfig;
import wand555.github.io.challenges.generated.NoCraftingRuleConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.mapping.CraftingTypeJSON;
import wand555.github.io.challenges.punishments.CancelPunishment;
import wand555.github.io.challenges.types.crafting.CraftingData;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NoCraftingRuleTest {
    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private static Context context;
    private static NoCraftingRuleMessageHelper messageHelper;

    private static Event emptyMockEvent;


    @BeforeAll
    public static void setUpIOData() throws IOException {
        ResourceBundleContext resourceBundleContext = mock(ResourceBundleContext.class);
        when(resourceBundleContext.ruleResourceBundle()).thenReturn(CriteriaUtil.loadRuleResourceBundle());
        DataSourceContext dataSourceContext = mock(DataSourceContext.class);
        when(dataSourceContext.craftingTypeJSONList()).thenReturn(CriteriaUtil.loadCraftingTypes().getData());
        ChallengeManager manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);
        when(manager.canTakeEffect(any(), any())).thenReturn(true);

        context = mock(Context.class);
        when(context.dataSourceContext()).thenReturn(dataSourceContext);
        when(context.resourceBundleContext()).thenReturn(resourceBundleContext);
        when(context.challengeManager()).thenReturn(manager);

        messageHelper = spy(new NoCraftingRuleMessageHelper(context));

        emptyMockEvent = mock(Event.class);
    }

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        when(context.plugin()).thenReturn(plugin);
        player = server.addPlayer("dummy");


    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @ParameterizedTest
    @MethodSource("provideNoCraftingTriggerChecks")
    public void testNoCraftingTriggerCheck(boolean expected, CraftingTypeJSON craftingTypeJSON, boolean internallyCrafted, NoCraftingRuleConfig config) {
        NoCraftingRule rule = new NoCraftingRule(context, config, messageHelper);
        CraftingData<?> craftingData = new CraftingData<>(emptyMockEvent, player, craftingTypeJSON, internallyCrafted);
        assertEquals(expected, rule.triggerCheck().applies(craftingData));
    }

    public static Stream<Arguments> provideNoCraftingTriggerChecks() {
        return Stream.of(
                Arguments.of(true, stickCraftingCraftingTypeJSON(), true, noCraftingRuleInternalCrafting(false)),
                Arguments.of(false, stickCraftingCraftingTypeJSON(), true, noCraftingRuleInternalCrafting(true)),
                Arguments.of(true, charcoalFurnaceCraftingTypeJSON(), false, noCraftingRuleFurnaceSmelting(false)),
                Arguments.of(false, charcoalFurnaceCraftingTypeJSON(), false, noCraftingRuleFurnaceSmelting(true)),
                Arguments.of(true, stickCraftingCraftingTypeJSON(), false, noCraftingRuleWorkbenchCrafting(false)),
                Arguments.of(false, stickCraftingCraftingTypeJSON(), false, noCraftingRuleWorkbenchCrafting(true)),
                Arguments.of(true, cooked_salmon_from_campfire_cookingFurnaceCraftingTypeJSON(), false, noCraftingRuleCampfireCooking(false)),
                Arguments.of(false, cooked_salmon_from_campfire_cookingFurnaceCraftingTypeJSON(), false, noCraftingRuleCampfireCooking(true)),
                Arguments.of(true, netherite_shovel_smithingSmithingCraftingTypeJSON(), false, noCraftingRuleSmithing(false)),
                Arguments.of(false, netherite_shovel_smithingSmithingCraftingTypeJSON(), false, noCraftingRuleSmithing(true)),
                Arguments.of(true, diorite_wall_from_diorite_stonecuttingStonecuttingCraftingTypeJSON(), false, noCraftingRuleStonecutter(false)),
                Arguments.of(false, diorite_wall_from_diorite_stonecuttingStonecuttingCraftingTypeJSON(), false, noCraftingRuleStonecutter(true))
        );
    }

    @Test
    public void testIsInExemptions() {
        NoCraftingRule rule = new NoCraftingRule(context, noCraftingRule(List.of(stickCraftingCraftingTypeJSON().getCode()), true, false, true, true, true, true), messageHelper);
        CraftingData<?> craftingData = new CraftingData<>(emptyMockEvent, player, stickCraftingCraftingTypeJSON(), false);
        assertFalse(rule.triggerCheck().applies(craftingData));
    }

    @Test
    public void testIsNotInExemptions() {
        NoCraftingRule rule = new NoCraftingRule(context, noCraftingRule(List.of(stickCraftingCraftingTypeJSON().getCode()), true, true, false, true, true, true), messageHelper);
        CraftingData<?> craftingData = new CraftingData<>(emptyMockEvent, player, charcoalFurnaceCraftingTypeJSON(), false);
        assertTrue(rule.triggerCheck().applies(craftingData));
    }

    @ParameterizedTest
    @MethodSource("provideExemptionsAlwaysOverruleOtherSettings")
    public void testExemptionsAlwaysOverruleOtherSetting(CraftingTypeJSON craftingTypeJSON, boolean internallyCrafted, NoCraftingRuleConfig config) {
        config.setExemptions(List.of(craftingTypeJSON.getCode()));
        NoCraftingRule rule = new NoCraftingRule(context, config, messageHelper);
        CraftingData<?> craftingData = new CraftingData<>(emptyMockEvent, player, craftingTypeJSON, internallyCrafted);
        assertFalse(rule.triggerCheck().applies(craftingData));
    }

    @Test
    public void testViolationMessageSentIfViolated() {
        NoCraftingRule rule = new NoCraftingRule(context, noCraftingRuleWorkbenchCrafting(false), messageHelper);
        CraftingData<?> craftingData = new CraftingData<>(emptyMockEvent, player, stickCraftingCraftingTypeJSON(), false);
        rule.trigger().actOnTriggered(craftingData);
        verify(messageHelper).sendViolationAction(craftingData);
    }

    @Test
    public void testIsAllowedIfNoCancelPunishment() {
        NoCraftingRule rule = new NoCraftingRule(context, noCraftingRuleInternalCrafting(true), messageHelper);
        CraftingRecipe stickCraftingRecipe = new ShapelessRecipe(stickCraftingCraftingTypeJSON().getKey(), new ItemStack(stickCraftingCraftingTypeJSON().getCraftingResult(), 4));
        player.openInventory(player.getInventory());
        CraftItemEvent event = new CraftItemEvent(stickCraftingRecipe, player.getOpenInventory(), InventoryType.SlotType.RESULT, 0, ClickType.LEFT, InventoryAction.UNKNOWN);
        CriteriaUtil.callEvent(server, event, 1);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testIsDisallowedIfCancelPunishment() {
        NoCraftingRule rule = new NoCraftingRule(context, noCraftingRuleInternalCrafting(false), messageHelper); // mockbukkit will switch it to workbench crafting
        rule.setPunishments(List.of(new CancelPunishment(context, new CancelPunishmentConfig())));
        CraftingRecipe stickCraftingRecipe = new ShapelessRecipe(new NamespacedKey("challenges", "stick"), new ItemStack(stickCraftingCraftingTypeJSON().getCraftingResult(), 4));
        player.openInventory(player.getInventory());
        player.getOpenInventory().setItem(0, new ItemStack(Material.STICK, 4));
        CraftItemEvent event = new CraftItemEvent(stickCraftingRecipe, player.getOpenInventory(), InventoryType.SlotType.RESULT, 0, ClickType.LEFT, InventoryAction.UNKNOWN);
        CriteriaUtil.callEvent(server, event, 1);
        assertTrue(event.isCancelled());
    }

    public static Stream<Arguments> provideExemptionsAlwaysOverruleOtherSettings() {
        return Stream.of(
                Arguments.of(stickCraftingCraftingTypeJSON(), true, noCraftingRuleInternalCrafting(false)),
                Arguments.of(charcoalFurnaceCraftingTypeJSON(), false, noCraftingRuleFurnaceSmelting(false)),
                Arguments.of(stickCraftingCraftingTypeJSON(), false, noCraftingRuleWorkbenchCrafting(false)),
                Arguments.of(cooked_salmon_from_campfire_cookingFurnaceCraftingTypeJSON(), false, noCraftingRuleCampfireCooking(false)),
                Arguments.of( netherite_shovel_smithingSmithingCraftingTypeJSON(), false, noCraftingRuleSmithing(false)),
                Arguments.of(diorite_wall_from_diorite_stonecuttingStonecuttingCraftingTypeJSON(), false, noCraftingRuleStonecutter(false))
        );
    }

    private static NoCraftingRuleConfig noCraftingRuleInternalCrafting(boolean internalCrafting) {
        return new NoCraftingRuleConfig(false, List.of(), false, internalCrafting, new PunishmentsConfig(), false, false, false);
    }

    private static NoCraftingRuleConfig noCraftingRuleWorkbenchCrafting(boolean workBenchCrafting) {
        return new NoCraftingRuleConfig(false, List.of(), false, false, new PunishmentsConfig(), false, false, workBenchCrafting);
    }

    private static NoCraftingRuleConfig noCraftingRuleCampfireCooking(boolean campfireCooking) {
        return new NoCraftingRuleConfig(campfireCooking, List.of(), false, false, new PunishmentsConfig(), false, false, false);
    }

    private static NoCraftingRuleConfig noCraftingRuleFurnaceSmelting(boolean furnaceSmelting) {
        return new NoCraftingRuleConfig(false, List.of(), furnaceSmelting, false, new PunishmentsConfig(), false, false, false);
    }

    private static NoCraftingRuleConfig noCraftingRuleSmithing(boolean smithing) {
        return new NoCraftingRuleConfig(false, List.of(), false, false, new PunishmentsConfig(), smithing, false, false);
    }

    private static NoCraftingRuleConfig noCraftingRuleStonecutter(boolean stoneCutter) {
        return new NoCraftingRuleConfig(false, List.of(), false, false, new PunishmentsConfig(), false, stoneCutter, false);
    }



    private static NoCraftingRuleConfig noCraftingRule(boolean internalCrafting, boolean workBenchCrafting, boolean furnaceSmelting, boolean campfireCooking, boolean smithing, boolean stonecutter) {
        return new NoCraftingRuleConfig(campfireCooking, List.of(), furnaceSmelting, internalCrafting, new PunishmentsConfig(), smithing, stonecutter, workBenchCrafting);
    }

    private static NoCraftingRuleConfig noCraftingRule(List<String> exemptions, boolean internalCrafting, boolean workBenchCrafting, boolean furnaceSmelting, boolean campfireCooking, boolean smithing, boolean stonecutter) {
        return new NoCraftingRuleConfig(campfireCooking, exemptions, furnaceSmelting, internalCrafting, new PunishmentsConfig(), smithing, stonecutter, workBenchCrafting);
    }

    private static CraftingTypeJSON stickCraftingCraftingTypeJSON() {
        return new CraftingTypeJSON(null, "challenges-stick", "stick", "crafting");
    }

    private static CraftingTypeJSON  charcoalFurnaceCraftingTypeJSON() {
        return new CraftingTypeJSON("dark_oak_log","challenges-charcoal", "charcoal", "furnace");
    }

    private static CraftingTypeJSON  cooked_salmon_from_campfire_cookingFurnaceCraftingTypeJSON() {
        return new CraftingTypeJSON("salmon", "challenges-cooked_salmon_from_campfire_cooking", "cooked_salmon", "campfire");
    }

    private static CraftingTypeJSON netherite_shovel_smithingSmithingCraftingTypeJSON() {
        return new CraftingTypeJSON(null, "challenges-netherite_shovel_smithing", "netherite_shovel", "smithing");
    }

    private static CraftingTypeJSON diorite_wall_from_diorite_stonecuttingStonecuttingCraftingTypeJSON() {
        return new CraftingTypeJSON(null, "challenges-diorite_wall_from_diorite_stonecutting", "diorite_wall", "stonecutting");
    }
}
