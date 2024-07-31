package wand555.github.io.challenges.utils;

import wand555.github.io.challenges.Challenges;

import javax.validation.constraints.NotNull;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class ResourceBundleHelper {

    public static String getFromBundle(@NotNull Challenges plugin, ResourceBundle bundle, String key) {
        try {
            return bundle.getString(key);
        } catch(MissingResourceException e) {
            // log to ingame
            String msg = String.format("Key '%s' is not found in resource bundle '%s'.",
                                       key,
                                       bundle.getBaseBundleName()
            );
            plugin.getServer().getLogger().warning(msg);
            //plugin.getServer().getLogger().log(Level.WARNING, msg, e);
        } catch(ClassCastException e) {
            // log to ingame
            String msg = String.format("Key '%s' is not a valid string in resource bundle '%s'.",
                                       key,
                                       bundle.getBaseBundleName()
            );
            plugin.getServer().getLogger().warning(msg);
            //plugin.getServer().getLogger().log(Level.WARNING, msg, e);
        }
        // return raw key so it is visually noticeable
        return key;
    }
}
