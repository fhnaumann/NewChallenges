package wand555.github.io.challenges.live;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import wand555.github.io.challenges.ChallengesDebugLogger;
import wand555.github.io.challenges.generated.MCEventAlias;

import java.net.URI;
import java.net.http.WebSocket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

public class AWSEventProvider implements EventProvider {

    private static final Logger logger = ChallengesDebugLogger.getLogger(AWSEventProvider.class);

    private final static String WEBSOCKET_URL = "wss://oaxuru4o1c.execute-api.eu-central-1.amazonaws.com/%s/?client_type=mc-server".formatted(System.getProperty("stage", "development"));
    private WebSocket webSocket;
    private String challengeIDUsedInWebSocket = "";
    private final ObjectMapper objectMapper;

    /*
    Only exists so the integration tests can access the event UUID from the last event that was sent. The variable may
    only be accessed within tests.
     */
    private UUID uuidFromLastEvent;

    /*
    Only exists for integration tests, so they can wait until the message is sent.
     */
    public CompletableFuture<Void> lastSentEventFuture;

    public AWSEventProvider() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public CompletableFuture<Void> sendEvent( int timestamp, MCEventAlias.EventType eventType, Object additionalData) {
        lastSentEventFuture = CompletableFuture.runAsync(() -> {
            Preconditions.checkNotNull(webSocket, "WebSocket client is null!");
            Preconditions.checkArgument(!challengeIDUsedInWebSocket.isBlank(), "ChallengeID in WebSocket is empty!");
            MCEventAlias mcEventAlias = constructMCEventFrom(challengeIDUsedInWebSocket, timestamp, eventType, additionalData);
            try {
                String serializedMCEvent = objectMapper.writeValueAsString(mcEventAlias);
                webSocket.sendText(serializedMCEvent, true)
                         .exceptionally(throwable -> {
                             logger.severe("Error while sending message: %s".formatted(throwable.toString()));
                             return null;
                         }).join();
                System.out.println("SENT EVENT");
                logger.fine("Sent message: %s".formatted(serializedMCEvent));
            } catch(JsonProcessingException e) {
                logger.severe("Error while serializing the event: %s".formatted(mcEventAlias));
                logger.severe(e.toString());
            }
        });
        return lastSentEventFuture;
    }

    @Override
    public UUID getUUIDFromLastEvent() {
        return uuidFromLastEvent;
    }

    @Override
    public void setUUIDFromLastEvent(UUID uuidFromLastEvent) {
        this.uuidFromLastEvent = uuidFromLastEvent;
    }

    @Override
    public String getChallengeID() {
        return challengeIDUsedInWebSocket;
    }

    @Override
    public CompletableFuture<WebSocket> setChallengeID(String challengeID) {
        challengeIDUsedInWebSocket = challengeID;
        if(webSocket != null) {
            return webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing this and reopening with different challengeID: %s".formatted(challengeID))
                    .thenCompose((ignored) -> openNewConnection(challengeID));
        }
        else {
            return openNewConnection(challengeID);
        }
    }

    private CompletableFuture<WebSocket> openNewConnection(String challengeID) {
        return CompletableFuture.supplyAsync(() -> {
            String formattedURL = "%s&challenge_ID=%s".formatted(WEBSOCKET_URL, challengeID);
            logger.fine("Opening connection to %s".formatted(formattedURL));
            webSocket = LiveService.getClient().newWebSocketBuilder()
                                   .buildAsync(URI.create(formattedURL), new WebsocketListener())
                                   .join();
            return webSocket;
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
