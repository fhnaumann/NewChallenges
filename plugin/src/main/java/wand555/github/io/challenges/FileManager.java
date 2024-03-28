package wand555.github.io.challenges;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import wand555.github.io.challenges.generated.*;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.validation.BossBarShower;
import wand555.github.io.challenges.validation.Validation;
import wand555.github.io.challenges.validation.ValidationResult;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

public class FileManager {


    public static void writeToFile(ChallengeManager challengeManager, Writer writer) {
        EnabledRules enabledRulesConfig = new EnabledRules();
        challengeManager.getRules().forEach(rule -> rule.addToGeneratedConfig(enabledRulesConfig));
        PunishmentsConfig globalPunishmentsConfig = new PunishmentsConfig();
        challengeManager.getGlobalPunishments().forEach(punishment -> punishment.addToGeneratedConfig(globalPunishmentsConfig));
        RulesConfig rulesConfig = new RulesConfig(globalPunishmentsConfig, enabledRulesConfig);

        GoalsConfig goalsConfig = new GoalsConfig();
        challengeManager.getGoals().forEach(goal -> goal.addToGeneratedConfig(goalsConfig));

        // casting time from long to int could be problematic...
        // on the other hand ~24000 days fit into an int, no one will reach that (hopefully)
        ChallengesSchema ChallengesSchema = new ChallengesSchema(goalsConfig, rulesConfig, (int) challengeManager.getTime());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, ChallengesSchema);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Context readFromFile(File file, Challenges plugin) throws LoadValidationException {
        String json = null;
        try {
            json = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        InputStream schemaStream = Main.class.getResourceAsStream("/challenges_schema.json");
        InputStream schematronStream = Main.class.getResourceAsStream("/constraints.sch");
        System.out.println(schematronStream);

        JsonNode schemaRoot = null;

        try {
            schemaRoot = new ObjectMapper().readTree(Main.class.getResourceAsStream("/challenges_schema.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        DataSourceContext sourceContext = new DataSourceContext.Builder()
                .withMaterialJSONList(FileManager.class.getResourceAsStream("/materials.json"))
                .withEntityTypeJSONList(FileManager.class.getResourceAsStream("/entity_types.json"))
                .build();
        ValidationResult validationResult = Validation.modernValidate(json, schemaStream, schematronStream, sourceContext);
        if(validationResult.isValid()) {
            ObjectMapper objectMapper = new ObjectMapper();
            ChallengesSchema challengesSchema = null;
            try {
                challengesSchema = objectMapper.readValue(json, ChallengesSchema.class);

                Context context = new Context.Builder()
                        .withPlugin(plugin)
                        .withRuleResourceBundle(ResourceBundle.getBundle("rules", Locale.US, UTF8ResourceBundleControl.get()))
                        .withGoalResourceBundle(ResourceBundle.getBundle("goals", Locale.US, UTF8ResourceBundleControl.get()))
                        .withPunishmentResourceBundle(ResourceBundle.getBundle("punishments", Locale.US, UTF8ResourceBundleControl.get()))
                        .withCommandsResourceBundle(ResourceBundle.getBundle("commands", Locale.US, UTF8ResourceBundleControl.get()))
                        .withMiscResourceBundle(ResourceBundle.getBundle("misc", Locale.US, UTF8ResourceBundleControl.get()))
                        .withSchemaRoot(schemaRoot)
                        .withMaterialJSONList(Main.class.getResourceAsStream("/materials.json"))
                        .withEntityTypeJSONList(Main.class.getResourceAsStream("/entity_types.json"))
                        .withChallengeManager(new ChallengeManager())
                        .withRandom(new Random())
                        .build();
                context.challengeManager().setContext(context); // immediately set context so it is available in the manager
                context.challengeManager().setValid(true);
                context.challengeManager().setBossBarShower(new BossBarShower(context));
                ModelMapper.map2ModelClasses(context, challengesSchema);


                return context;
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
