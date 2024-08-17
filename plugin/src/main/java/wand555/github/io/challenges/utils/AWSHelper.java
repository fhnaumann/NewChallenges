package wand555.github.io.challenges.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import wand555.github.io.challenges.generated.ChallengeMetadata;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class AWSHelper {

    public static CompletableFuture<Void> uploadToS3(ChallengeMetadata metadata, File file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                HttpGet getSignedURL = new HttpGet("https://ehhkbf8pb0.execute-api.eu-central-1.amazonaws.com/uploads");
                getSignedURL.addHeader("challenge_ID", metadata.getChallengeID());
                try(CloseableHttpResponse signedURLResponse = httpClient.execute(getSignedURL)) {
                    JsonNode responseBody = new ObjectMapper().readTree(signedURLResponse.getEntity().getContent());
                    String challengeFileNameInS3 = responseBody.at("Key").asText();
                    String uploadURL = responseBody.at("uploadURL").asText();

                    HttpPut uploadChallengeToS3 = new HttpPut(uploadURL);
                    HttpEntity challengeFile = MultipartEntityBuilder.create()
                                                                     .addBinaryBody("file",
                                                                                    file,
                                                                                    ContentType.create(
                                                                                            "application/octet-stream"),
                                                                                    challengeFileNameInS3
                                                                     ).build();
                    uploadChallengeToS3.setEntity(challengeFile);
                    try(CloseableHttpResponse uploadResponse = httpClient.execute(uploadChallengeToS3)) {

                    }
                }
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }
}
