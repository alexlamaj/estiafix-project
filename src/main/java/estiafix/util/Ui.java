package estiafix.util;

import estiafix.model.Status;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;

public final class Ui {

    private Ui() {}

    public static void statusTag(Label tag, Status status) {
        tag.setText(status.getLabel());
        tag.getStyleClass().setAll("label", "tag", status.getStyleClass());
    }

    public static <S> TableCell<S, Status> statusTagCell() {
        return new TableCell<>() {

            private final Label tag = new Label();

            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                setText(null);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    statusTag(tag, status);
                    setGraphic(tag);
                }
            }

        };
    }

    public static Label emptyState(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("empty-state");
        label.setWrapText(true);
        return label;
    }

}
