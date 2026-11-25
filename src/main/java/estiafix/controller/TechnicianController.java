package estiafix.controller;

import estiafix.AppState;
import estiafix.SceneManager;
import estiafix.View;
import estiafix.data.DataStore;
import estiafix.model.FaultReport;
import estiafix.model.Status;
import estiafix.util.Dialogs;
import estiafix.util.Formats;
import estiafix.util.Ui;
import estiafix.util.Validator;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TechnicianController {

    private static final String OPEN = "Ανοιχτές";
    private static final String ALL = "Όλες";
    private static final String ALL_BUILDINGS = "Όλα";

    @FXML private ComboBox<String> buildingFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private CheckBox urgentOnlyCheck;
    @FXML private Label countLabel;
    @FXML private Label messageLabel;
    @FXML private TableView<FaultReport> jobsTable;
    @FXML private TableColumn<FaultReport, String> codeColumn;
    @FXML private TableColumn<FaultReport, String> categoryColumn;
    @FXML private TableColumn<FaultReport, String> roomColumn;
    @FXML private TableColumn<FaultReport, Boolean> urgentColumn;
    @FXML private TableColumn<FaultReport, LocalDateTime> dateColumn;
    @FXML private TableColumn<FaultReport, Status> statusColumn;

    @FXML private Label emptyDetails;
    @FXML private VBox detailsBox;
    @FXML private Label jobCodeLabel;
    @FXML private Label jobTag;
    @FXML private Label jobTitleLabel;
    @FXML private Label jobDescriptionLabel;
    @FXML private Label jobAccessLabel;
    @FXML private Label jobContactLabel;

    @FXML private VBox editBox;
    @FXML private ComboBox<Status> nextStatusBox;
    @FXML private Label statusError;
    @FXML private TextArea noteArea;
    @FXML private Label noteCountLabel;
    @FXML private Label noteError;
    @FXML private Label closedLabel;

    private FilteredList<FaultReport> filteredJobs;
    private Control firstInvalidField;

    @FXML
    private void initialize() {

        filteredJobs = new FilteredList<>(DataStore.get().getReports());
        SortedList<FaultReport> sortedJobs = new SortedList<>(filteredJobs);
        sortedJobs.comparatorProperty().bind(jobsTable.comparatorProperty());
        jobsTable.setItems(sortedJobs);

        codeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCode()));
        categoryColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategory().getLabel()));
        roomColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLocation()));

        urgentColumn.setCellValueFactory(cell -> new SimpleObjectProperty<Boolean>(cell.getValue().isUrgent()));
        urgentColumn.setCellFactory(column -> new TableCell<>() {

            @Override
            protected void updateItem(Boolean urgent, boolean empty) {

                super.updateItem(urgent, empty);
                boolean show = !empty && Boolean.TRUE.equals(urgent);
                setText(show ? "Επείγον" : null);
                getStyleClass().remove("urgent-mark");

                if (show) {
                    getStyleClass().add("urgent-mark");
                }

            }

        });

        statusColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getStatus()));
        statusColumn.setCellFactory(column -> Ui.statusTagCell());

        urgentColumn.setSortType(TableColumn.SortType.DESCENDING);
        dateColumn.setSortType(TableColumn.SortType.ASCENDING);
        jobsTable.getSortOrder().setAll(List.of(urgentColumn, dateColumn));
        jobsTable.setPlaceholder(Ui.emptyState("Καμία εργασία με αυτά τα φίλτρα!"));

        buildingFilter.getItems().add(ALL_BUILDINGS);
        DataStore.get().getReports().stream().map(FaultReport::getBuilding).distinct().sorted().forEach(buildingFilter.getItems()::add);
        buildingFilter.setValue(ALL_BUILDINGS);

        statusFilter.getItems().addAll(OPEN, ALL);
        for (Status status : Status.values()) {
            statusFilter.getItems().add(status.getLabel());
        }
        statusFilter.setValue(OPEN);

        buildingFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        urgentOnlyCheck.selectedProperty().addListener(((observable, oldValue, newValue) -> applyFilters()));

        jobsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldJob, newJob) -> showJob(newJob));

        nextStatusBox.setButtonCell(new ListCell<>() {

            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                setText(empty || status == null ? nextStatusBox.getPromptText() : status.getLabel());
            }

        });

        noteArea.textProperty().addListener((observable, oldText, newText) -> noteCountLabel.setText(newText.length() + "/" + Validator.MAX_NOTE));

        setShown(messageLabel, false);
        applyFilters();
        showJob(null);

    }

    @FXML
    private void onSave() {

        FaultReport job = jobsTable.getSelectionModel().getSelectedItem();
        if (job == null) {
            return;
        }

        cleanErrors();

        Status next = nextStatusBox.getValue();
        String note = noteArea.getText() == null ? "" : noteArea.getText().trim();
        boolean noteChanged = !note.equals(job.getTechnicianNote());

        boolean valid = true;
        valid &= check(Validator.statusChoice(next, noteChanged), statusError, nextStatusBox);
        valid &= check(Validator.technicianNote(next, note), noteError, noteArea);

        if (!valid) {
            return;
        }

        if (next != null && !next.isOpen()) {
            String question = "Η " + job.getCode() + " θα γίνει <<" + next.getLabel() + ">> και θα κλείσει, Μετά δεν αλλάζει άλλο. Να συνεχίσω;";
            if (!Dialogs.confirmDestructive(question)) {
                return;
            }
        }

        DataStore.get().updateByTechnician(job, next, note);
        AppState.get().markUpdated(job);

        String what = next != null ? "έγινε <<" + next.getLabel() + ">>" : "πήρε νέα σημείωση";
        showMessage("Η " + job.getCode() + " " + what + ". Ο φοιτητής βλέπει την αλλαγή στις δηλώσεις του...");

        applyFilters();
        jobsTable.refresh();

        if (filteredJobs.contains(job)) {
            jobsTable.getSelectionModel().select(job);
            showJob(job);
        } else {
            jobsTable.getSelectionModel().clearSelection();
        }

    }

    @FXML
    private void onStudentView() {
        SceneManager.show(View.HOME);
    }

    // HELPERS //
    private void applyFilters() {

        String building = buildingFilter.getValue();
        String status = statusFilter.getValue();
        boolean urgentOnly = urgentOnlyCheck.isSelected();

        filteredJobs.setPredicate(job -> {
            boolean buildingOk = building == null || ALL_BUILDINGS.equals(building) || job.getBuilding().equals(building);
            boolean statusOk = status == null || ALL.equals(status) || (OPEN.equals(status) ? job.isOpen() : job.getStatus().getLabel().equals(status));
            boolean urgentOk = !urgentOnly || job.isUrgent();
            return buildingOk && statusOk && urgentOk;
        });

        countLabel.setText(filteredJobs.size() == 1 ? "1 εργασία" : filteredJobs.size() + " εργασίες");

    }

    private void showJob(FaultReport job) {

        cleanErrors();

        if (job == null) {
            setShown(detailsBox, false);
            setShown(emptyDetails, true);
            return;
        }

        setShown(emptyDetails, false);
        setShown(detailsBox, true);

        jobCodeLabel.setText(job.getCode());
        Ui.statusTag(jobTag, job.getStatus());
        jobTitleLabel.setText(job.getCategory().getLabel() + ", δωμάτιο " + job.getLocation() + (job.isUrgent() ? ", επείγον" : ""));
        jobDescriptionLabel.setText("<<" + job.getDescription() + ">>\nΔηλώθηκε " + Formats.dateTime(job.getCreatedAt()) + ", τελευταία αλλαγή" + Formats.dateTime(job.getLastUpdated()));
        jobAccessLabel.setText(accessText(job));
        jobContactLabel.setText(contactText(job));

        boolean closed = !job.isOpen();
        setShown(editBox, !closed);
        setShown(closedLabel, closed);

        nextStatusBox.getItems().setAll(job.getStatus().nextSteps());
        nextStatusBox.setValue(null);
        noteArea.setText(job.getTechnicianNote());

    }

    private String accessText(FaultReport job) {

        if (job.isAccessWithoutMe()) {
            return "Πρόσβαση: Μπορείς να μπεις χωρίς τον φοιτητή στο δωμάτιο";
        }

        if (job.getAccessFrom() != null && job.getAccessTo() != null) {
            return "Πρόσβαση: Μόνο όταν είναι εκεί ο φοιτητής, " + job.getAccessFrom() + " - " + job.getAccessTo();
        }

        return "Πρόσβαση: Δεν δηλώθηκε";

    }

    private String contactText(FaultReport job) {
        String phone = job.getPhone() == null || job.getPhone().isBlank() ? "Δεν δόθηκε" : job.getPhone();
        int others = job.getFollowers().size();
        String followers = others == 0 ? "" : "Την ακολουθούν ακόμα " + others + " φοιτητές";
        return "Δήλωσε: " + job.getReporter() + ", τηλέφωνο" + phone + followers + ".";
    }

    private boolean check(Optional<String> error, Label errorLabel, Control field) {

        if (error.isEmpty()) {
            return true;
        }

        errorLabel.setText(error.get());
        setShown(errorLabel, true);
        field.getStyleClass().add("field-error");

        if (firstInvalidField == null) {
            firstInvalidField = field;
            field.requestFocus();
        }

        return false;

    }

    private void cleanErrors() {

        firstInvalidField = null;

        for (Label label : List.of(statusError, noteError)) {
            label.setText("");
            setShown(label, false);
        }

        nextStatusBox.getStyleClass().remove("field-error");
        noteArea.getStyleClass().remove("field-error");

    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        setShown(messageLabel, true);
    }

    private void setShown(Node node, boolean shown) {
        node.setVisible(shown);
        node.setManaged(shown);
    }

}
