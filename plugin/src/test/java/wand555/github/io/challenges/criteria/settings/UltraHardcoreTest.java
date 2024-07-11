package wand555.github.io.challenges.criteria.settings;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.ChallengeManager;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.generated.UltraHardcoreSettingConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UltraHardcoreTest {

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

    }

    @Test
    public void testPreventGameRuleNaturalRegChangesInListedWorlds() {
        UltraHardcoreSetting ultraHardcoreSetting = create(context, false, false, false, false, false, false, false);
        ultraHardcoreSetting.onStart();
        World world = server.getWorld("world");
        world.setGameRuleValue(GameRule.NATURAL_REGENERATION.getName(), "true");
        assertEquals(Boolean.FALSE, world.getGameRuleValue(GameRule.NATURAL_REGENERATION));
    }

    @Test
    public void testAllowGameRuleNaturalRegChangesInUnlistedWorlds() {

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

    public static ItemStack goldenApple(int amount) {
        return new ItemStack(Material.GOLDEN_APPLE, amount);
    }

    private static UltraHardcoreSetting create(Context context, boolean naturalRegeneration, boolean regWithGoldenApples, boolean regWithEnchantedGoldenApples, boolean regWithSuspiciousStew, boolean regWithPotions, boolean allowAbsorptionHearts, boolean allowTotems) {
        return new UltraHardcoreSetting(context, new UltraHardcoreSettingConfig(allowAbsorptionHearts, allowTotems, naturalRegeneration, regWithEnchantedGoldenApples, regWithGoldenApples, regWithPotions, regWithSuspiciousStew));
    }
}
