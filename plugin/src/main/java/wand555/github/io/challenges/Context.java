package wand555.github.io.challenges;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ResourceBundle;

public record Context(Challenges plugin, ResourceBundleContext resourceBundleContext, JsonNode schemaRoot, ChallengeManager challengeManager) {

    public static final class Builder {
        private Challenges plugin;
        private ResourceBundleContext.Builder resourceBundleContextBuilder;
        private JsonNode schemaRoot;
        private ChallengeManager manager;

        public Builder() {
            this.resourceBundleContextBuilder = new ResourceBundleContext.Builder();
        }

        public Builder withPlugin(Challenges plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder withRuleResourceBundle(ResourceBundle ruleResourceBundle) {
            resourceBundleContextBuilder.withRuleResourceBundle(ruleResourceBundle);
            return this;
        }

        public Builder withPunishmentResourceBundle(ResourceBundle punishmentResourceBundle) {
            resourceBundleContextBuilder.withPunishmentResourceBundle(punishmentResourceBundle);
            return this;
        }

        public Builder withSchemaRoot(JsonNode schemaRoot) {
            this.schemaRoot = schemaRoot;
            return this;
        }

        public Builder withChallengeManager(ChallengeManager challengeManager) {
            this.manager = challengeManager;
            return this;
        }

        public Context build() {
            return new Context(plugin, resourceBundleContextBuilder.build(), schemaRoot, manager);
        }
    }

}
