module estiafix {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    opens estiafix.controller to javafx.fxml;
    opens estiafix.model to javafx.base;
    exports estiafix;
}
