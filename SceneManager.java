import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void switchTo(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(SceneManager.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static <T> T switchToAndGetController(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlFile));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(SceneManager.class.getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        return loader.getController();
    }
}