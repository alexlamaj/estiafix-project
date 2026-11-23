package estiafix;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public final class SceneManager {

    private static final double WIDTH = 1280;
    private static final double HEIGHT = 800;

    private static Stage stage;
    private static Scene scene;

    private SceneManager() {}

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void show(View view) {

        Parent root = load(view);
        if (scene == null) {

            scene = new Scene(root, WIDTH, HEIGHT);
            URL css = MainApp.class.getResource("css/style.css");

            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }

            stage.setScene(scene);

        } else {
            scene.setRoot(root);
        }

        stage.setTitle("EstiaFix . " + view.getTitle());

    }

    private static Parent load(View view) {

        URL fxml = MainApp.class.getResource("view/" + view.getFxmlFile());
        if (fxml == null) {
            throw new IllegalStateException("Δεν βρέθηκε το αρχείο view!");
        }

        try {
            return new FXMLLoader(fxml).load();
        } catch (IOException e) {
            throw new IllegalStateException("Σφάλμα στο " + view.getFxmlFile() + ": " + e.getMessage(), e);
        }

    }

}
