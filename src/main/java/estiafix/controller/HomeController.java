package estiafix.controller;

import estiafix.AppState;
import estiafix.SceneManager;
import estiafix.View;
import estiafix.data.DataStore;
import estiafix.model.FaultReport;
import estiafix.model.Student;
import javafx.collections.transformation.FilteredList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class HomeController {

    @FXML private Label greetingLabel;
    @FXML private Label buildingTitle;
    @FXML private ListView<FaultReport> myOpenList;
    @FXML private ListView<FaultReport> buildingIssuesList;

    @FXML
    private void initialize() {

        Student student = AppState.get().getCurrentStudent();
        greetingLabel.setText("Γεια σου, " + student.firstName());
        buildingTitle.setText("Γνωστές βλάβες στο κτήριο " + student.building());

        myOpenList.setItems(new FilteredList<>(DataStore.get().getReports(), report -> report.belongsTo(student.username()) && report.isOpen()));
        buildingIssuesList.setItems(new FilteredList<>(DataStore.get().getReports(), report -> getBuilding().equals(student.building()) && report.isOpen()));

        myOpenList.setCellFactory(list -> createReportCell());
        buildingIssuesList.setCellFactory(list -> createReportCell());

        myOpenList.setPlaceholder(new Label("Δεν έχεις ανοιχτές δηλώσεις!"));
        buildingIssuesList.setPlaceholder(new Label("Καμία γνωστή βλάβη στο κτήριο σου!"));

    }

    private ListCell<FaultReport> createReportCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(FaultReport report, boolean empty) {
                super.updateItem(report, empty);
                if (empty || report == null) {
                    setText(null);
                } else {
                    setText(report.getCode() + " . " + report.getCategory().getLabel() + " . " + report.getLocation() + " . " + report.getStatus().getLabel());
                }
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

}
