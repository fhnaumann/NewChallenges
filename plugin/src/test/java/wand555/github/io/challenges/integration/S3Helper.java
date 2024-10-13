package wand555.github.io.challenges.integration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import wand555.github.io.challenges.generated.Model;

import static org.junit.jupiter.api.Assertions.*;

public class S3Helper {

    private static final String BUCKET_NAME = "existing-challenges-testing";

    private static final S3Client s3Client = S3Client.builder()
                                                     .credentialsProvider(DefaultCredentialsProvider.create())
                                                     .region(Region.EU_CENTRAL_1)
                                                     .build();

    public static void assertFileExists(String filename) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(filename)
                    .build();
            HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
            // Assert true if no exception was thrown
            assertTrue(true);
        } catch(S3Exception e) {
            if(e.statusCode() == 404) {
                fail(STR."'\{filename}' not in S3 '\{BUCKET_NAME}' bucket.");
            }
            fail("Error accessing S3 bucket.", e);
        }
    }

    public static void assertFileContent(Model expected) {

    }
}
