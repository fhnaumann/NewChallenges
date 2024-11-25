package wand555.github.io.challenges.live;

import wand555.github.io.challenges.generated.MCEventAlias;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface EventProvider extends PerChallenge {

    CompletableFuture<Void> sendEvent(int timestamp, MCEventAlias.EventType eventType, Object additionalData);

    default MCEventAlias constructMCEventFrom(String challengeID, int timestamp, MCEventAlias.EventType eventType, Object additionalData) {
        UUID uuid = UUID.randomUUID();
        setUUIDFromLastEvent(uuid);
        return new MCEventAlias("event", challengeID, additionalData, uuid.toString(), eventType, timestamp);
    }

    /**
     * Only exists so the integration tests can access the event UUID from the last event that was sent. The variable may
     * only be accessed within tests.
     *
     * @return UUID from last event
     */
    UUID getUUIDFromLastEvent();

    /**
     * Only exists so the integration tests can access the event UUID from the last event that was sent. The variable may
     * only be accessed within tests.
     *
     * @param uuidFromLastEvent
     *         UUID from last event
     */
    void setUUIDFromLastEvent(UUID uuidFromLastEvent);
}
