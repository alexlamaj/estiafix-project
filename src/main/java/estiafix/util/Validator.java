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

    public static Optional<String> description(String text) {

        int length = text == null ? 0 : text.trim().length();

        if (length < 20) {
            return Optional.of("Γράψε τουλάχιστον 20 χαρακτήρες!");
        }

        if (length > 500) {
            return Optional.of("Η περιγραφή χωράει έως 500 χαρακτήρες!");
        }

        return Optional.empty();

    }

    public static Optional<String> access(Boolean withoutMe) {
        if (withoutMe == null) {
            return Optional.of("Πες μας αν μπορεί να μπει ο τεχνικός χωρίς εσένα στον χώρο...");
        }
        return Optional.empty();
    }

    public static Optional<String> consent(boolean withoutMe, boolean consentGiven) {
        if (withoutMe && !consentGiven) {
            return Optional.of("Χρειάζεται η συναίνεση σου για να μπει ο τεχνικός...");
        }
        return Optional.empty();
    }

    public static Optional<String> hours(LocalTime from, LocalTime to) {

        if (from == null || to == null) {
            return Optional.of("Διάλεξε από ποια έως ποια ώρα είσαι στο δωμάτιο...");
        }

        if (!to.isAfter(from)) {
            return Optional.of("Η ώρα λήξης πρέπει να είναι μετά την ώρα έναρξης!");
        }

        if (from < WORK_START || from > WORK_END) {
            return Optional.of("Οι τεχνικοί δουλέυουν 08:00 - 16:00");
        }

        return Optional.empty();

    }

    public static Optional<String> phone(String text) {

        String value = text == null ? "" : text.replace(" ", "").trim();

        if (!value.matches("69\\d{8}|2\\d{9}")) {
            return Optional.of("Το τηλέφωνο θέλει 10 ψηφία, π.χ. 6912345678");
        }

        return Optional.empty();

    }

}
