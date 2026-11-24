package estiafix.util;

import estiafix.model.Status;
import javafx.scene.control.Label;

public final class Ui {

    private Ui() {}

    public static void statusTag(Label tag, Status status) {
        tag.setText(status.getLabel());
        tag.getStyleClass().setAll("label", "tag", status.getStyleClass());
    }

    public static Label emptyState(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("empty-state");
        label.setWrapText(true);
        return label;
    }

}
