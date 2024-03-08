package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.HealthPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.generated.RandomEffectPunishmentConfig;

public class RandomEffectPunishment extends Punishment implements Storable<RandomEffectPunishmentConfig> {

    private final int effectsAtOnce;
    private final boolean randomizeEffectsAtOnce;

    public RandomEffectPunishment(Context context, RandomEffectPunishmentConfig config) {
        super(context, map(config.getAffects()));
        this.effectsAtOnce = config.getEffectsAtOnce();
        this.randomizeEffectsAtOnce = config.isRandomizeEffectsAtOnce();
    }

    @Override
    public void enforcePunishment(Player causer) {

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
