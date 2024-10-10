package wand555.github.io.challenges.live;

import wand555.github.io.challenges.generated.ChallengeMetadata;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public interface ChallengeUploader {

    CompletableFuture<Void> uploadChallenge(ChallengeMetadata metadata, File file);
}
