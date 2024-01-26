package wand555.github.io.challenges;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ResourceBundle;

public record Context(Challenges plugin, ResourceBundle bundle, JsonNode schemaRoot, ChallengeManager challengeManager) {
}
