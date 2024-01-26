package wand555.github.io.challenges.rules;

import wand555.github.io.challenges.Challenges;

import java.util.ResourceBundle;

public abstract class Rule {

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
