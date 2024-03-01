package wand555.github.io.challenges.mapping;

import org.bukkit.Keyed;

public interface DataSourceJSON<E extends Enum<E>> {
    E toEnum();

    static <T extends Keyed> String toCode(T dataSourceElement) {
        return dataSourceElement.key().value();
    }

    String getCode();
}
