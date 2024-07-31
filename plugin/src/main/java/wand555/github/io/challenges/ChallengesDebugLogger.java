package wand555.github.io.challenges;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ChallengesDebugLogger extends Logger {

    private static boolean DEBUG = ConfigValues.DEBUG.defaultValue();

    private final String logPrefix;

    public static void initLogging(Challenges plugin) {
        DEBUG = ConfigValues.DEBUG.getValueOrDefault(plugin);
    }

    public ChallengesDebugLogger(Class<?> clazz) {
        super("Challenges", null);
        logPrefix = "%s: ".formatted(clazz.getSimpleName());
        setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord record) {
        if(getParent() == null) {
            try {
                setParent(JavaPlugin.getPlugin(Challenges.class).getServer().getLogger());
            } catch(IllegalArgumentException ignored) {
                // plugin won't be found during tests, but we don't care about log messages during test runs
            }
        }
        // don't log if logging is less important than info and the debug flag is explicitly turned off via the configuration file
        if(!DEBUG && record.getLevel().intValue() < Level.INFO.intValue()) {
            return;
        }
        if(DEBUG) {
            // Bukkit forces info level at least
            record.setLevel(Level.INFO);
            record.setMessage(logPrefix + record.getMessage());
        } else {
            record.setMessage(record.getMessage());
        }
        super.log(record);
    }

    public static ChallengesDebugLogger getLogger(Class<?> clazz) {
        return new ChallengesDebugLogger(clazz);
    }
}
