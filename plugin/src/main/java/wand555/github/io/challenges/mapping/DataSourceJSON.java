package wand555.github.io.challenges.mapping;

import org.bukkit.Keyed;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface DataSourceJSON<E extends Keyed> {
    E toEnum();

    static <T extends Keyed> String toCode(T dataSourceElement) {
        return dataSourceElement.key().value();
    }

    static <T extends DataSourceJSON<E>, E extends Keyed> T fromCode(Collection<T> dataSource, String code) {
        return fromCode(dataSource, code, t -> t.getCode().equals(code));
    }

    static <T extends DataSourceJSON<E>, E extends Keyed> T fromCode(Collection<T> dataSource, String code, Predicate<T> filter) {
        List<T> shouldBeExactlyOne = dataSource.stream().filter(filter).toList();
        if(shouldBeExactlyOne.isEmpty()) {
            throw new RuntimeException("'%s' does not exist in data source %s".formatted(code, dataSource));
        }
        if(shouldBeExactlyOne.size() > 1) {
            throw new RuntimeException("%s exists more than once in data source, all matches: '%s'".formatted(code, shouldBeExactlyOne));
        }
        return shouldBeExactlyOne.get(0);
    }

    String getCode();
}
