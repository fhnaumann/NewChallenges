package wand555.github.io.challenges;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.state.EValidity;
import com.helger.schematron.pure.SchematronResourcePure;
import com.helger.schematron.pure.errorhandler.CollectingPSErrorHandler;
import com.helger.schematron.pure.errorhandler.IPSErrorHandler;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.jaxb.FailedAssert;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;
import wand555.github.io.challenges.generated.DeathPunishmentConfig;
import wand555.github.io.challenges.generated.HealthPunishmentConfig;
import wand555.github.io.challenges.generated.TestOutputSchema;
import wand555.github.io.challenges.utils.ResourcePackHelper;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class Main {


    public static void main(String[] args) throws IOException {
    //public static void main(String[] args, File file, Challenges plugin) {
        JSONObject fontDefaultJSON = ResourcePackHelper.createFontDefaultJSON();
        File file = new File("default.json");
        //new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(file, fontDefaultJSON);
        try( BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
           writer.write(fontDefaultJSON.toString(4));
        }


        /*
        try {
            String json = Files.readString(Paths.get(Main.class.getResource("/dummy_data.json").toURI()));
            //String json = Files.readString(file.toPath());
            InputStream stream = Main.class.getResourceAsStream("/test-output-schema.json");
            SchematronResourcePure schematronResourcePure = SchematronResourcePure.fromInputStream("abc", Main.class.getResourceAsStream("/constraints.sch"));
            //SchematronResourcePure schematronResourcePure = SchematronResourcePure.fromInputStream("abc", new FileInputStream(new File(plugin.getDataFolder(), "constraints.sch")));
            Validator validator = new Validator();
            ValidationResult result = validator.validate(json, stream, schematronResourcePure);
            System.out.println(result.asFormattedString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
         */
    }

    public static void validateAgainstJSONSchema() {
        InputStream stream = Main.class.getResourceAsStream("/test-output-schema.json");
        JSONObject rawSchema = new JSONObject(new JSONTokener(stream));
        Schema schema = SchemaLoader.load(rawSchema);
        try {
            String json = Files.readString(Paths.get(Main.class.getResource("/dummy_data.json").toURI()));
            //System.out.println(json);
            schema.validate(new JSONObject(json));
        } catch (ValidationException e) {
            System.out.println(e.toJSON());
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement jsonElement = JsonParser.parseString(e.toJSON().toString());
            String out = gson.toJson(jsonElement);
            System.out.println(out);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void validateAgainstSchematron() throws Exception {
        String json = Files.readString(Paths.get(Main.class.getResource("/dummy_data.json").toURI()));
        JSONObject object = new JSONObject(json);
        System.out.println(object);
        String xml = XML.toString(object, "root");
        System.out.println(xml);

        SchematronResourcePure schematronResourcePure = SchematronResourcePure.fromInputStream("abc", Main.class.getResourceAsStream("/constraints.sch"));
        IPSErrorHandler errorHandler = new CollectingPSErrorHandler();
        schematronResourcePure.setErrorHandler(errorHandler);
        Source streamSource = new StreamSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))); // TODO
        SchematronOutputType schematronOutputType = schematronResourcePure.applySchematronValidationToSVRL(streamSource);
        List<Object> failedAsserts = schematronOutputType.getActivePatternAndFiredRuleAndFailedAssert();
        System.out.println(failedAsserts.size());
        ICommonsList<SVRLFailedAssert> failedAsserts1 = SVRLHelper.getAllFailedAssertions(schematronOutputType);
        failedAsserts1.forEach(svrlFailedAssert -> {
            System.out.println(svrlFailedAssert);
        });
    }
}
