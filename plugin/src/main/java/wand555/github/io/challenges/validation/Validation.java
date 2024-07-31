package wand555.github.io.challenges.validation;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.schematron.pure.SchematronResourcePure;
import com.helger.schematron.pure.errorhandler.CollectingPSErrorHandler;
import com.helger.schematron.pure.errorhandler.IPSErrorHandler;
import com.helger.schematron.svrl.AbstractSVRLMessage;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.SVRLSuccessfulReport;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.validation.goals.GoalsValidator;
import wand555.github.io.challenges.validation.rules.RulesValidator;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Validation {

    private ValidationResult.ValidationResultBuilder builder;

    private JSONObject jsonSchema;
    private SchematronResourcePure schematronResourcePure;

    private List<Validator> validators;

    public Validation(InputStream jsonSchemaStream, File schematronFile) throws IOException {
        this.jsonSchema = new JSONObject(new JSONTokener(jsonSchemaStream));
        jsonSchemaStream.close();
        this.schematronResourcePure = SchematronResourcePure.fromFile(schematronFile);
        //this.schematronResourcePure = SchematronResourcePure.fromFile(Challenges.class.getResource("/constraints.sch").getFile());
        IPSErrorHandler errorHandler = new CollectingPSErrorHandler();
        schematronResourcePure.setErrorHandler(errorHandler);
    }

    public Validation(InputStream jsonSchemaStream, InputStream schematronInputStream) throws IOException {
        this.jsonSchema = new JSONObject(new JSONTokener(jsonSchemaStream));
        jsonSchemaStream.close();
        this.schematronResourcePure = SchematronResourcePure.fromInputStream("ignored", schematronInputStream);
        //this.schematronResourcePure = SchematronResourcePure.fromFile(Challenges.class.getResource("/constraints.sch").getFile());
        IPSErrorHandler errorHandler = new CollectingPSErrorHandler();
        schematronResourcePure.setErrorHandler(errorHandler);
    }

    public ValidationResult validate(String json) {


        builder = new ValidationResult.ValidationResultBuilder();
        return validateAgainstJSONSchema(json)
                .validateAgainstSchematron(json)
                .builder.build();
    }

    public static ValidationResult modernValidate(String json, InputStream jsonSchemaStream, InputStream schematronStream, DataSourceContext dataSourceContext) {
        return new ValidationContainer(
                new EmptyDataValidator(),
                new JSONSchemaValidator(jsonSchemaStream),
                new SchematronValidator(schematronStream),
                new RulesValidator(dataSourceContext),
                new GoalsValidator(dataSourceContext)
        ).validate(new ValidationResult.ValidationResultBuilder(), json);
    }

    private Validation validateAgainstJSONSchema(String json) {
        Schema schema = SchemaLoader.load(jsonSchema);
        try {
            schema.validate(new JSONObject(json));
        } catch(ValidationException e) {
            JSONObject errors = e.toJSON();
            addJSONViolationsToBuilder(errors);
        }
        return this;
    }

    private void addJSONViolationsToBuilder(JSONObject errors) {
        JSONArray causingExceptions = errors.getJSONArray("causingExceptions");
        if(causingExceptions.isEmpty()) {
            builder.addViolation(new Violation(errors.getString("pointerToViolation"),
                                               errors.getString("message"),
                                               Violation.Level.ERROR
            ));
        }
        for(Object obj : causingExceptions) {
            JSONObject jsonException = (JSONObject) obj;
            String where = jsonException.getString("pointerToViolation");
            String message = jsonException.getString("message");
            builder.addViolation(new Violation(where, message, Violation.Level.ERROR));
        }
    }

    private Validation validateAgainstSchematron(String json) {

        String xml = XML.toString(new JSONObject(json), "root");
        try {
            //this.schematronResourcePure = SchematronResourcePure.fromFile(Challenges.class.getResource("/constraints.sch").getFile());
            //schematronResourcePure = SchematronResourcePure.fromFile(new File("src/main/resources/constraints.sch"));
            var test = Challenges.class.getResource("/constraints.sch");
            System.out.println(test);
            //schematronResourcePure = SchematronResourcePure.fromFile(new File(test.getFile()));
            //schematronResourcePure = SchematronResourcePure.fromInputStream("ignored", Challenges.class.getResourceAsStream("/constraints.sch"));
            Source streamSource = new StreamSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))); // TODO

            SchematronOutputType schematronOutputType = schematronResourcePure.applySchematronValidationToSVRL(
                    streamSource);


            List<Object> failedAsserts = schematronOutputType.getActivePatternAndFiredRuleAndFailedAssert();
            ICommonsList<AbstractSVRLMessage> svrlFailedAsserts = SVRLHelper.getAllFailedAssertionsAndSuccessfulReports(
                    schematronOutputType);
            addSchematronViolationsToBuilder(svrlFailedAsserts);
        } catch(Exception e) {
            builder.setInitialException(e);
            throw new RuntimeException(e);

        }
        return this;
    }

    private void addSchematronViolationsToBuilder(ICommonsList<AbstractSVRLMessage> svrlFailedAsserts) {
        svrlFailedAsserts.forEach(svrlFailedAssert -> {
            Violation.Level level = Violation.Level.ERROR;
            if(svrlFailedAssert instanceof SVRLSuccessfulReport) {
                level = Violation.Level.WARNING;
            }
            builder.addViolation(new Violation(svrlFailedAssert.getLocation(), svrlFailedAssert.getText(), level));
        });
    }
}
