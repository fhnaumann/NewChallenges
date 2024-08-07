package wand555.github.io.challenges;

import com.fasterxml.jackson.databind.JsonNode;
import wand555.github.io.challenges.offline_temp.OfflineTempData;

import java.io.InputStream;
import java.util.Random;
import java.util.ResourceBundle;

public record Context(
        Challenges plugin, ResourceBundleContext resourceBundleContext, DataSourceContext dataSourceContext,
        JsonNode schemaRoot, ChallengeManager challengeManager, Random random, OfflineTempData offlineTempData
) {

    public static final class Builder {
        private Challenges plugin;
        private ResourceBundleContext.Builder resourceBundleContextBuilder;
        private DataSourceContext.Builder dataSourceContextBuilder;
        private JsonNode schemaRoot;
        private ChallengeManager manager;

        private Random random;
        private OfflineTempData offlineTempData;

        public Builder() {
            this.resourceBundleContextBuilder = new ResourceBundleContext.Builder();
            this.dataSourceContextBuilder = new DataSourceContext.Builder();
        }

        public Builder withPlugin(Challenges plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder withRuleResourceBundle(ResourceBundle ruleResourceBundle) {
            resourceBundleContextBuilder.withRuleResourceBundle(ruleResourceBundle);
            return this;
        }

        public Builder withGoalResourceBundle(ResourceBundle goalResourceBundle) {
            resourceBundleContextBuilder.withGoalResourceBundle(goalResourceBundle);
            return this;
        }

        public Builder withSettingsResourceBundle(ResourceBundle settingsResourceBundle) {
            resourceBundleContextBuilder.withSettingsResourceBundle(settingsResourceBundle);
            return this;
        }

        public Builder withPunishmentResourceBundle(ResourceBundle punishmentResourceBundle) {
            resourceBundleContextBuilder.withPunishmentResourceBundle(punishmentResourceBundle);
            return this;
        }

        public Builder withCommandsResourceBundle(ResourceBundle commandsResourceBundle) {
            resourceBundleContextBuilder.withCommandsResourceBundle(commandsResourceBundle);
            return this;
        }

        public Builder withMiscResourceBundle(ResourceBundle miscResourceBundle) {
            resourceBundleContextBuilder.withMiscResourceBundle(miscResourceBundle);
            return this;
        }

        public Builder withMaterialJSONList(InputStream materialJSONInputStream) {
            dataSourceContextBuilder.withMaterialJSONList(materialJSONInputStream);
            return this;
        }

        public Builder withEntityTypeJSONList(InputStream entityTypeJSONInputStream) {
            dataSourceContextBuilder.withEntityTypeJSONList(entityTypeJSONInputStream);
            return this;
        }

        public Builder withDeathMessageList(InputStream deathMessageListInputStream) {
            dataSourceContextBuilder.withDeathMessageList(deathMessageListInputStream);
            return this;
        }

        public Builder withCraftingTypeJSONList(InputStream craftingTypeJSONListInputStream) {
            dataSourceContextBuilder.withCraftingTypeJSONList(craftingTypeJSONListInputStream);
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

        public Builder withRandom(Random random) {
            this.random = random;
            return this;
        }

        public Builder withOfflineTempData(OfflineTempData offlineTempData) {
            this.offlineTempData = offlineTempData;
            return this;
        }

        public Context build() {
            return new Context(plugin,
                               resourceBundleContextBuilder.build(),
                               dataSourceContextBuilder.build(),
                               schemaRoot,
                               manager,
                               random,
                               offlineTempData
            );
        }
    }

}
