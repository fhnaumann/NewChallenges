package wand555.github.io.challenges.criteria.rules;

import wand555.github.io.challenges.Challenges;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.JSONConfigGroup;
import wand555.github.io.challenges.StatusInfo;
import wand555.github.io.challenges.criteria.Criteria;
import wand555.github.io.challenges.criteria.Loadable;
import wand555.github.io.challenges.generated.EnabledRules;

import java.util.ResourceBundle;

public abstract class Rule implements Criteria, JSONConfigGroup<EnabledRules>, StatusInfo, Loadable {

    protected final Context context;

    public Rule(Context context) {
        this.context = context;
    }
}
