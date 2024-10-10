package wand555.github.io.challenges.live;

import java.net.http.HttpClient;

public record LiveService(ChallengeUploader challengeUploader, EventProvider eventProvider) {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static HttpClient getClient() {
        return client;
    }
}
