package wand555.github.io.challenges.punishments;

import org.bukkit.entity.Player;
import wand555.github.io.challenges.Storable;
import wand555.github.io.challenges.generated.PunishmentsConfig;
import wand555.github.io.challenges.rules.PunishableRule;

import java.util.Objects;

public abstract class Punishment {

    private final Affects affects;

    public Punishment(Affects affects) {
        this.affects = affects;
    }

    public abstract void enforcePunishment(Player causer);

    /**
     * During the model -> generated json classes mapping, every {@link wand555.github.io.challenges.rules.PunishableRule}
     * requires a {@link PunishmentsConfig}. This method's sole purpose is to "ask" each concrete punishment implementation
     * where to put it in the punishments config.
     *
     * @param generatedPunishmentsConfig where to put "itself" in the obj.
     */
    public abstract void addToGeneratedPunishmentsConfig(PunishmentsConfig generatedPunishmentsConfig);

    public Affects getAffects() {
        return affects;
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
        CAUSER("Causer"),
        ALL("All");

        private String value;

        Affects(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Affects fromJSONString(String valueInJSONString) {
            switch (valueInJSONString) {
                case "All": return ALL;
                case "Causer": return CAUSER;
                default: throw new RuntimeException();
            }
        }
    }
}
