package wand555.github.io.challenges.punishments;

import org.bukkit.event.Event;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.JSONConfigGroup;
import wand555.github.io.challenges.StatusInfo;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.teams.Team;
import wand555.github.io.challenges.types.Data;

import java.util.Objects;
import java.util.UUID;

public abstract class Punishment implements JSONConfigGroup<PunishmentsConfig>, StatusInfo {

    protected final Context context;
    private final Affects affects;

    public Punishment(Context context, Affects affects) {
        this.context = context;
        this.affects = affects;
    }

    public <E extends Event, K> void enforcePunishment(Data<E, K> data) {
        switch(affects) {
            case CAUSER -> enforceCauserPunishment(data);
            case ALL -> enforceAllPunishment(data, Team.getTeamPlayerIn(context, data.playerUUID()));
        }
    }

    public abstract <E extends Event, K> void enforceCauserPunishment(Data<E, K> data);

    public abstract <E extends Event, K> void enforceAllPunishment(Data<E, K> data, Team team);

    public Affects getAffects() {
        return affects;
    }

    protected static <E extends Enum<E>> Affects map(E affectEnum) {
        return Punishment.Affects.fromJSONString(affectEnum.toString());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        Punishment that = (Punishment) o;
        return affects == that.affects;
    }

    @Override
    public int hashCode() {
        return Objects.hash(affects);
    }

    public enum Affects {
        CAUSER("causer"),
        ALL("all");

        private final String value;

        Affects(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Affects fromJSONString(String valueInJSONString) {
            return switch(valueInJSONString) {
                case "all" -> ALL;
                case "causer" -> CAUSER;
                default -> throw new RuntimeException("Failed to match %s".formatted(valueInJSONString));
            };
        }
    }
}
