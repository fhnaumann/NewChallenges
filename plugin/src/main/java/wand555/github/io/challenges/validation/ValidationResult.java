package wand555.github.io.challenges.validation;

import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.exception.ExceptionUtils;
import wand555.github.io.challenges.ComponentUtil;
import wand555.github.io.challenges.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValidationResult {

    private final boolean valid;
    private final List<Violation> violations;
    private final Exception initialException;

    public ValidationResult() {
        this(true, List.of(), null);
    }

    public ValidationResult(boolean valid, List<Violation> violations, Exception initialException) {
        this.valid = valid;
        this.violations = violations;
        this.initialException = initialException;
    }

    public boolean isValid() {
        return valid;
    }

    public List<Violation> getViolations() {
        return ImmutableList.copyOf(violations);
    }

    public Component asFormattedComponent(Context context) {
        Component component = Component.empty();

        /*
        for(Violation violation : getViolations()) {
            component = component.append(ComponentUtil.formatChallengesPrefixChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenges.validation.failure.result.violation",
                    Map.of(
                            "level", Component.text(violation.getLevel().toString().toUpperCase()),
                            "where", Component.text(violation.getWhere()),
                            "message", Component.text(violation.getMessage())
                    ),
                    false
            )).appendNewline();
        }*/
        component = getViolations().stream().map(violation -> ComponentUtil.formatChallengesPrefixChatMessage(
                context.plugin(),
                context.resourceBundleContext().miscResourceBundle(),
                "challenges.validation.failure.result.violation",
                Map.of(
                        "level", Component.text(violation.getLevel().toString().toUpperCase()),
                        "where", Component.text(violation.getWhere()),
                        "message", Component.text(violation.getMessage())
                ),
                false
        )).reduce(Component.empty(), (component1, component2) -> component1.appendNewline().append(component2));

        /*component = getViolations().stream().reduce((Component)Component.empty(), (textComponent, violation) -> {
           return textComponent.appendNewline().append(ComponentUtil.formatChallengesPrefixChatMessage(
                   context.plugin(),
                   context.resourceBundleContext().miscResourceBundle(),
                   "challenges.validation.failure.result.violation",
                   Map.of(
                           "level", Component.text(violation.getLevel().toString().toUpperCase()),
                           "where", Component.text(violation.getWhere()),
                           "message", Component.text(violation.getMessage())
                   ),
                   false
           ));
        }, (textComponent, textComponent2) -> textComponent.appendNewline().append(textComponent2));

         */

        if(initialException != null) {
            component = component.append(ComponentUtil.formatChallengesPrefixChatMessage(
                    context.plugin(),
                    context.resourceBundleContext().miscResourceBundle(),
                    "challenges.validation.failure.result.exception",
                    Map.of(
                            "exception", Component.text(ExceptionUtils.getStackTrace(initialException))
                    ),
                    false
            ));
        }
        return component;
    }

    public String asFormattedString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Provided configuration is valid? ").append(this.valid);
        // may have violations that are warnings

        if(getViolations().isEmpty()) {
            if(initialException != null) {
                builder.append(System.lineSeparator()).append("Additional exception: ").append(initialException);
            }
            return builder.toString();
        }

        builder.append(System.lineSeparator());
        Map<Violation.Level, List<Violation>> groupedByLevel = getViolations().stream()
                .collect(Collectors.groupingBy(
                        Violation::getLevel
                ));
        List<Violation> warnings = groupedByLevel.get(Violation.Level.WARNING);
        if(warnings != null && !warnings.isEmpty()) {
            builder.append("Warnings:").append(System.lineSeparator());
            warnings.forEach(violation -> {
                builder.append("  Where: ").append(violation.getWhere()).append(System.lineSeparator())
                        .append("    Message: ").append(violation.getMessage()).append(System.lineSeparator());
            });
        }

        List<Violation> errors = groupedByLevel.get(Violation.Level.ERROR);
        if(errors != null && !errors.isEmpty()) {
            builder.append("Violations:").append(System.lineSeparator());
            errors.forEach(violation -> {
                builder.append("  Where: ").append(violation.getWhere()).append(System.lineSeparator())
                        .append("    Message: ").append(violation.getMessage()).append(System.lineSeparator());
            });
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
            if(violation.getLevel() == Violation.Level.ERROR) {
                setValid(false);
            }
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
