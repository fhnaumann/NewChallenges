package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.generated.RandomEffectPunishmentConfig;
import wand555.github.io.challenges.mapping.NullHelper;
import wand555.github.io.challenges.utils.CollectionUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RandomEffectPunishment extends Punishment implements Storable<RandomEffectPunishmentConfig> {

    private final int effectsAtOnce;
    private final boolean randomizeEffectsAtOnce;

    private final int minimumEffectsAtOnce, maximumEffectsAtOnce;

    public RandomEffectPunishment(Context context, RandomEffectPunishmentConfig config) {
        super(context, map(config.getAffects()));
        this.effectsAtOnce = config.getEffectsAtOnce();
        this.randomizeEffectsAtOnce = config.isRandomizeEffectsAtOnce();
        this.minimumEffectsAtOnce = NullHelper.minValue(context.schemaRoot(),
                                                        "RandomEffectPunishmentConfig",
                                                        "effectsAtOnce"
        );
        this.maximumEffectsAtOnce = NullHelper.maxValue(context.schemaRoot(),
                                                        "RandomEffectPunishmentConfig",
                                                        "effectsAtOnce"
        );
    }

    @Override
    public void enforceCauserPunishment(Player causer) {
        List<PotionEffect> calculatedEffectsAtOnce = getCalculatedEffectsAtOnce();
        enforceOnReceiver(causer, calculatedEffectsAtOnce);
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().punishmentResourceBundle(),
                "randomeffect.enforced.causer",
                Map.of("player", Component.text(causer.getName()),
                       "amount", Component.text(Integer.toString(calculatedEffectsAtOnce.size()))
                )
        );
        context.plugin().getServer().broadcast(toSend);
    }

    @Override
    public void enforceAllPunishment() {
        List<PotionEffect> calculatedEffectsAtOnce = getCalculatedEffectsAtOnce();
        Bukkit.getOnlinePlayers().forEach(player -> InteractionManager.applyInteraction(player,
                                                                                        samePlayer -> enforceOnReceiver(
                                                                                                samePlayer,
                                                                                                calculatedEffectsAtOnce
                                                                                        )
        ));
        Component toSend = ComponentUtil.formatChatMessage(
                context.plugin(),
                context.resourceBundleContext().punishmentResourceBundle(),
                "randomeffect.enforced.all",
                Map.of("amount", Component.text(Integer.toString(calculatedEffectsAtOnce.size())))
        );
        context.plugin().getServer().broadcast(toSend);
    }

    private void enforceOnReceiver(Player receiver, List<PotionEffect> effects) {
        effects.forEach(receiver::addPotionEffect);
    }

    private List<PotionEffect> getCalculatedEffectsAtOnce() {
        int amount;
        if(!randomizeEffectsAtOnce) {
            amount = effectsAtOnce;
        } else {
            amount = context.random().nextInt(minimumEffectsAtOnce, maximumEffectsAtOnce);
        }
        return createNEffects(amount);
    }

    private List<PotionEffect> createNEffects(int n) {
        List<PotionEffectType> toApply = CollectionUtil.pickN(Arrays.asList(PotionEffectType.values()),
                                                              n,
                                                              context.random()
        );
        return toApply.stream().map(this::potionEffect).toList();
    }

    private PotionEffect potionEffect(PotionEffectType type) {
        return type.createEffect(
                context.random().nextInt(10, 60 * 10 + 1) * 20, // between 10 seconds and 10 minutes
                context.random().nextInt(6)
        );
    }

    public void addToGeneratedConfig(PunishmentsConfig generatedPunishmentsConfig) {
        generatedPunishmentsConfig.setRandomEffectPunishment(toGeneratedJSONClass());
    }

    @Override
    public RandomEffectPunishmentConfig toGeneratedJSONClass() {
        return new RandomEffectPunishmentConfig(
                RandomEffectPunishmentConfig.Affects.fromValue(getAffects().getValue()),
                effectsAtOnce,
                randomizeEffectsAtOnce
        );
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }
}
