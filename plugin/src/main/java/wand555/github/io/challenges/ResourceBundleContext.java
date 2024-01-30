package wand555.github.io.challenges;

import wand555.github.io.challenges.punishments.Punishment;

import java.util.ResourceBundle;

public record ResourceBundleContext(ResourceBundle ruleResourceBundle, ResourceBundle goalResourceBundle, ResourceBundle punishmentResourceBundle) {

    public static final class Builder {
        private ResourceBundle ruleResourceBundle, goalResourceBundle, punishmentResourceBundle;

        public Builder() {}

        public Builder withRuleResourceBundle(ResourceBundle ruleResourceBundle) {
            this.ruleResourceBundle = ruleResourceBundle;
            return this;
        }

        public Builder withGoalResourceBundle(ResourceBundle goalResourceBundle) {
            this.goalResourceBundle = goalResourceBundle;
            return this;
        }

        public Builder withPunishmentResourceBundle(ResourceBundle punishmentResourceBundle) {
            this.punishmentResourceBundle = punishmentResourceBundle;
            return this;
        }

        public ResourceBundleContext build() {
            return new ResourceBundleContext(ruleResourceBundle, goalResourceBundle, punishmentResourceBundle);
        }

    }
}
