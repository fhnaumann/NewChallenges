package wand555.github.io.challenges.punishments;

import org.bukkit.entity.Player;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.HealthPunishmentConfig;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.generated.RandomEffectPunishmentConfig;

public class RandomEffectPunishment extends Punishment implements Storable<RandomEffectPunishmentConfig> {

    private final int effectsAtOnce;
    private final boolean randomizeEffectsAtOnce;

    public RandomEffectPunishment(Affects affects, int effectsAtOnce, boolean randomizeEffectsAtOnce) {
        super(affects);
        this.effectsAtOnce = effectsAtOnce;
        this.randomizeEffectsAtOnce = randomizeEffectsAtOnce;
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
}
