package estiafix.util;

import java.time.LocalTime;
import java.util.Optional;

public final class Validator {

    public static final LocalTime WORK_START = LocalTime.of(8, 0);
    public static final LocalTime WORK_END = LocalTime.of(16, 0);

    private Validator() {}

    public static Optional<String> building(String value) {
        if (value == null || value.isBlank()) {
            return Optional.of("Διάλεξε κτήριο");
        }
        return Optional.empty();
    }

    public static Optional<String> room(String text) {

        String value = text == null ? "" : text.trim();
        if (!value.matches("\\d{3}")) {
            return Optional.of("Ο αριθμός δωματίου έχει 3 ψηφία, πχ 214");
        }

        int number = Integer.parseInt(value);
        if (number < 101 || number > 499) {
            return Optional.of("Τα δωμάτια της εστίας είναι από 101 έως 499");
        }

        return Optional.empty();

    }

}
