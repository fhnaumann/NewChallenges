package wand555.github.io.challenges;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wand555.github.io.challenges.Context;
import wand555.github.io.challenges.utils.TimerUtil;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class TimerUtilTest {


    @Test
    public void testFormat() {
        assertEquals(create(0, 0, 0, 2), TimerUtil.format(2));
        assertEquals(create(0, 0, 2, 0), TimerUtil.format(120));
        assertEquals(create(0, 0, 2, 32), TimerUtil.format(152));
        assertEquals(create(0, 1, 2, 32), TimerUtil.format(3752));
        assertEquals(create(5, 1, 2, 32), TimerUtil.format(435752));
    }

    @Test
    public void testSecondsMinutesOverlap() {
        assertEquals(create(0, 0, 0, 0), TimerUtil.format(0));
        assertEquals(create(0, 0, 0, 59), TimerUtil.format(59));
        assertEquals(create(0, 0, 1, 0), TimerUtil.format(60));
        assertNotEquals(create(0, 0, 0, 60), TimerUtil.format(60));
    }

    @Test
    public void testMinutesHoursOverlap() {
        assertEquals(create(0, 0, 59, 59), TimerUtil.format(3599));
        assertEquals(create(0, 1, 0, 0), TimerUtil.format(3600));
        assertNotEquals(create(0, 0, 60, 0), TimerUtil.format(3600));
    }

    @Test
    public void testHoursDaysOverlap() {
        assertEquals(create(0, 23, 59, 59), TimerUtil.format(86399));
        assertEquals(create(1, 0, 0, 0), TimerUtil.format(86400));
        assertNotEquals(create(0, 24, 0, 0), TimerUtil.format(86400));
    }

    private Map<TimerUtil.TimeParts, String> create(long days, long hours, long minutes, long seconds) {
        Map<TimerUtil.TimeParts, String> map = new HashMap<>();
        if(days != 0) {
            map.put(TimerUtil.TimeParts.DAYS, Long.toString(days));
        }
        if(hours != 0) {
            map.put(TimerUtil.TimeParts.HOURS, Long.toString(hours));
        }
        if(minutes != 0) {
            map.put(TimerUtil.TimeParts.MINUTES, Long.toString(minutes));
        }
        map.put(TimerUtil.TimeParts.SECONDS, Long.toString(seconds));
        return map;
    }
}
