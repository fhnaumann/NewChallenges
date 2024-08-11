package wand555.github.io.challenges.validation;

import wand555.github.io.challenges.files.ProgressListener;

public class EmptyDataValidator extends Validator {
    @Override
    protected ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, String json, ProgressListener progressListener) {
        if(json.isEmpty() || json.isBlank() || json.equals("{}")) {
            builder.addViolation(new Violation("root", "File is empty!", Violation.Level.ERROR));
        }
        return builder;
    }
}
