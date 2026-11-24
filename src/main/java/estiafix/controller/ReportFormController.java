package estiafix.controller;

import estiafix.AppState;
import estiafix.SceneManager;
import estiafix.View;
import estiafix.data.DataStore;
import estiafix.model.Category;
import estiafix.model.FaultReport;
import estiafix.util.Dialogs;
import estiafix.util.Validator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import javafx.scene.Node;
import estiafix.model.Student;

public class ReportFormController {

    private static final int MAX_DESCRIPTION = 500;

    @FXML private Label categoryLabel;
    @FXML private Label formError;
    @FXML private ComboBox<String> buildingBox;
    @FXML private Label buildingError;
    @FXML private TextField roomField;
    @FXML private Label roomError;
    @FXML private TextArea descriptionArea;
    @FXML private Label charCountLabel;
    @FXML private Label descriptionError;
    @FXML private CheckBox urgentCheck;
    @FXML private RadioButton withoutMeRadio;
    @FXML private RadioButton withMeRadio;
    @FXML private CheckBox consentCheck;
    @FXML private Label consentError;
    @FXML private HBox hoursBox;
    @FXML private ComboBox<LocalTime> fromBox;
    @FXML private ComboBox<LocalTime> toBox;
    @FXML private Label hoursError;
    @FXML private Label accessError;
    @FXML private TextField phoneField;
    @FXML private Label phoneError;

    private Category category;
    private Control firstInvalidField;

    @FXML
    private void initialize() {

        Student student = AppState.get().getCurrentStudent();
        category = AppState.get().getSelectedCategory();

        if (category == null) {
            category = Category.OTHER;
        }
        categoryLabel.setText("Κατηγορία: " + category.getLabel());

        buildingBox.getItems().setAll("A", "B", "C");
        buildingBox.setValue(student.building());
        roomField.setText(String.valueOf(student.room()));
        phoneField.setText(student.phone());

        for (LocalTime time = Validator.WORK_START; !time.isAfter(Validator.WORK_END); time = time.plusMinutes(30)) {
            fromBox.getItems().add(time);
            toBox.getItems().add(time);
        }

        consentCheck.visibleProperty().bind(withoutMeRadio.selectedProperty());
        consentCheck.managedProperty().bind(withoutMeRadio.selectedProperty());
        hoursBox.visibleProperty().bind(withMeRadio.selectedProperty());
        hoursBox.managedProperty().bind(withMeRadio.selectedProperty());

        descriptionArea.textProperty().addListener((observable, oldText, newText) -> charCountLabel.setText(newText.length() + "/" + MAX_DESCRIPTION + " χαρακτήρες"));
        charCountLabel.setText("0/" + MAX_DESCRIPTION + " χαρακτήρες");

        cleanErrors();

    }

    @FXML
    private void onSubmit() {

        cleanErrors();
        Student student = AppState.get().getCurrentStudent();
        Boolean withoutMe = null;

        if (withoutMeRadio.isSelected()) {
            withoutMe = true;
        } else if (withMeRadio.isSelected()) {
            withoutMe = false;
        }

        boolean valid = true;
        valid &= check(Validator.building(buildingBox.getValue()), buildingError, buildingBox);
        valid &= check(Validator.room(roomField.getText()), roomError, roomField);
        valid &= check(Validator.description(descriptionArea.getText()), descriptionError, descriptionArea);
        valid &= check(Validator.access(withoutMe), accessError, null);

        if (Boolean.TRUE.equals(withoutMe)) {
            valid &= check(Validator.consent(true, consentCheck.isSelected()), consentError, consentCheck);
        }

        if (Boolean.FALSE.equals(withoutMe)) {
            valid &= check(Validator.hours(fromBox.getValue(), toBox.getValue()), hoursError, fromBox);
        }

        valid &= check(Validator.phone(phoneField.getText()), phoneError, phoneField);

        if (!valid) {
            showFormError("Η δήλωση δεν καταχωρίστηκε. Διόρθωσε τα πεδία με κόκκινο και πάτα ξανά «Καταχώριση δήλωσης».");
            return;
        }

        String building = buildingBox.getValue();
        int room = Integer.parseInt(roomField.getText().trim());

        FaultReport report = new FaultReport(student.username(), building, room, category, descriptionArea.getText().trim(), LocalDateTime.now());
        report.setUrgent(urgentCheck.isSelected());
        report.setAccessWithoutMe(withoutMe);

        if (!withoutMe) {
            report.setAccessFrom(fromBox.getValue());
            report.setAccessTo(toBox.getValue());
        }

        report.setPhone(phoneField.getText().replace(" ", "").trim());

        DataStore.get().add(report);
        AppState.get().setLastSubmitted(report);
        SceneManager.show(View.CONFIRMATION);

    }

    @FXML
    private void onBack() {
        boolean hasTyping = !descriptionArea.getText().isBlank();
        if (hasTyping && !Dialogs.confirm("Θα χαθούν όσα έγραψες. Να συνεχίσω;")) {
            return;
        }
        SceneManager.show(View.CATEGORY);
    }

    // HELPERS FOR ERRORS //
    private boolean check(Optional<String> error, Label errorLabel, Control field) {

        if (error.isEmpty()) {
            return true;
        }

        errorLabel.setText(error.get());
        setShown(errorLabel, true);

        if (field != null) {
            field.getStyleClass().add("field-error");
            if (firstInvalidField == null) {
                firstInvalidField = field;
            }
        }

        return false;

    }

    private void showFormError(String message) {
        formError.setText(message);
        setShown(formError, true);
        if (firstInvalidField != null) {
            firstInvalidField.requestFocus();
        }
    }

    private void cleanErrors() {

        firstInvalidField = null;
        List<Label> errorLabels = List.of(formError, buildingError, roomError, descriptionError, consentError, hoursError, accessError, phoneError);

        for (Label label : errorLabels) {
            label.setText("");
            setShown(label, false);
        }

        List<Control> fields = List.of(buildingBox, roomField, descriptionArea, consentCheck, fromBox, phoneField);

        for (Control field : fields) {
            field.getStyleClass().remove("field-error");
        }

    }

    private void setShown(Node node, boolean shown) {
        node.setVisible(shown);
        node.setManaged(shown);
    }

}
