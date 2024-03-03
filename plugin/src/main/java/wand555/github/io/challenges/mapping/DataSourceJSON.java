package wand555.github.io.challenges.mapping;

import org.bukkit.Keyed;

import java.util.Collection;
import java.util.List;

public interface DataSourceJSON<E extends Keyed> {
    E toEnum();

    static <T extends Keyed> String toCode(T dataSourceElement) {
        return dataSourceElement.key().value();
    }

    static <T extends DataSourceJSON<E>, E extends Keyed> T fromCode(Collection<T> dataSource, String code) {
        List<T> shouldBeExactlyOne = dataSource.stream().filter(t -> t.getCode().equals(code)).toList();
        if(shouldBeExactlyOne.isEmpty()) {
            throw new RuntimeException("'%s' does not exist in data source %s".formatted(code, dataSource));
        }
        if(shouldBeExactlyOne.size() > 1) {
            throw new RuntimeException("%s exists more than once in data source %s".formatted(code, dataSource));
        }
        return shouldBeExactlyOne.get(0);
    }

    String getCode();
}
