package estiafix.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.Optional;

public final class Dialogs {

    private Dialogs() {}

    public static boolean confirm(String question) {
        return ask(question, false);
    }

    public static boolean confirmDestructive(String question) {
        return ask(question, true);
    }

    private static boolean ask(String question, boolean noIsDefault) {

        ButtonType yes = new ButtonType("Ναι", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("Όχι", ButtonBar.ButtonData.NO);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, question, yes, no);
        alert.setTitle("EstiaFix");
        alert.setHeaderText(null);

        URL css = Dialogs.class.getResource("/css/style.css");
        if (css != null) {
            alert.getDialogPane().getStylesheets().add(css.toExternalForm());
        }

        if (noIsDefault) {
            ((Button) alert.getDialogPane().lookupButton(yes)).setDefaultButton(false);
            ((Button) alert.getDialogPane().lookupButton(no)).setDefaultButton(true);
        }

        Optional<ButtonType> answer = alert.showAndWait();
        return answer.isPresent() && answer.get() == yes;

    }

}
