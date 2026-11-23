package estiafix.controller;

import estiafix.AppState;
import estiafix.SceneManager;
import estiafix.View;
import estiafix.data.DataStore;
import estiafix.model.Category;
import estiafix.model.FaultReport;
import estiafix.model.Student;
import estiafix.util.Formats;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.util.Optional;

public class CategoryController {

    @FXML private VBox duplicateCard;
    @FXML private Label duplicateText;

    private Category chosenCategory;
    private FaultReport existingReport;

    @FXML
    private void initialize() {
        setDuplicateCardShown(false);
    }

    @FXML
    private void onCategorySelected(ActionEvent event) {

        Button button = (Button) event.getSource();
        chosenCategory = Category.valueOf((String) button.getUserData());

        Student student = AppState.get().getCurrentStudent();
        Optional<FaultReport> open = DataStore.get().findOpenBuilding(student.building(), chosenCategory);

        if (open.isPresent()) {

            existingReport = open.get();
            duplicateText.setText("Στο κτήριο " + existingReport.getBuilding() + " " + chosenCategory.getLabel() + ">> (" + existingReport.getCode() + ", " + existingReport.getLocation() + "):\n<<" + " " + existingReport.getStatus().getLabel() + " " + Formats.shortDate(existingReport.getCreatedAt()) + ".\n" + "");
            setDuplicateCardShown(true);

        } else {
            goToForm();
        }

    }

    @FXML
    private void onFollow() {

        Student student = AppState.get().getCurrentStudent();
        DataStore.get().follow(existingReport, student.username());
        AppState.get().setFlashMessage("Θα βλέπεις την πρόοδο της " + existingReport.getCode() + " εδώ, στα 'Οι δηλώσεις μου'.");
        SceneManager.show(View.MY_REPORTS);

    }

    @FXML
    private void onSomethingElse() {
        goToForm();
    }

    @FXML
    private void onBack() {
        SceneManager.show(View.HOME);
    }

    private void goToForm() {
        AppState.get().setSelectedCategory(chosenCategory);
        SceneManager.show(View.REPORT_FORM);
    }

    private void setDuplicateCardShown(boolean shown) {
        duplicateCard.setVisible(shown);
        duplicateCard.setManaged(shown);
    }

}
