package wand555.github.io.challenges.live;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.generated.ChallengeMetadata;
import wand555.github.io.challenges.generated.Model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class S3ChallengeUploader implements ChallengeUploader {

    public static Logger logger = ChallengesDebugLogger.getLogger(S3ChallengeUploader.class);

    @Override
    public CompletableFuture<Void> uploadChallenge(ChallengeMetadata metadata, File file) {
        return CompletableFuture.supplyAsync(() -> {
            try(CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
                HttpGet getSignedURL = new HttpGet("https://ehhkbf8pb0.execute-api.eu-central-1.amazonaws.com/%s/uploads?challenge_ID=%s".formatted(System.getProperty("stage", "development"), metadata.getChallengeID()));
                try(CloseableHttpResponse signedURLResponse = httpClient.execute(getSignedURL)) {
                    JsonNode responseBody = new ObjectMapper().readTree(signedURLResponse.getEntity().getContent());
                    String challengeFileNameInS3 = responseBody.get("Key").asText();
                    String uploadURL = responseBody.get("uploadURL").asText();

                    HttpPut uploadChallengeToS3 = new HttpPut(uploadURL);
                    ObjectMapper objectMapper = new ObjectMapper();
                    String modelAsJSONString = objectMapper.writeValueAsString(objectMapper.readTree(file));
                    StringEntity stringEntity = new StringEntity(modelAsJSONString, ContentType.APPLICATION_JSON);
                    uploadChallengeToS3.setEntity(stringEntity);
                    try(CloseableHttpResponse uploadResponse = httpClient.execute(uploadChallengeToS3)) {
                        logger.fine(uploadResponse.toString());
                        return null;
                    }
                } catch(IOException e) {
                    throw new RuntimeException(e);
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
