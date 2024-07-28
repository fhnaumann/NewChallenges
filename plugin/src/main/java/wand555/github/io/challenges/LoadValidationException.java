package wand555.github.io.challenges;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import wand555.github.io.challenges.validation.ValidationResult;

public class LoadValidationException extends Exception {

    private final ValidationResult validationResult;

    public LoadValidationException(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }
}
