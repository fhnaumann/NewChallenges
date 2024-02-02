package wand555.github.io.challenges.validation;

import java.util.Objects;

public class Violation {

    private final String where;
    private final String message;
    private final Level level;

    public Violation(String where, String message, Level level) {
        this.where = where;
        this.message = message;
        this.level = level;
    }

    public String getWhere() {
        return where;
    }

    public String getMessage() {
        return message;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Violation violation = (Violation) o;
        return Objects.equals(where, violation.where) && Objects.equals(message, violation.message) && level == violation.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(where, message, level);
    }

    public enum Level {
        WARNING, ERROR,
    }
}
