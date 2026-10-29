import javafx.application.Application;
import javafx.stage.Stage;

public class HotelApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseManager.initialize(); // Load/save data from file-based database
        ChatServer.start();

        primaryStage.setTitle("7amada Hotel Reservation System");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        SceneManager.setPrimaryStage(primaryStage);
        SceneManager.switchTo("Login.fxml");

        primaryStage.setOnCloseRequest(e -> {
            try { DatabaseManager.saveAllData(); } catch (Exception ex) {}
            ChatServer.stop();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
