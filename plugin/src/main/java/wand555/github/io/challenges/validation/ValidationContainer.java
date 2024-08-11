package wand555.github.io.challenges.validation;

import com.google.common.base.Preconditions;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.files.ProgressListener;

import java.util.List;
import java.util.logging.Logger;

public class ValidationContainer {

    private static Logger logger = ChallengesDebugLogger.getLogger(ValidationContainer.class);

    private final ProgressListener progressListener;
    private final List<Validator> validators;

    public ValidationContainer(ProgressListener progressListener, List<Validator> validators) {
        this.validators = validators;
        this.progressListener = progressListener;
    }

    public ValidationContainer(ProgressListener progressListener, Validator... validators) {
        Preconditions.checkArgument(validators != null, "Validators may not be null.");
        Preconditions.checkArgument(validators.length != 0, "Validators may not be empty.");
        this.progressListener = progressListener;
        this.validators = List.of(validators);
    }

    public ValidationResult validate(ValidationResult.ValidationResultBuilder builder, String json) {
        logger.fine("Validating with %s".formatted(validators.toString()));
        // builder is being mutated in each validate call
        for(int i=0; i<validators.size(); i++) {
            Validator validator = validators.get(i);
            ValidationResult intermediate = validator.validate(builder, json, progressListener);
            if(!intermediate.isValid()) {
                return builder.build();
            }
            // validators make up 80% of loading (arbitrarily defined, just a guess)
            //progressListener.onProgress(0.1 + (double) i/validators.size() * 0.8);
        }
        return builder.build();
    }
}
