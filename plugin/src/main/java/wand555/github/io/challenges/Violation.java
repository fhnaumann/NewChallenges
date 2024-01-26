package wand555.github.io.challenges;

public class Violation {

    private final String where;
    private final String message;

    public Violation(String where, String message) {
        this.where = where;
        this.message = message;
    }

    public String getWhere() {
        return where;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Violation{" +
                "where='" + where + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
