package wand555.github.io.challenges;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.schematron.pure.SchematronResourcePure;
import com.helger.schematron.pure.errorhandler.CollectingPSErrorHandler;
import com.helger.schematron.pure.errorhandler.IPSErrorHandler;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Validator {

    private final ValidationResult.ValidationResultBuilder builder = new ValidationResult.ValidationResultBuilder();

    public Validator() {

    }

    public ValidationResult validate(String json, InputStream schemaStream, SchematronResourcePure schematronResourcePure) {
        return validateAgainstJSONSchema(json, schemaStream)
                .validateAgainstSchematron(json, schematronResourcePure)
                .builder.build();
    }

    private Validator validateAgainstJSONSchema(String json, InputStream schemaStream) {
        JSONObject rawSchema = new JSONObject(new JSONTokener(schemaStream));
        Schema schema = SchemaLoader.load(rawSchema);
        try {
            schema.validate(new JSONObject(json));
        } catch (ValidationException e) {
            JSONObject errors = e.toJSON();
            addJSONViolationsToBuilder(errors);
        }
        return this;
    }

    private void addJSONViolationsToBuilder(JSONObject errors) {
        JSONArray causingExceptions = errors.getJSONArray("causingExceptions");
        for(Object obj : causingExceptions) {
            JSONObject jsonException = (JSONObject) obj;
            String where = jsonException.getString("pointerToViolation");
            String message = jsonException.getString("message");
            builder.addViolation(new Violation(where, message));
        }
    }

    private Validator validateAgainstSchematron(String json, SchematronResourcePure schematronResourcePure) {

        String xml = XML.toString(new JSONObject(json), "root");

        try {
            IPSErrorHandler errorHandler = new CollectingPSErrorHandler();
            schematronResourcePure.setErrorHandler(errorHandler);
            Source streamSource = new StreamSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))); // TODO
            SchematronOutputType schematronOutputType = schematronResourcePure.applySchematronValidationToSVRL(streamSource);
            List<Object> failedAsserts = schematronOutputType.getActivePatternAndFiredRuleAndFailedAssert();
            System.out.println(failedAsserts.size());
            ICommonsList<SVRLFailedAssert> svrlFailedAsserts = SVRLHelper.getAllFailedAssertions(schematronOutputType);

            addSchematronViolationsToBuilder(svrlFailedAsserts);
        } catch (Exception e) {
            builder.setInitialException(e);
        }
        return this;
    }

    private void addSchematronViolationsToBuilder(ICommonsList<SVRLFailedAssert> svrlFailedAsserts) {
        svrlFailedAsserts.forEach(svrlFailedAssert -> {
            builder.addViolation(new Violation(svrlFailedAssert.getLocation(), svrlFailedAssert.getText()));
        });
    }
}
