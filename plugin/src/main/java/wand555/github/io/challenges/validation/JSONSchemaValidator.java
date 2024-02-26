package wand555.github.io.challenges.validation;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class JSONSchemaValidator extends Validator {

    private final JSONObject jsonSchema;

    public JSONSchemaValidator(InputStream jsonSchemaStream) {
        this.jsonSchema = new JSONObject(new JSONTokener(jsonSchemaStream));
    }
    @Override
    protected ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, String json) {
       Schema schema = SchemaLoader.load(jsonSchema);
       try {
           schema.validate(new JSONObject(json));
       } catch (ValidationException e) {
           JSONObject errors = e.toJSON();
           addJSONViolationsToBuilder(builder, errors);
       }
       return builder;
    }

    private void addJSONViolationsToBuilder(ValidationResult.ValidationResultBuilder builder, JSONObject errors) {
        JSONArray causingExceptions = errors.getJSONArray("causingExceptions");
        if (causingExceptions.isEmpty()) {
            builder.addViolation(new Violation(errors.getString("pointerToViolation"), errors.getString("message"), Violation.Level.ERROR));
        }
        for (Object obj : causingExceptions) {
            JSONObject jsonException = (JSONObject) obj;
            String where = jsonException.getString("pointerToViolation");
            String message = jsonException.getString("message");
            builder.addViolation(new Violation(where, message, Violation.Level.ERROR));
        }
    }
}
