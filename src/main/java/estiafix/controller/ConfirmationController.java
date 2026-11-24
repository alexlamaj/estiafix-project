package estiafix.controller;

import estiafix.AppState;
import estiafix.SceneManager;
import estiafix.View;
import estiafix.model.FaultReport;
import estiafix.util.Ui;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ConfirmationController {

    @FXML private Label titleLabel;
    @FXML private Label codeLabel;
    @FXML private Label statusTag;
    @FXML private Label summaryLabel;
    @FXML private Label nextStepsLabel;

    @FXML
    private void initialize() {

        FaultReport report = AppState.get().getLastSubmitted();

        if (report == null) {
            titleLabel.setText("Δεν βρέθηκε νέα δήλωση");
            codeLabel.setText("");
            statusTag.setVisible(false);
            return;
        }

        titleLabel.setText("Η δήλωση σου " + report.getCode() + " καταχωρίστηκε.");
        codeLabel.setText(report.getCode());
        Ui.statusTag(statusTag, report.getStatus());
        String access = report.isAccessWithoutMe() ? "Ο τεχνικός μπορεί να μπει χωρίς εσένα" : "Ο τεχνικός θα έρθει όταν είσαι έκει" + hoursText(report);
        summaryLabel.setText(report.getCategory().getLabel() + " . Δωμάτιο " + report.getLocation() + (report.isUrgent() ? " . Επείγον" : "") + "\n" + "<<" + report.getDescription() + ">>\n" + access);
        nextStepsLabel.setText("1. Ο τεχνικός θα δει τη δήλωση μέσα σε 1 εργάσιμη.\n" + "2. Όταν οριστεί μέρα, η κατάσταση θα γίνει 'Προγραμματίστηκε'.\n" + "3. Την πρόοδο την βλέπεις πάντα στο 'Οι δηλώσεις μου'.\n");

    }

    private String hoursText(FaultReport report) {
        if (report.getAccessFrom() == null || report.getAccessTo() == null) {
            return "";
        }
        return ", " + report.getAccessFrom() + "-" + report.getAccessTo();
    }

    @FXML
    private void onMyReports() {
        SceneManager.show(View.MY_REPORTS);
    }

    @FXML
    private void onHome() {
        SceneManager.show(View.HOME);
    }

}
