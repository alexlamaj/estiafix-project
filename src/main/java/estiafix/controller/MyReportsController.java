package estiafix.controller;

import estiafix.AppState;
import estiafix.SceneManager;
import estiafix.View;
import estiafix.data.DataStore;
import estiafix.model.FaultReport;
import estiafix.model.Status;
import estiafix.util.Formats;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.security.AllPermission;
import java.text.Normalizer;
import java.time.LocalDateTime;

public class MyReportsController {

    private static final String ALL = "Όλες";

    @FXML private Label messageLabel;
    @FXML private Label countLabel;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TextField searchField;
    @FXML private TableView<FaultReport> table;
    @FXML private TableColumn<FaultReport, String> codeColumn;
    @FXML private TableColumn<FaultReport, String> categoryColumn;
    @FXML private TableColumn<FaultReport, String> locationColumn;
    @FXML private TableColumn<FaultReport, LocalDateTime> dateColumn;
    @FXML private TableColumn<FaultReport, Status> statusColumn;
    @FXML private Label detailsLabel;
    @FXML private Button cancelButton;

    private FilteredList<FaultReport> filteredReports;
    private String username;

    @FXML
    private void initialize() {

        username = AppState.get().getCurrentStudent().username();

        filteredReports = new FilteredList<>(DataStore.get().getReports(), report -> report.belongsTo(username));
        SortedList<FaultReport> sortedReports = new SortedList<>(filteredReports);
        sortedReports.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedReports);

        // Table Setting
        codeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCode()));
        categoryColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCategory().getLabel()));
        locationColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLocation()));

        dateColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCreatedAt()));
        dateColumn.setCellFactory(column -> new TableCell<FaultReport, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : Formats.dateTime(date));
            }
        });

        statusColumn.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getStatus()));
        statusColumn.setCellFactory(column -> new TableCell<FaultReport, Status>() {
            @Override
            protected void updateItem(Status status, boolean empty) {

                super.updateItem(status, empty);
                getStyleClass().removeIf(style -> style.startsWith("status-"));

                if (empty || status == null) {
                    setText(null);
                } else {
                    setText(status.getLabel());
                    getStyleClass().add(status.getStyleClass());
                }

            }
        });

        // Sorting
        dateColumn.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(dateColumn);
        table.setPlaceholder(new Label("Δεν βρέθηκαν δηλώσεις με αυτά τα κριτήρια"));

        // Filtering
        statusFilter.getItems().add(ALL);
        for (Status status : Status.values()) {
            statusFilter.getItems().add(status.getLabel());
        }
        statusFilter.setValue(ALL);
        statusFilter.valueProperty().addListener(((observable, oldValue, newValue) -> applyFilters()));
        searchField.textProperty().addListener((observable, oldText, newText) -> applyFilters());

        // Selected Row and Actions
        table.getSelectionModel().selectedItemProperty().addListener((observable, oldReport, newReport) -> showDetails(newReport));
        cancelButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            FaultReport selected = table.getSelectionModel().getSelectedItem();
            return selected == null || !selected.getStatus().canBeCanceled();
        }, table.getSelectionModel().selectedItemProperty()));

        showDetails(null);
        hideMessage();
        AppState.get().takeFlashMessage().ifPresent(this::showMessage);
        updateCount();

    }

    private void applyFilters() {

        String selectedStatus = statusFilter.getValue();
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();

        filteredReports.setPredicate(report -> report.belongsTo(username) && (selectedStatus == null || ALL.equals(selectedStatus) || report.getStatus().getLabel().equals(selectedStatus)));

        updateCount();

    }

    @FXML
    private void onCancelReport() {

        FaultReport selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        DataStore.get().cancel(selected);
        table.refresh();
        table.getSelectionModel().clearSelection();
        applyFilters();
        showMessage("Η δήλωση: " + selected.getCode() + " ακυρώθηκε.");

    }

    @FXML
    private void onHome() {
        SceneManager.show(View.HOME);
    }

    @FXML
    private void onNewReport() {
        SceneManager.show(View.CATEGORY);
    }

    // HELPERS //
    private void showDetails(FaultReport report) {

        if (report == null) {
            detailsLabel.setText("Διάλεξε μια δήλωση από τον πίνακα για να δεις λεπτομέρειες.");
            return;
        }

        String note = report.getTechnicianNote().isBlank() ? "-" : report.getTechnicianNote();
        detailsLabel.setText(report.getCode() + " . " + report.getCategory().getLabel() + " . Δωμάτιο " + report.getLocation() + "\n" + "<<" + report.getDescription() + ">>\n" + "Κατάσταση: " + report.getStatus().getLabel() + " (τελευταία αλλαγή: " + Formats.dateTime(report.getLastUpdated()) + ")\n" + "Σημείωση τεχνικού: " + note);

    }

    private void updateCount() {
        countLabel.setText(Formats.reportsCount(filteredReports.size()));
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }

    private void hideMessage() {
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }

}
