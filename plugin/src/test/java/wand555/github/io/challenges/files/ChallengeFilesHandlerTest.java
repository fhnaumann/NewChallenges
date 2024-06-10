package wand555.github.io.challenges.files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ChallengeFilesHandlerTest {

    private ChallengeFilesHandler challengeFilesHandler;

    @TempDir
    Path tempFolderContainingChallenges;

    private Path firstChallenge;
    private Path secondChallenge;
    private Path invalidChallenge;
    private Path notAJson;

    @BeforeEach
    public void setUp() throws IOException {
        try {
            firstChallenge = tempFolderContainingChallenges.resolve("test-challenge-1.json");
            Files.write(firstChallenge,
                    """
                    {
                      "metadata": {
                        "builderMCVersion": "1.20.4",
                        "builderVersion": "0.0.1",
                        "lastModified": "",
                        "name": "test1",
                        "pluginMCVersion": "",
                        "pluginVersion": "",
                        "whenCreated": ""
                      }
                    }
                    """.getBytes());
            secondChallenge = tempFolderContainingChallenges.resolve("test-challenge-2.json");
            Files.write(secondChallenge,
                    """
                    {
                      "metadata": {
                        "builderMCVersion": "1.21",
                        "builderVersion": "0.0.5",
                        "lastModified": "",
                        "name": "test2",
                        "pluginMCVersion": "",
                        "pluginVersion": "",
                        "whenCreated": ""
                      }
                    }
                    """.getBytes());
            invalidChallenge = tempFolderContainingChallenges.resolve("invalid-challenge.json");
            Files.write(invalidChallenge,
                    """
                    {
                    }
                    """.getBytes());
            notAJson = tempFolderContainingChallenges.resolve("not-a-json.txt");
            Files.write(notAJson, "I am not JSON content!".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        challengeFilesHandler = new ChallengeFilesHandler(tempFolderContainingChallenges.toFile());
    }

    @Test
    public void testHandlerRecognizesValidChallengeJSON() {
        List<ChallengeFilesHandler.ChallengeLoadStatus> statuses = challengeFilesHandler.getChallengesInFolderStatus();
        List<File> asFiles = statuses.stream().map(ChallengeFilesHandler.ChallengeLoadStatus::file).toList();
        assertTrue(asFiles.contains(firstChallenge.toFile()));
        assertTrue(asFiles.contains(secondChallenge.toFile()));
    }

    @Test
    public void testHandlerIgnoresNonJSON() {
        List<ChallengeFilesHandler.ChallengeLoadStatus> statuses = challengeFilesHandler.getChallengesInFolderStatus();
        assertFalse(statuses.stream().map(ChallengeFilesHandler.ChallengeLoadStatus::file).toList().contains(notAJson.toFile()));
    }

    @Test
    public void testHandlerShowsJSONWithoutMetadataAsInvalid() {
        List<ChallengeFilesHandler.ChallengeLoadStatus> statuses = challengeFilesHandler.getChallengesInFolderStatus();
        ChallengeFilesHandler.ChallengeLoadStatus status = statuses.stream().filter(challengeLoadStatus -> challengeLoadStatus.file().equals(invalidChallenge.toFile())).findFirst().get();
        assertNull(status.challengeMetadata());
    }
}
