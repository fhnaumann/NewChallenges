package wand555.github.io.challenges.live;

import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;

public interface PerChallenge {

    String getChallengeID();
    CompletableFuture<WebSocket> setChallengeID(String challengeID);
}
