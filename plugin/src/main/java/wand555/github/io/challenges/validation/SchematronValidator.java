package wand555.github.io.challenges.validation;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.schematron.pure.SchematronResourcePure;
import com.helger.schematron.pure.errorhandler.CollectingPSErrorHandler;
import com.helger.schematron.pure.errorhandler.IPSErrorHandler;
import com.helger.schematron.svrl.AbstractSVRLMessage;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.SVRLSuccessfulReport;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import org.json.JSONObject;
import org.json.XML;
import wand555.github.io.challenges.Challenges;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SchematronValidator extends Validator {

    private final SchematronResourcePure schematronResourcePure;

    public SchematronValidator(InputStream schematronInputStream) {
        this.schematronResourcePure = SchematronResourcePure.fromInputStream("ignored", schematronInputStream);
        //this.schematronResourcePure = SchematronResourcePure.fromFile(Challenges.class.getResource("/constraints.sch").getFile());
        IPSErrorHandler errorHandler = new CollectingPSErrorHandler();
        schematronResourcePure.setErrorHandler(errorHandler);
    }

    @Override
    protected ValidationResult.ValidationResultBuilder performValidation(ValidationResult.ValidationResultBuilder builder, String json) {

        try {
            String xml = XML.toString(new JSONObject(json), "root");
            Source streamSource = new StreamSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))); // TODO
            SchematronOutputType schematronOutputType = schematronResourcePure.applySchematronValidationToSVRL(
                    streamSource);
            ICommonsList<AbstractSVRLMessage> svrlFailedAsserts = SVRLHelper.getAllFailedAssertionsAndSuccessfulReports(
                    schematronOutputType);
            addSchematronViolationsToBuilder(builder, svrlFailedAsserts);
        } catch(Exception e) {
            builder.setInitialException(e);
            //throw new RuntimeException(e);

        }
        return builder;
    }

    private void addSchematronViolationsToBuilder(ValidationResult.ValidationResultBuilder builder, ICommonsList<AbstractSVRLMessage> svrlFailedAsserts) {
        svrlFailedAsserts.forEach(svrlFailedAssert -> {
            Violation.Level level = Violation.Level.ERROR;
            if(svrlFailedAssert instanceof SVRLSuccessfulReport) {
                level = Violation.Level.WARNING;
            }
            builder.addViolation(new Violation(svrlFailedAssert.getLocation(), svrlFailedAssert.getText(), level));
        });
    }
}
