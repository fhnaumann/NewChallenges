package wand555.github.io.challenges.punishments;

import org.bukkit.entity.Player;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.JSONConfigGroup;
import wand555.github.io.challenges.StatusInfo;
import wand555.github.io.challenges.generated.PunishmentsConfig;

import java.util.Objects;

public abstract class Punishment implements JSONConfigGroup<PunishmentsConfig>, StatusInfo {

    protected final Context context;
    private final Affects affects;

    public Punishment(Context context, Affects affects) {
        this.context = context;
        this.affects = affects;
    }

    public void enforcePunishment(Player causer) {
        switch (affects) {
            case CAUSER -> enforceCauserPunishment(causer);
            case ALL -> enforceAllPunishment();
        }
    }

    public abstract void enforceCauserPunishment(Player causer);

    public abstract void enforceAllPunishment();

    public Affects getAffects() {
        return affects;
    }

    protected static <E extends Enum<E>> Affects map(E affectEnum) {
        return Punishment.Affects.fromJSONString(affectEnum.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
            return switch (valueInJSONString) {
                case "all" -> ALL;
                case "causer" -> CAUSER;
                default -> throw new RuntimeException("Failed to match %s".formatted(valueInJSONString));
            };
        }
    }
}
