package estiafix.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public final class Dialogs {

    private Dialogs() {}

    public static boolean confirm(String question) {

        ButtonType yes = new ButtonType("Ναι", ButtonBar.ButtonData.YES);
        ButtonType no = new ButtonType("Όχι", ButtonBar.ButtonData.NO);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, question, yes, no);
        alert.setTitle("EstiaFix");
        alert.setHeaderText(null);

        Optional<ButtonType> answer = alert.showAndWait();
        return answer.isPresent() && answer.get() == yes;

    }

}
