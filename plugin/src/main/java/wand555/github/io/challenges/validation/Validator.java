package wand555.github.io.challenges.validation;

import wand555.github.io.challenges.files.ProgressListener;

public abstract class Validator {

    public ValidationResult validate(ValidationResult.ValidationResultBuilder builder, String json, ProgressListener progressListener) {
        ValidationResult.ValidationResultBuilder intermediateBuilder = performValidation(builder, json, progressListener);
        return intermediateBuilder.build();
    }

    protected abstract ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, String json, ProgressListener progressListener);
}
