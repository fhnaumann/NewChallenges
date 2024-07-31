package wand555.github.io.challenges.utils;

import java.util.HashMap;
import java.util.Map;

public class TimerUtil {


    private static final long ONE_SECOND = 1L;
    private static final long MINUTE_IN_SECONDS = 60L * ONE_SECOND;
    private static final long HOUR_IN_SECONDS = 60L * MINUTE_IN_SECONDS;
    private static final long DAY_IN_SECONDS = 24 * HOUR_IN_SECONDS;


    public static Map<TimeParts, String> format(long time) {
        Map<TimeParts, String> map = new HashMap<>();
        long remainingTime = time;
        if(remainingTime >= DAY_IN_SECONDS) {
            map.put(TimeParts.DAYS, Long.toString(remainingTime / DAY_IN_SECONDS));
            remainingTime %= DAY_IN_SECONDS;
        }
        if(remainingTime >= HOUR_IN_SECONDS) {
            map.put(TimeParts.HOURS, Long.toString(remainingTime / HOUR_IN_SECONDS));
            remainingTime %= HOUR_IN_SECONDS;
        }
        if(remainingTime >= MINUTE_IN_SECONDS) {
            map.put(TimeParts.MINUTES, Long.toString(remainingTime / MINUTE_IN_SECONDS));
            remainingTime %= MINUTE_IN_SECONDS;
        }
        map.put(TimeParts.SECONDS, Long.toString(remainingTime));

        return map;
    }

    public enum TimeParts {
        SECONDS,
        MINUTES,
        HOURS,
        DAYS
    }
}
