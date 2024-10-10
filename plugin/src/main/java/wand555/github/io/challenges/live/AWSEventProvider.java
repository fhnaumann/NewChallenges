package wand555.github.io.challenges.live;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.generated.MCEventAlias;

import java.net.URI;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

public class AWSEventProvider implements EventProvider {

    private static final Logger logger = ChallengesDebugLogger.getLogger(AWSEventProvider.class);

    private final static String WEBSOCKET_URL = "wss://oaxuru4o1c.execute-api.eu-central-1.amazonaws.com/%s/?client_type=mc-server".formatted(System.getProperty("stage", "development"));
    private WebSocket webSocket;
    private String challengeIDUsedInWebSocket = "";
    private final ObjectMapper objectMapper;

    public AWSEventProvider() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void sendEvent( int timestamp, MCEventAlias.EventType eventType, Object additionalData) {
        Preconditions.checkNotNull(webSocket, "WebSocket client is null!");
        Preconditions.checkArgument(!challengeIDUsedInWebSocket.isBlank(), "ChallengeID in WebSocket is empty!");
        MCEventAlias mcEventAlias = EventProvider.constructMCEventFrom(challengeIDUsedInWebSocket, timestamp, eventType, additionalData);
        try {
            String serializedMCEvent = objectMapper.writeValueAsString(mcEventAlias);
            webSocket.sendText(serializedMCEvent, true)
                     .thenRun(() -> logger.fine("Sent message: %s".formatted(serializedMCEvent)))
                    .exceptionally(throwable -> {
                        logger.severe("Error while sending message: %s".formatted(throwable.toString()));
                        return null;
                    });
        } catch(JsonProcessingException e) {
            logger.severe("Error while serializing the event: %s".formatted(mcEventAlias));
            logger.severe(e.toString());
        }
    }

    @Override
    public String getChallengeID() {
        return challengeIDUsedInWebSocket;
    }

    @Override
    public void setChallengeID(String challengeID) {
        challengeIDUsedInWebSocket = challengeID;
        if(webSocket != null) {
            CompletableFuture.runAsync(() -> {
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing this and reopening with different challengeID: %s".formatted(challengeID)).join();
                openNewConnection(challengeID);
            });
        }
        else {
            openNewConnection(challengeID);
        }
    }

    private void openNewConnection(String challengeID) {
        CompletableFuture.runAsync(() -> {
            String formattedURL = "%s&challenge_ID=%s".formatted(WEBSOCKET_URL, challengeID);
            logger.fine("Opening connection to %s".formatted(formattedURL));
            webSocket = LiveService.getClient().newWebSocketBuilder()
                                   .buildAsync(URI.create(formattedURL), new WebsocketListener())
                                   .join();
        });
    }

    private static class WebsocketListener implements WebSocket.Listener {
        @Override
        public void onOpen(WebSocket webSocket) {
            logger.fine("WebSocket connection opened.");
            WebSocket.Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            logger.fine("Received message: %s".formatted(data));
            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            logger.severe("Received error: %s".formatted(error.toString()));
            WebSocket.Listener.super.onError(webSocket, error);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            logger.fine("Closed connection");
            return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
        }
    }
}
