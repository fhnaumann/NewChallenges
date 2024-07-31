package wand555.github.io.challenges.criteria.settings;

import io.papermc.paper.event.world.WorldGameRuleChangeEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.criteria.Triggable;
import wand555.github.io.challenges.generated.SettingsConfig;
import wand555.github.io.challenges.generated.UltraHardcoreSettingConfig;
import wand555.github.io.challenges.types.death.DeathData;
import wand555.github.io.challenges.types.death.DeathType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class UltraHardcoreSetting extends BaseSetting implements Storable<UltraHardcoreSettingConfig>, Listener {

    private static final Logger logger = ChallengesDebugLogger.getLogger(UltraHardcoreSetting.class);

    private final UltraHardcoreSettingConfig config;
    private final List<World> worldsInConfig;

    public UltraHardcoreSetting(Context context, UltraHardcoreSettingConfig config) {
        super(context);
        this.config = config;
        this.worldsInConfig = context.plugin().getConfig().getStringList("worlds").stream().map(s -> {
                                         World world = Bukkit.getWorld(s);
                                         if(world == null) {
                                             logger.warning("World '%s' does not match any existing world in %s. It will be ignored.".formatted(s,
                                                                                                                                                Bukkit.getWorlds()
                                             ));
                                         }
                                         return world;
                                     })
                                     .filter(Objects::nonNull)
                                     .toList();
        context.plugin().getServer().getPluginManager().registerEvents(this, context.plugin());
    }


    @Override
    public void onStart() {
        worldsInConfig.forEach(world -> world.setGameRule(GameRule.NATURAL_REGENERATION,
                                                          config.isNaturalRegeneration()
        ));
    }

    @Override
    public void onEnd() {

    }

    @EventHandler
    public void onPlayerRegEvent(EntityRegainHealthEvent event) {
        // Should not be called because the gamerule NATURAL_REGENERATION is disabled
        logger.fine("%s triggered.".formatted(event.getEventName()));
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }
        if(!context.challengeManager().isRunning()) {
            return;
        }

        logger.info("reason %s".formatted(event.getRegainReason()));
        logger.fine("Initial check for %s passes.".formatted(event.getEventName()));
        if(event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            logger.fine("Canceling %s for %s.".formatted(event.getEventName(), player));
            event.setCancelled(true);
            player.setSaturation(player.getSaturation() + 1.5f); // eh, approximation is good enough
        } else if(event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC) {
            // regeneration effect could be from any source (potion effects, golden apples or totems)+
        }

    }

    @EventHandler
    public void onPlayerConsumeItemEvent(PlayerItemConsumeEvent event) {
        // handles golden apples, enchanted golden apples, suspicious stews, instant health potions and regeneration potions
        if(!context.challengeManager().isRunning()) {
            return;
        }

        ItemStack item = event.getItem();
        boolean cancelAndDecreaseFood = shouldBlockFood(item);
        if(cancelAndDecreaseFood) {
            event.setCancelled(true);
            getItemInRelevantHand(event.getPlayer(), event.getHand()).setAmount(item.getAmount() - 1);
            logger.fine("Cancelled and decreased %s for %s".formatted(item.getType(), event.getPlayer().getName()));
            addAbsorptionHeartsIfNecessary(event.getPlayer(), item.getType());
        }
        boolean cancelAndClearPotion = shouldBlockPotion(item);
        if(cancelAndClearPotion) {
            event.setCancelled(true);
            getItemInRelevantHand(event.getPlayer(), event.getHand()).setType(Material.GLASS_BOTTLE);
            logger.fine("Cancelled and replaced %s with empty glass bottle for %s".formatted(item.getType(),
                                                                                             event.getPlayer().getName()
            ));
        }
    }

    private void addAbsorptionHeartsIfNecessary(Player player, Material material) {
        // https://minecraft.wiki/w/Absorption
        if(material == Material.GOLDEN_APPLE) {
            player.addPotionEffect(PotionEffectType.ABSORPTION.createEffect(60 * 2 * 20, 0)); // 4 hearts for 2 minutes
        }
        if(material == Material.ENCHANTED_GOLDEN_APPLE) {
            player.addPotionEffect(PotionEffectType.ABSORPTION.createEffect(60 * 2 * 20, 3)); // 8 hearts for 2 minutes
        }
    }

    private ItemStack getItemInRelevantHand(Player player, EquipmentSlot hand) {
        return switch(hand) {
            case HAND -> player.getEquipment().getItemInMainHand();
            case OFF_HAND -> player.getEquipment().getItemInOffHand();
            default -> throw new RuntimeException("Not a hand");
        };
    }

    private boolean shouldBlockFood(ItemStack item) {
        Material itemType = item.getType();
        // manually prevent the player from consuming the item and then immediately decrease
        // the amount by one to simulate the consumption but with no effect
        return (itemType == Material.GOLDEN_APPLE && !config.isRegWithGoldenApples())
                || (itemType == Material.ENCHANTED_GOLDEN_APPLE && !config.isRegWithEnchantedGoldenApples())
                || (itemType == Material.SUSPICIOUS_STEW && !config.isRegWithSuspiciousStew()
        );
    }

    private boolean shouldBlockPotion(ItemStack item) {
        return isInstantHealthOrRegenerationPotion(item) && !config.isRegWithPotions();
    }

    private boolean isInstantHealthOrRegenerationPotion(ItemStack itemStack) {
        if(!itemStack.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if(!(meta instanceof PotionMeta potionMeta)) {
            return false;
        }
        return potionMeta.getBasePotionType().getPotionEffects().stream()
                         .anyMatch(this::isInstantHealthOrRegenerationPotion);
    }

    private boolean isInstantHealthOrRegenerationPotion(PotionEffect potionEffect) {
        return potionEffect.getType() == PotionEffectType.REGENERATION || potionEffect.getType() == PotionEffectType.HEAL;
    }

    @EventHandler
    public void onPlayerUsePotionEvent(EntityPotionEffectEvent event) {
        logger.info(event.getCause().toString());
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }
        if(event.getAction() == EntityPotionEffectEvent.Action.CLEARED || event.getAction() == EntityPotionEffectEvent.Action.REMOVED) {
            return;
        }
        if(!context.challengeManager().isRunning()) {
            return;
        }
        if(event.getNewEffect() == null) {
            return;
        }
        if(!config.isRegWithPotions() && isInstantHealthOrRegenerationPotion(event.getNewEffect())
                || !config.isAllowAbsorptionHearts() && event.getNewEffect().getType() == PotionEffectType.ABSORPTION) {
            // prevent status effects
            event.setCancelled(true);
            logger.fine("Removing %s from %s.".formatted(event.getModifiedType(), player));
        }
    }

    @EventHandler
    public void onPlayerResurrectEvent(EntityResurrectEvent event) {
        logger.info("Received resurrection from UltraHardcoreSetting.");
        if(!(event.getEntity() instanceof Player player)) {
            return;
        }
        boolean poppedTotem = !event.isCancelled();
        if(poppedTotem && !config.isAllowTotems()) {
            // cancel the resurrection
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeathGameRuleChange(WorldGameRuleChangeEvent event) {
        if(event.getGameRule() == GameRule.NATURAL_REGENERATION && Boolean.parseBoolean(event.getValue()) && worldsInConfig.contains(
                event.getWorld())) {
            // user tries to set the natural regeneration gamerule manually to true, we don't allow this
            event.setCancelled(true);
            if(event.getCommandSender() != null) {
                Component warnMessage = ComponentUtil.formatChatMessage(
                        context.plugin(),
                        context.resourceBundleContext().settingsResourceBundle(),
                        "ultraHardcore.manual_gamerule",
                        true
                );
                event.getCommandSender().sendMessage(warnMessage);
            }
        }
    }

    @Override
    public void addToGeneratedConfig(SettingsConfig config) {
        config.setUltraHardcoreSetting(toGeneratedJSONClass());
    }

    @Override
    public UltraHardcoreSettingConfig toGeneratedJSONClass() {
        return config; // return same instance
    }
}
