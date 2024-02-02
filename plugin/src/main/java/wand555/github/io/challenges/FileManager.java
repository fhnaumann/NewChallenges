package wand555.github.io.challenges;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.schematron.pure.SchematronResourcePure;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import wand555.github.io.challenges.generated.*;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.validation.ValidationResult;
import wand555.github.io.challenges.validation.Validator;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Locale;
import java.util.ResourceBundle;

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

    public static ChallengeManager readFromFile(File file, Challenges plugin) throws LoadValidationException, IOException {
        String json = null;
        try {
            json = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InputStream schemaStream = Challenges.class.getResourceAsStream("/test-output-schema.json");
        URL schematronStream = Main.class.getResource("/constraints.sch");
        Validator validator = new Validator(schemaStream, new File(schematronStream.getFile()));

        JsonNode schemaRoot = null;
        try {
            schemaRoot = new ObjectMapper().readTree(schemaStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ValidationResult validationResult = validator.validate(json);
        if(validationResult.isValid()) {
            ObjectMapper objectMapper = new ObjectMapper();
            TestOutputSchema testOutputSchema = null;
            try {
                testOutputSchema = objectMapper.readValue(json, TestOutputSchema.class);

                Context context = new Context.Builder()
                        .withPlugin(plugin)
                        .withRuleResourceBundle(ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get()))
                        .withGoalResourceBundle(ResourceBundle.getBundle("goals", Locale.US, UTF8ResourceBundleControl.get()))
                        .withPunishmentResourceBundle(ResourceBundle.getBundle("punishments", Locale.US, UTF8ResourceBundleControl.get()))
                        .withSchemaRoot(schemaRoot)
                        .withChallengeManager(new ChallengeManager())
                        .build();
                context.challengeManager().setContext(context); // immediately set context so it is available in the manager
                ModelMapper.map2ModelClasses(context, testOutputSchema);


                return context.challengeManager();
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
