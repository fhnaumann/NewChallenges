package wand555.github.io.challenges;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.schematron.pure.SchematronResourcePure;
import org.json.JSONTokener;
import wand555.github.io.challenges.generated.*;
import wand555.github.io.challenges.mapping.ModelMapper;

import java.io.*;
import java.nio.file.Files;

public class FileManager {

    public static void writeToFile(ChallengeManager challengeManager, Writer writer) {
        EnabledRules enabledRulesConfig = new EnabledRules();
        challengeManager.getRules().forEach(rule -> rule.addToGeneratedConfig(enabledRulesConfig));
        RulesConfig rulesConfig = new RulesConfig(new PunishmentsConfig(), enabledRulesConfig);

        GoalsConfig goalsConfig = new GoalsConfig();
        challengeManager.getGoals().forEach(goal -> goal.addToGeneratedConfig(goalsConfig));


        TestOutputSchema testOutputSchema = new TestOutputSchema(goalsConfig, rulesConfig);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(writer, testOutputSchema);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ChallengeManager readFromFile(File file, Challenges plugin) throws LoadValidationException {
        String json = null;
        try {
            json = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Validator validator = new Validator();
        InputStream schemaStream = Challenges.class.getResourceAsStream("/test-output-schema.json");
        JsonNode schemaRoot = null;
        try {
            schemaRoot = new ObjectMapper().readTree(schemaStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SchematronResourcePure schematronResourcePure = SchematronResourcePure.fromInputStream("abc", Main.class.getResourceAsStream("/constraints.sch"));
        ValidationResult validationResult = validator.validate(json, Challenges.class.getResourceAsStream("/test-output-schema.json"), schematronResourcePure);
        if(validationResult.isValid()) {
            ObjectMapper objectMapper = new ObjectMapper();
            TestOutputSchema testOutputSchema = null;
            try {
                testOutputSchema = objectMapper.readValue(json, TestOutputSchema.class);
                ResourceBundleContext resourceBundleContext = new ResourceBundleContext(null);
                ModelMapper modelMapper = new ModelMapper(plugin, resourceBundleContext, schemaRoot);
                ChallengeManager challengeManager = modelMapper.map2ModelClasses(testOutputSchema);
                return challengeManager;
            } catch (JsonProcessingException e) {
                // should never happen because it was validated first.
                throw new RuntimeException(e);
            }
        }
        else {
            throw new LoadValidationException(validationResult);
        }
    }
}
