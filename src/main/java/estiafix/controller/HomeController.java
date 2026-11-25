package estiafix.controller;

import estiafix.AppState;
import estiafix.SceneManager;
import estiafix.View;
import estiafix.data.DataStore;
import estiafix.model.FaultReport;
import estiafix.model.Student;
import estiafix.util.Formats;
import estiafix.util.Ui;
import javafx.collections.transformation.FilteredList;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class HomeController {

    @FXML private Label greetingLabel;
    @FXML private Label roomLabel;
    @FXML private Label buildingTitle;
    @FXML private Label updatesLabel;
    @FXML private ListView<FaultReport> myOpenList;
    @FXML private ListView<FaultReport> buildingIssuesList;

    @FXML
    private void initialize() {

        Student student = AppState.get().getCurrentStudent();
        greetingLabel.setText("Γεια σου, " + student.firstName());
        roomLabel.setText("Κτήριο: " + student.building() + ", δωμάτιο " + student.room());
        buildingTitle.setText("Γνωστές βλάβες στο κτήριο " + student.building());

        myOpenList.setItems(new FilteredList<>(DataStore.get().getReports(), report -> report.belongsTo(student.username()) && report.isOpen()));
        buildingIssuesList.setItems(new FilteredList<>(DataStore.get().getReports(), report -> report.getBuilding().equals(student.building()) && report.isOpen()));

        myOpenList.setCellFactory(list -> createReportCell());
        buildingIssuesList.setCellFactory(list -> createReportCell());

        myOpenList.setPlaceholder(Ui.emptyState("Δεν έχεις ανοιχτές δηλώσεις!"));
        buildingIssuesList.setPlaceholder(Ui.emptyState("Καμία γνωστή βλάβη στο κτήριο σου!"));

        showTechnicianUpdates(student);

    }

    private void showTechnicianUpdates(Student student) {

        List<FaultReport> updates = AppState.get().takeUpdatesFor(student.username());

        if (updates.isEmpty()) {
            updatesLabel.setVisible(false);
            updatesLabel.setManaged(false);
            return;
        }

        String lines = updates.stream().map(report -> report.getCode() + ": " + report.getStatus().getLabel() + (report.getTechnicianNote().isBlank() ? "" : " . Ο τεχνικός γράφει: <<" + report.getTechnicianNote() + ">>")).collect(Collectors.joining("\n"));

        updatesLabel.setText("Νέα από το τεχνικό τμήμα: \n" + lines);
        updatesLabel.setVisible(true);
        updatesLabel.setManaged(true);


    }

    private ListCell<FaultReport> createReportCell() {

        return new ListCell<>() {

            private final Label tag = new Label();
            private final Label title = new Label();
            private final Label meta = new Label();
            private final Label note = new Label();
            private final Label date = new Label();
            private final Region spacer = new Region();
            private final HBox row = new HBox(16, tag, new VBox(1, title, meta), spacer, date);

            {
                title.getStyleClass().add("row-title");
                meta.getStyleClass().add("row-meta");
                date.getStyleClass().add("row-meta");
                HBox.setHgrow(spacer, Priority.ALWAYS);
                row.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(FaultReport report, boolean empty) {

                super.updateItem(report, empty);
                setText(null);

                if (empty || report == null) {
                    setGraphic(null);
                    return;
                }

                Ui.statusTag(tag, report.getStatus());
                title.setText(report.getCategory().getLabel());
                meta.setText(report.getCode() + ", δωμάτιο " + report.getLocation());
                date.setText(Formats.shortDate(report.getCreatedAt()));
                setGraphic(row);

                boolean hasNote = !report.getTechnicianNote().isBlank();
                note.setText(hasNote ? "" + report.getTechnicianNote() : "");
                note.setVisible(hasNote);
                note.setManaged(hasNote);
                setGraphic(row);

            }

        };

    }

    @FXML
    private void onNewReport() {
        SceneManager.show(View.CATEGORY);
    }

    @FXML
    private void onMyReports() {
        SceneManager.show(View.MY_REPORTS);
    }

    @FXML
    private void onTechnician() {
        SceneManager.show(View.TECHNICIAN);
    }

}
