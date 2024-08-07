package wand555.github.io.challenges.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import wand555.github.io.challenges.DataSourceContext;
import wand555.github.io.challenges.mapping.*;
import wand555.github.io.challenges.mapping.DeathMessage;

import java.io.*;
import java.util.List;

public class OnlyValidation {

    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();


        File folder = new File(args[0]);
        File[] children = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if(children == null) {
            throw new RuntimeException("Folder has no children!");
        }

        JsonArray validationData = new JsonArray();
        for(File child : children) {
            InputStream jsonSchemaStream = OnlyValidation.class.getResourceAsStream("/challenges_schema.json");
            InputStream schematronStream = OnlyValidation.class.getResourceAsStream("/constraints.sch");

            List<MaterialJSON> materialJSONS = objectMapper.readValue(OnlyValidation.class.getResourceAsStream(
                    "/materials.json"), MaterialDataSource.class).getData();
            List<EntityTypeJSON> entityTypeJSONS = objectMapper.readValue(OnlyValidation.class.getResourceAsStream(
                    "/entity_types.json"), EntityTypeDataSource.class).getData();
            List<DeathMessage> deathMessageList = objectMapper.readValue(OnlyValidation.class.getResourceAsStream(
                    "/death_messages_as_data_source_JSON.json"), DeathMessageDataSource.class).getData();
            List<CraftingTypeJSON> craftingTypeJSONList = objectMapper.readValue(OnlyValidation.class.getResourceAsStream(
                    "/craftables.json"), CraftingTypeDataSource.class).getData();
            DataSourceContext dataSourceContext = new DataSourceContext(materialJSONS,
                                                                        entityTypeJSONS,
                                                                        deathMessageList,
                                                                        craftingTypeJSONList
            );

            File jsonSourcePath = new File(child.getPath());
            String json = objectMapper.writeValueAsString(objectMapper.readValue(jsonSourcePath, Object.class));
            ValidationResult result = Validation.modernValidate(json,
                                                                jsonSchemaStream,
                                                                schematronStream,
                                                                dataSourceContext
            );
            JsonObject entry = new JsonObject();
            entry.addProperty("filename", child.getName());
            entry.addProperty("valid", result.isValid());
            entry.addProperty("message", result.asFormattedString());
            validationData.add(entry);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File jsonResult = new File("results.json");
        try(Writer writer = new FileWriter(jsonResult)) {
            String json = gson.toJson(validationData);
            writer.write(json);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
