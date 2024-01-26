package wand555.github.io.challenges.rules;

import org.bukkit.Material;
import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.punishments.Punishment;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class NoBlockPlaceRule extends PunishableRule {

    private final Set<Material> exemptions;

    public NoBlockPlaceRule(Challenges plugin, ResourceBundle ruleResourceBundle, Set<Material> exemptions) {
        this(plugin, ruleResourceBundle, List.of(), exemptions);
    }

    public NoBlockPlaceRule(Challenges plugin, ResourceBundle ruleResourceBundle, List<Punishment> punishments, Set<Material> exemptions) {
        super(plugin, ruleResourceBundle, punishments);
        this.exemptions = exemptions;
    }
}
