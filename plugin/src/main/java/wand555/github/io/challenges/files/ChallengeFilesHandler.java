package wand555.github.io.challenges.files;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import wand555.github.io.challenges.offline_temp.OfflineTempData;
import wand555.github.io.challenges.generated.ChallengeMetadata;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class ChallengeFilesHandler {

    private final OfflineTempData offlineTempData;
    private final File folderContainingChallenges;
    private final ObjectMapper objectMapper;

    private String fileNameBeingPlayed;

    public ChallengeFilesHandler(OfflineTempData offlineTempData, File folderContainingChallenges) throws IOException {
        this.offlineTempData = offlineTempData;
        this.folderContainingChallenges = folderContainingChallenges;
        if(!this.folderContainingChallenges.exists()) {
            this.folderContainingChallenges.mkdirs();
            Files.createFile(this.folderContainingChallenges.toPath().resolve("MOVE YOUR CHALLENGES IN THIS FOLDER.txt"));
        }
        this.objectMapper = new ObjectMapper();
        this.fileNameBeingPlayed = offlineTempData.get("fileNameBeingPlayed", String.class);
    }

    private List<File> getJSONFilesInFolder() {
        return List.of(Objects.requireNonNull(folderContainingChallenges.listFiles(pathname -> pathname.isFile() && isJSONFile(
                pathname))));
    }

    public List<ChallengeLoadStatus> getChallengesInFolderStatus() {
        JsonFactory jsonFactory = new JsonFactory();
        return getJSONFilesInFolder().stream()
                                     .map(file -> file2LoadStatus(file, jsonFactory))
                                     .filter(challengeLoadStatus -> challengeLoadStatus.challengeMetadata() != null)
                                     .toList();
    }

    private ChallengeLoadStatus file2LoadStatus(File file, JsonFactory jsonFactory) {
        try(JsonParser jsonParser = jsonFactory.createParser(file)) {
            // Move to the root element
            JsonToken jsonToken = jsonParser.nextToken();

            if(JsonToken.START_OBJECT.equals(jsonToken)) {
                while(!jsonParser.isClosed()) {
                    jsonToken = jsonParser.nextToken();

                    if(JsonToken.FIELD_NAME.equals(jsonToken)) {
                        String fieldName = jsonParser.getCurrentName();

                        // Check if the current field is the target key
                        if("metadata".equals(fieldName)) {
                            // Move to the value of the target key
                            jsonToken = jsonParser.nextToken();

                            // Parse the value into a JsonNode
                            ChallengeMetadata metadata = objectMapper.readValue(jsonParser, ChallengeMetadata.class);
                            return new ChallengeLoadStatus(file, metadata);
                        }
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
            return new ChallengeLoadStatus(file, null);
        }
        return new ChallengeLoadStatus(file, null);
    }

    private boolean isJSONFile(File file) {
        int dotIndex = file.getName().lastIndexOf(".");
        return dotIndex != -1 && file.getName().substring(dotIndex + 1).equals("json");
    }

    public String getFileNameBeingPlayed() {
        return fileNameBeingPlayed;
    }

    public void setFileNameBeingPlayed(String fileNameBeingPlayed) {
        this.fileNameBeingPlayed = fileNameBeingPlayed;
        offlineTempData.addAndSave("fileNameBeingPlayed", this.fileNameBeingPlayed);
    }

    public @Nullable File getFileBeingPlayed() {
        return getFileNameBeingPlayed() != null ? new File(folderContainingChallenges, fileNameBeingPlayed) : null;
    }

    public File getFolderContainingChallenges() {
        return folderContainingChallenges;
    }

    public record ChallengeLoadStatus(@NotNull File file, @Nullable ChallengeMetadata challengeMetadata) {}
}
