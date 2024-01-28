package wand555.github.io.challenges.rules;

import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.JSONConfigGroup;
import wand555.github.io.challenges.generated.EnabledRules;

import java.util.ResourceBundle;

public abstract class Rule implements JSONConfigGroup<EnabledRules> {

    protected final Challenges plugin;
    protected final ResourceBundle rulesResourceBundle;

    public Rule(Challenges plugin, ResourceBundle rulesResourceBundle) {
        this.plugin = plugin;
        this.rulesResourceBundle = rulesResourceBundle;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "plugin=" + plugin +
                ", rulesResourceBundle=" + rulesResourceBundle +
                '}';
    }
}
