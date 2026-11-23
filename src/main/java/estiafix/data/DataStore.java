package estiafix.data;

import estiafix.model.FaultReport;
import estiafix.model.Category;
import estiafix.model.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.util.Optional;

public final class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    private final ObservableList<FaultReport> reports = FXCollections.observableArrayList();
    private int lastNumber = 0;

    private DataStore() {
        loadSampleData();
    }

    public static DataStore get() {
        return INSTANCE;
    }

    public ObservableList<FaultReport> getReports() {
        return reports;
    }

    public FaultReport add(FaultReport report) {
        lastNumber++;
        report.setId(String.format("ΒΛ-%04d", lastNumber));
        reports.add(report);
        return report;
    }

    public Optional<FaultReport> findOpenBuilding(String building, Category category) {
        return reports.stream().filter(r -> r.isOpen()).filter(r -> r.getBuilding().equals(building)).filter(r -> r.getCategory().equals(category)).findFirst();
    }

    public boolean hasOpenReport(String username, String building, int room, Category category) {
        return reports.stream().anyMatch(r -> r.isOpen() && r.getReporter().equals(username) && r.getBuilding().equals(building) && r.getCategory().equals(category) && r.getRoom() == room);
    }

    public void cancel(FaultReport report) {
        report.changeStatus(Status.CANCELED, LocalDateTime.now());
    }

    public void follow(FaultReport report, String username) {
        report.follow(username);
    }

    // Sample Data so it cannot be empty at the start //
    private void loadSampleData() {

        sample(190, "marina", "B", 214, Category.INTERNET, "Το WiFi στο δωμάτιο κόβεται κάθε λίγα λεπτά", "2026-11-03T18:20", Status.COMPLETED, "Αλλαγή access point στον 2ο όροφο");
        sample(193, "nikos", "A", 105, Category.ELECTRICAL, "Η πρίζα δίπλα στο γραφείο δεν έχει ρεύμα", "2026-11-06Τ10:05", Status.COMPLETED, "Αλλαγή πρίζας");
        sample(196, "marina", "B", 214, Category.DOORS_FURNITURE, "Η πόρτα της ντουλάπας δεν κλείνει έσπασε ο μεντεσές", "2026-11-12T21:40", Status.SCHEDULED, "");
        sample(198, "eleni", "B", 305, Category.PLUMBING, "Δεν υπάρχει ζεστό νερό σε όλον τον όροφο", "2026-11-18Τ21:40", Status.IN_PROGRESS, "Περιμένουμε ανταλλακτικά για τον θερμοσίφωνα");
        sample(203, "kostas", "C", 410, Category.HEATING, "Το καλοριφέρ κάνει δυνατό θόρυβο την νύχτα", "2026-11-18T23:10", Status.SUBMITTED, "");
        sample(207, "anna", "B", 118, Category.ELECTRICAL, "Ο διάδρομος του 1ου ορόφου είναι σκοτεινός, μάλλον κάηκαν οι λάμπες", "2026-11-19T19:30", Status.SCHEDULED, "");
        sample(215, "giorgos", "A", 221, Category.PLUMBING, "Η βρύση του νιπτήρα στάζει συνέχεια", "2026-11-20T09:45", Status.SUBMITTED, "");
        sample(222, "petros", "C", 102, Category.INTERNET, "Ο υπολογιστής δεν συνδέεται στο δίκτυο της εστίας", "2026-11-20T16:00", Status.IN_PROGRESS, "");
        sample(230, "maria", "A", 310, Category.OTHER, "Το παράθυρο δεν κλείνει και μπαίνει κρύο", "2026-11-21T15:45", Status.SUBMITTED, "");
        sample(204, "eleni", "B", 305,Category.HEATING, "Ο θερμοστάτης του δωματίου δεν ανάβει", "2026-11-19T20:00", Status.COMPLETED, "Αλλαγή μπαταρίας θερμοστάτη");

    }

    private void sample(int number, String reporter, String building, int room, Category category, String description, String createdAt, Status status, String note) {

        LocalDateTime created = LocalDateTime.parse(createdAt);
        FaultReport report = new FaultReport(reporter, building, room, category, description, created);

        report.setId(String.format("ΒΛ-%04d", number));

        if (status != Status.SUBMITTED) {
            report.changeStatus(status, created.plusHours(3));
        }

        report.setTechnicianNote(note);
        reports.add(report);
        lastNumber = Math.max(lastNumber, number);

    }

}
