package wand555.github.io.challenges.criteria.settings;

import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.JSONConfigGroup;
import wand555.github.io.challenges.criteria.Criteria;
import wand555.github.io.challenges.generated.SettingsConfig;

public abstract class BaseSetting implements Criteria, JSONConfigGroup<SettingsConfig> {

    protected final Context context;

    public BaseSetting(Context context) {
        this.context = context;
    }
}
