package wand555.github.io.challenges.mapping;

public interface DataSourceJSON<E extends Enum<E>> {

    E toEnum();

    String getCode();
}
