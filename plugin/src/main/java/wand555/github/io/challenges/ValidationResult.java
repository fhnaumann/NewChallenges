package wand555.github.io.challenges;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    private final boolean valid;
    private final List<Violation> violations;
    private final Exception initialException;

    public ValidationResult(boolean valid, List<Violation> violations, Exception initialException) {
        this.valid = valid;
        this.violations = violations;
        this.initialException = initialException;
    }

    public boolean isValid() {
        return valid;
    }

    public String asFormattedString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Provided configuration is valid? ").append(this.valid);
        if(valid) {
           return builder.toString();
        }
        builder.append(System.lineSeparator());
        builder.append("Violations:").append(System.lineSeparator());
        violations.forEach(violation -> {
            builder.append("Where: ").append(violation.getWhere()).append(System.lineSeparator())
                    .append("Message: ").append(violation.getMessage()).append(System.lineSeparator());
        });
        if(initialException != null) {
            builder.append("Additional exception: ").append(initialException);
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", violations=" + violations +
                ", initialException=" + initialException +
                '}';
    }

    public static class ValidationResultBuilder {
        private boolean valid = true; // assume valid until proven otherwise (violation or exception is added)
        private final List<Violation> violations = new ArrayList<>();
        private Exception initialException;

        public ValidationResultBuilder() {
        }

        public ValidationResultBuilder setValid(boolean valid) {
            this.valid = valid;
            return this;
        }

        public ValidationResultBuilder setInitialException(Exception initialException) {
            setValid(false);
            this.initialException = initialException;
            return this;
        }

        public ValidationResultBuilder addViolation(Violation violation) {
            setValid(false);
            violations.add(violation);
            return this;
        }
        public ValidationResultBuilder addViolations(Violation... violations) {
            List.of(violations).forEach(this::addViolation);
            return this;
        }

        public ValidationResult build() {
            return new ValidationResult(valid, violations, initialException);
        }
    }
}
