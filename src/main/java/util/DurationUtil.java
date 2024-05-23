package util;

import java.time.Duration;

public class DurationUtil {
    public static String formatDuration(Duration duration) {
        return String.format("%02d:%02d:%02d",
                duration.toHoursPart(),
                duration.toMinutesPart(),
                duration.toSecondsPart());
    }

}
