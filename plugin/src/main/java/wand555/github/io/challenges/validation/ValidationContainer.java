package wand555.github.io.challenges.validation;

import com.google.common.base.Preconditions;
import wand555.github.io.challenges.ChallengesDebugLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ValidationContainer {

    private static Logger logger = ChallengesDebugLogger.getLogger(ValidationContainer.class);

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
        logger.fine("Validating with %s".formatted(validators.toString()));
        // builder is being mutated in each validate call
        for(Validator validator : validators) {
            ValidationResult intermediate = validator.validate(builder, json);
            if(!intermediate.isValid()) {
                return builder.build();
            }
        }
        return builder.build();
    }
}
