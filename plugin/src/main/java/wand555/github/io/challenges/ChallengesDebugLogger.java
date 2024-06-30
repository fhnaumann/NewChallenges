package wand555.github.io.challenges;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

// deprecated because pretty much nothing works with it. Logging in spigot/paper is a mess
// and the static JavaPlugin access messes up MockBukkit tests.
@Deprecated()
public class ChallengesDebugLogger extends Logger {

    // TODO: load value from (static) config helper class
    private static final boolean DEBUG = true;

    private final String logPrefix;

    public ChallengesDebugLogger(Class<?> clazz) {
        super("Challenges", null);
        logPrefix = "%s: ".formatted(clazz.getSimpleName());
        //setParent(JavaPlugin.getPlugin(Challenges.class).getServer().getLogger());
        setLevel(Level.ALL);
    }

    @Override
    public void log(LogRecord record) {
        // don't log if logging is less important than info and the debug flag is explicitly turned off via the configuration file
        if(!DEBUG && record.getLevel().intValue() < Level.INFO.intValue()) {
            return;
        }
        if(DEBUG) {
            // Bukkit forces info level at least
            record.setLevel(Level.INFO);
            record.setMessage(logPrefix + record.getMessage());
        }
        else {
            record.setMessage(record.getMessage());
        }
        super.log(record);
    }

    public static ChallengesDebugLogger getLogger(Class<?> clazz) {
        return new ChallengesDebugLogger(clazz);
    }
}
