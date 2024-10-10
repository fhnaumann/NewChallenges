package wand555.github.io.challenges.live;

import wand555.github.io.challenges.generated.MCEventAlias;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface EventProvider extends PerChallenge {

    void sendEvent(int timestamp, MCEventAlias.EventType eventType, Object additionalData);

    static MCEventAlias constructMCEventFrom(String challengeID, int timestamp, MCEventAlias.EventType eventType, Object additionalData) {
        return new MCEventAlias(
                "event",
                challengeID,
                additionalData,
                UUID.randomUUID().toString(),
                eventType,
                timestamp
        );
    }
}
