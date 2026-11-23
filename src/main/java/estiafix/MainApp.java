package estiafix;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.init(stage);
        stage.setMinWidth(1024);
        stage.setMinHeight(700);
        SceneManager.show(View.HOME);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
