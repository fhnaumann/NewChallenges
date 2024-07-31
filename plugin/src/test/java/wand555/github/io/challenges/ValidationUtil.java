package wand555.github.io.challenges;

import wand555.github.io.challenges.validation.ValidationResult;
import wand555.github.io.challenges.validation.Violation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationUtil {

    public static void assertResult(ValidationResult expected, ValidationResult actual) {
        assertEquals(expected.isValid(), actual.isValid());
        assertEquals(expected.getViolations().size(), actual.getViolations().size());
        for(int i = 0; i < expected.getViolations().size(); i++) {
            Violation expectedViolation = expected.getViolations().get(i);
            Violation actualViolation = actual.getViolations().get(i);
            assertEquals(expectedViolation.getWhere(), actualViolation.getWhere());
            assertEquals(expectedViolation.getLevel(), actualViolation.getLevel());
        }
    }
}
