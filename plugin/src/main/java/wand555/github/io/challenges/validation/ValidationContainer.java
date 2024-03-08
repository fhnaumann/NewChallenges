package wand555.github.io.challenges.validation;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

public class ValidationContainer {

    private final List<Validator> validators;

    public ValidationContainer(List<Validator> validators) {
        this.validators = validators;
    }

    public ValidationContainer(Validator... validators) {
        Preconditions.checkArgument(validators != null, "Validators may not be null.");
        Preconditions.checkArgument(validators.length != 0, "Validators may not be empty.");
        this.validators = List.of(validators);
    }

    public ValidationResult validate(ValidationResult.ValidationResultBuilder builder, String json) {
        // builder is being mutated in each validate call
        validators.forEach(validator -> validator.validate(builder, json));
        return builder.build();
    }
}
