package wand555.github.io.challenges.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import wand555.github.io.challenges.generated.ChallengesSchema;

public abstract class ModelValidator extends Validator {

    @Override
    protected final ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, String json) {
        try {
            ChallengesSchema challengesSchema = new ObjectMapper().readValue(json, ChallengesSchema.class);
            return performValidation(builder, challengesSchema);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ModelValidator fired before JSONValidator ran!", e);
        }
    }

    protected abstract ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, ChallengesSchema challengesSchema);
}
