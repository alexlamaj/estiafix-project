package estiafix;

public enum View {

    HOME("home-view.fxml", "Αρχική"),
    CATEGORY("category-view.fxml", "Νέα Δήλωση"),
    REPORT_FORM("report-form-view.fxml", "Στοιχεία Βλάβης"),
    CONFIRMATION("confirmation-view.fxml", "Επιβεβαίωση"),
    MY_REPORTS("my-reports-view.fxml", "Οι Δηλώσεις Μου"),
    TECHNICIAN("technician-view.fxml", "Τεχνικό Τμήμα");

    private final String fxmlFile;
    private final String title;

    View(String fxmlFile, String title) {
        this.fxmlFile = fxmlFile;
        this.title = title;
    }

    public String getFxmlFile() {
        return fxmlFile;
    }

    public String getTitle() {
        return title;
    }

}
