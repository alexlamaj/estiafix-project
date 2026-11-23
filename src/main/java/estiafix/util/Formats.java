package estiafix.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Formats {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd/MM");

    private Formats() {}

    public static String dateTime(LocalDateTime time) {
        return time == null ? "" : DATE_TIME.format(time);
    }

    public static String shortDate(LocalDateTime time) {
        return time == null ? "" : SHORT_DATE.format(time);
    }

    public static String reportsCount(int count) {
        return count == 1 ? "1 Δήλωση" : count + " Δηλώσεις";
    }

}
