package wand555.github.io.challenges.criteria.settings;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.generated.UltraHardcoreSettingConfig;
import wand555.github.io.challenges.types.death.DeathData;
import wand555.github.io.challenges.types.death.DeathMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Disabled("Inflicted potion effects from foods are not applied in the current version of MockBukkit (for 1.20.4). It is only supported from 1.20.6 onwards.")
public class UltraHardcoreSettingTest {

    private ServerMock server;
    private Challenges plugin;

    private PlayerMock player;

    private Context context;
    private ChallengeManager manager;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.getOrCreateMock();
        plugin = MockBukkit.load(Challenges.class);
        player = server.addPlayer();
        context = mock(Context.class);
        when(context.plugin()).thenReturn(plugin);
        manager = mock(ChallengeManager.class);
        when(manager.isRunning()).thenReturn(true);
        when(context.challengeManager()).thenReturn(manager);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testGameRuleNaturalRegDisabledAfterStartInListedWorlds() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, false, false);
        ultraHardcoreSetting.onStart();
        server.getWorlds().forEach(world -> assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.NATURAL_REGENERATION)));
    }

    @Test
    public void testGameRuleNaturalRegIsDefaultAfterStartInUnlistedWorlds() {
        World unlisted = server.addSimpleWorld("unlisted");
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, false, false);
        ultraHardcoreSetting.onStart();
        assertEquals(unlisted.getGameRuleDefault(GameRule.NATURAL_REGENERATION), unlisted.getGameRuleValue(GameRule.NATURAL_REGENERATION)); // may fail if the default value is not 'true' in a future update
    }

    @Test
    public void testPreventGameRuleNaturalRegChangesInListedWorlds() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, false, false);
        ultraHardcoreSetting.onStart();
        World world = server.getWorld("world");
        world.setGameRule(GameRule.NATURAL_REGENERATION, true);
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.NATURAL_REGENERATION));
    }

    @Test
    public void testAllowGameRuleNaturalRegChangesInUnlistedWorlds() {
        World unlisted = server.addSimpleWorld("unlisted");
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, false, false);
        ultraHardcoreSetting.onStart();
        unlisted.setGameRule(GameRule.NATURAL_REGENERATION, true);
        assertEquals(Boolean.TRUE, unlisted.getGameRuleValue(GameRule.NATURAL_REGENERATION));
    }

    @Test
    public void testGoldenAppleAmountDecreasedAfterConsumption() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, false, false);
        ItemStack consumingFrom = goldenApple(10);
        player.getEquipment().setItemInMainHand(consumingFrom);
        player.simulateConsumeItem(consumingFrom);
        assertEquals(9, player.getEquipment().getItemInMainHand().getAmount());
    }

    @Test
    public void testConsumeGoldenAppleHasNoEffectIfRegWithGoldenApplesFalse() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, false, false);
        ItemStack consumingFrom = goldenApple(10);
        player.simulateConsumeItem(consumingFrom);
        assertNull(player.getPotionEffect(PotionEffectType.REGENERATION));
    }

    @Test
    public void testConsumeGoldenAppleHasEffectIfRegWithGoldenApplesTrue() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, true, false, false, false, false, false);
        ItemStack consumingFrom = goldenApple(10);
        player.simulateConsumeItem(consumingFrom);
        assertNotNull(player.getPotionEffect(PotionEffectType.REGENERATION));
    }

    @Test
    public void testConsumeEnchantedGoldenAppleHasNoEffectIfRegWithGoldenApplesFalse() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, false, false);
        ItemStack consumingFrom = enchantedGoldenApple(10);
        player.simulateConsumeItem(consumingFrom);
        assertNull(player.getPotionEffect(PotionEffectType.REGENERATION));
        assertNull(player.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE));
        assertNull(player.getPotionEffect(PotionEffectType.FIRE_RESISTANCE));
        assertNull(player.getPotionEffect(PotionEffectType.ABSORPTION));
    }

    @Test
    public void testConsumeEnchantedGoldenAppleHasEffectIfRegWithGoldenApplesTrue() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, true, false, false, false, false);
        ItemStack consumingFrom = enchantedGoldenApple(10);
        player.simulateConsumeItem(consumingFrom);
        assertNotNull(player.getPotionEffect(PotionEffectType.REGENERATION));
        assertNotNull(player.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE));
        assertNotNull(player.getPotionEffect(PotionEffectType.FIRE_RESISTANCE));
        assertNotNull(player.getPotionEffect(PotionEffectType.ABSORPTION));
    }

    @Test
    public void testConsumeSuspiciousStewHasNoEffectIfRegWithSuspiciousStewFalse() {

    }

    @Test
    public void testConsumeSuspiciousStewHasEffectIfRegWithSuspiciousStewTrue() {

    }

    @Test
    public void testConsumePotionHasNoEffectIfRegWithPotionFalse() {

    }

    @Test
    public void testConsumePotionHasEffectIfRegWithPotionTrue() {

    }

    @Test
    public void testAbsorptionHeartsAreRemovedIfDisabled() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, false, false);
        ItemStack consumingFrom = enchantedGoldenApple(10);
        player.simulateConsumeItem(consumingFrom);
        EntityPotionEffectEvent event = new EntityPotionEffectEvent(player, null, PotionEffectType.ABSORPTION.createEffect(2, 1), EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.ADDED, false);
        server.getPluginManager().callEvent(event);
        assertTrue(event.isCancelled());
        assertNull(player.getPotionEffect(PotionEffectType.ABSORPTION));
    }

    @Test
    public void testAbsorptionHeartsAreKeptIfEnabled() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, true, false);
        ItemStack consumingFrom = enchantedGoldenApple(10);
        player.simulateConsumeItem(consumingFrom);
        EntityPotionEffectEvent event = new EntityPotionEffectEvent(player, null, PotionEffectType.ABSORPTION.createEffect(2, 1), EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.ADDED, false);
        server.getPluginManager().callEvent(event);
        assertFalse(event.isCancelled());
        assertNotNull(player.getPotionEffect(PotionEffectType.ABSORPTION));
    }

    @Test
    public void testDisallowTotemsIfDisabled() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, false, false);
        EntityResurrectEvent event = new EntityResurrectEvent(player, EquipmentSlot.HAND);
        server.getPluginManager().callEvent(event);
        assertTrue(event.isCancelled());
    }

    @Test
    public void testAllowTotemsIfEnabled() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, false, true);
        EntityResurrectEvent event = new EntityResurrectEvent(player, EquipmentSlot.HAND);
        server.getPluginManager().callEvent(event);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testAbsorptionHeartsAreKeptEvenIfRegWithGoldenAppleIsFalse() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, true, false);
        ItemStack consumingFrom = enchantedGoldenApple(10);
        player.simulateConsumeItem(consumingFrom);
        assertNull(player.getPotionEffect(PotionEffectType.REGENERATION));
        assertNotNull(player.getPotionEffect(PotionEffectType.ABSORPTION));
    }

    public static ItemStack goldenApple(int amount) {
        return new ItemStack(Material.GOLDEN_APPLE, amount);
    }
    public static ItemStack enchantedGoldenApple(int amount) {
        return new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, amount);
    }

    private static UltraHardcoreSetting create(Context context, boolean naturalRegeneration, boolean regWithGoldenApples, boolean regWithEnchantedGoldenApples, boolean regWithSuspiciousStew, boolean regWithPotions, boolean allowAbsorptionHearts, boolean allowTotems) {
        return new UltraHardcoreSetting(context, new UltraHardcoreSettingConfig(allowAbsorptionHearts, allowTotems, naturalRegeneration, regWithEnchantedGoldenApples, regWithGoldenApples, regWithPotions, regWithSuspiciousStew));
    }
}
