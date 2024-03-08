package wand555.github.io.challenges.validation;

public abstract class Validator {

    public ValidationResult validate(ValidationResult.ValidationResultBuilder builder, String json) {
        ValidationResult.ValidationResultBuilder intermediateBuilder = performValidation(builder, json);
       return intermediateBuilder.build();
    }

    protected abstract ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, String json);
}
