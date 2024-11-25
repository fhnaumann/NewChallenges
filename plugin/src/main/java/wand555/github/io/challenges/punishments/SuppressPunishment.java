package wand555.github.io.challenges.punishments;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.generated.SuppressPunishmentConfig;
import wand555.github.io.challenges.teams.Team;
import wand555.github.io.challenges.types.Data;
import wand555.github.io.challenges.types.blockbreak.BlockBreakData;
import wand555.github.io.challenges.types.blockplace.BlockPlaceData;
import wand555.github.io.challenges.types.crafting.CraftingData;
import wand555.github.io.challenges.types.mob.MobData;

public class SuppressPunishment extends Punishment implements Storable<SuppressPunishmentConfig> {
    public SuppressPunishment(Context context, SuppressPunishmentConfig config) {
        super(context, Affects.CAUSER);
    }

    @Override
    public void addToGeneratedConfig(PunishmentsConfig config) {
        config.setSuppressPunishment(toGeneratedJSONClass());
    }

    @Override
    public Component getCurrentStatus() {
        return null;
    }

    @Override
    public SuppressPunishmentConfig toGeneratedJSONClass() {
        return new SuppressPunishmentConfig();
    }

    @Override
    public <E extends Event, K> void enforceCauserPunishment(Data<E, K> data) {
        handlePunishment(data);
    }

    @Override
    public <E extends Event, K> void enforceAllPunishment(Data<E, K> data, Team team) {
        handlePunishment(data);
    }

    private <E extends Event, K> void handlePunishment(Data<E, K> data) {
        if(data instanceof BlockBreakData blockBreakData) {
            blockBreakData.event().setDropItems(false);
            blockBreakData.event().setExpToDrop(0);
        }
        else if(data instanceof MobData mobData) {
            mobData.event().setDroppedExp(0);
            mobData.event().getDrops().clear();
        }
    }
}
