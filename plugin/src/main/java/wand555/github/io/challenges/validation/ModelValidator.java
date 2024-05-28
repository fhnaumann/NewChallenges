package wand555.github.io.challenges.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import wand555.github.io.challenges.generated.Model;

public abstract class ModelValidator extends Validator {

    @Override
    protected final ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, String json) {
        try {
            Model challengesSchema = new ObjectMapper().readValue(json, Model.class);
            return performValidation(builder, challengesSchema);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ModelValidator fired before JSONValidator ran!", e);
        }
    }

    protected abstract ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, Model challengesSchema);
}
