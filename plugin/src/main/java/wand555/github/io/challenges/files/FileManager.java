package wand555.github.io.challenges.files;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.apache.commons.lang3.RandomStringUtils;
import wand555.github.io.challenges.*;
import wand555.github.io.challenges.generated.*;
import wand555.github.io.challenges.mapping.ModelMapper;
import wand555.github.io.challenges.offline_temp.OfflineTempData;
import wand555.github.io.challenges.teams.Team;
import wand555.github.io.challenges.validation.BossBarShower;
import wand555.github.io.challenges.validation.Validation;
import wand555.github.io.challenges.validation.ValidationResult;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class FileManager {


    public static void writeToFile(ChallengeManager challengeManager, Writer writer) {
        EnabledRules enabledRulesConfig = new EnabledRules();
        challengeManager.getRules().forEach(rule -> rule.addToGeneratedConfig(enabledRulesConfig));
        PunishmentsConfig globalPunishmentsConfig = new PunishmentsConfig();
        challengeManager.getGlobalPunishments().forEach(punishment -> punishment.addToGeneratedConfig(
                globalPunishmentsConfig));
        RulesConfig rulesConfig = new RulesConfig(globalPunishmentsConfig, enabledRulesConfig);

        GoalsConfig goalsConfig = new GoalsConfig();
        challengeManager.getGoals().forEach(goal -> goal.addToGeneratedConfig(goalsConfig));

        SettingsConfig settingsConfig = new SettingsConfig();
        challengeManager.getSettings().forEach(baseSetting -> baseSetting.addToGeneratedConfig(settingsConfig));

        // casting time from long to int could be problematic...
        // on the other hand ~24000 days fit into an int, no one will reach that (hopefully)
        Model model = new Model(
                null, // Only exists so the Live Interfaces are available ("Bug" in jsonschema2pojo generator)
                Team.getGlobalCurrentOrder(),
                                goalsConfig,
                                challengeManager.getChallengeMetadata(),
                                null, // unsupported for now
                                rulesConfig,
                                settingsConfig,
                                challengeManager.getTeams().stream().map(Team::toGeneratedJSONClass).toList(),
                                (int)challengeManager.getTime()
        );
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, model);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompletableFuture<Context> readFromFile(File file, final Context context, ProgressListener progressListener) throws LoadValidationException {
        return CompletableFuture.supplyAsync(() -> {
            String json = null;
            try {
                json = Files.readString(file.toPath());
                progressListener.onProgress(0.1);
            } catch(IOException e) {
                throw new LoadValidationException(new ValidationResult(false, List.of(), e));
            }
            InputStream schemaStream = Main.class.getResourceAsStream("/challenges_schema.json");
            InputStream schematronStream = Main.class.getResourceAsStream("/constraints.sch");

            JsonNode schemaRoot = null;

            try {
                schemaRoot = new ObjectMapper().readTree(Main.class.getResourceAsStream("/challenges_schema.json"));
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
            DataSourceContext sourceContext = new DataSourceContext.Builder()
                    .withMaterialJSONList(FileManager.class.getResourceAsStream("/materials.json"))
                    .withEntityTypeJSONList(FileManager.class.getResourceAsStream("/entity_types.json"))
                    .withDeathMessageList(FileManager.class.getResourceAsStream("/death_messages_as_data_source_JSON.json"))
                    .withCraftingTypeJSONList(FileManager.class.getResourceAsStream("/craftables.json"))
                    .build();
            ValidationResult validationResult = Validation.modernValidate(json,
                                                                          schemaStream,
                                                                          schematronStream,
                                                                          sourceContext,
                                                                          progressListener
            );
            progressListener.onProgress(0.9);
            if(validationResult.isValid()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Model challengesSchema = null;
                try {
                    challengesSchema = objectMapper.readValue(json, Model.class);

                    // Change the challengeID in case the user has downloaded the file from the docs
                    assignUniqueChallengeIDIfDownloadedFromDocs(challengesSchema.getMetadata());

                    Context newContext = new Context.Builder()
                            .withPlugin(context.plugin())
                            .withRuleResourceBundle(ResourceBundle.getBundle("rules",
                                                                             Locale.US,
                                                                             UTF8ResourceBundleControl.get()
                            ))
                            .withGoalResourceBundle(ResourceBundle.getBundle("goals",
                                                                             Locale.US,
                                                                             UTF8ResourceBundleControl.get()
                            ))
                            .withSettingsResourceBundle(ResourceBundle.getBundle("settings",
                                                                                 Locale.US,
                                                                                 UTF8ResourceBundleControl.get()
                            ))
                            .withPunishmentResourceBundle(ResourceBundle.getBundle("punishments",
                                                                                   Locale.US,
                                                                                   UTF8ResourceBundleControl.get()
                            ))
                            .withCommandsResourceBundle(ResourceBundle.getBundle("commands",
                                                                                 Locale.US,
                                                                                 UTF8ResourceBundleControl.get()
                            ))
                            .withMiscResourceBundle(ResourceBundle.getBundle("misc",
                                                                             Locale.US,
                                                                             UTF8ResourceBundleControl.get()
                            ))
                            .withSchemaRoot(schemaRoot)
                            .withMaterialJSONList(Main.class.getResourceAsStream("/materials.json"))
                            .withEntityTypeJSONList(Main.class.getResourceAsStream("/entity_types.json"))
                            .withDeathMessageList(Main.class.getResourceAsStream(
                                    "/death_messages_as_data_source_JSON.json"))
                            .withCraftingTypeJSONList(Main.class.getResourceAsStream("/craftables.json"))
                            .withChallengeManager(new ChallengeManager())
                            .withRandom(new Random())
                            .withOfflineTempData(new OfflineTempData(context.plugin()))
                            .build();
                    context.challengeManager().setContext(context); // immediately set context so it is available in the manager
                    context.challengeManager().setValid(true);
                    context.challengeManager().setBossBarShower(new BossBarShower(context));

                    Team.initAllTeam(context, challengesSchema.getCurrentOrder());
                    ModelMapper.map2ModelClasses(context, challengesSchema);
                    progressListener.onProgress(1d);

                    return newContext;
                } catch(Exception e) {
                    // should never happen because it was validated first.

                    // Case where it may be thrown: A code (material, entityType, ...) is in the source json files, but does
                    // not exist as an enum at runtime for the current server version. This may occur in two scenarios:
                    // 1. The user is using a server version that is not supported.
                    // 2. I forgot to update the source jsons when changing the supported server version.
                    throw new RuntimeException(e);
                }
            } else {
                throw new LoadValidationException(validationResult);
            }
        });
    }

    private static void assignUniqueChallengeIDIfDownloadedFromDocs(ChallengeMetadata metadata) {
        if(metadata.getChallengeID().equals("from-examples")) {
            metadata.setChallengeID(generateChallengeID());
        }
    }

    private static String generateChallengeID() {
        int length = 10;
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
