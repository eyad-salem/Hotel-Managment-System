import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        DatabaseManager.initialize();
        showLoginScreen();
    }

    public static void showLoginScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 620);
        scene.getStylesheets().add(MainApp.class.getResource("/css/styles.css").toExternalForm());
        primaryStage.setTitle("Grand Hamada Hotel");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void showGuestDashboard(Guest guest) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/GuestDashboard.fxml"));
        Parent root = loader.load();
        GuestDashboardController ctrl = loader.getController();
        ctrl.setGuest(guest);
        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(MainApp.class.getResource("/css/styles.css").toExternalForm());
        primaryStage.setTitle("Grand Hamada Hotel — Guest Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
    }

    public static void showAdminDashboard(Admin admin) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/AdminDashboard.fxml"));
        Parent root = loader.load();
        AdminDashboardController ctrl = loader.getController();
        ctrl.setAdmin(admin);
        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(MainApp.class.getResource("/css/styles.css").toExternalForm());
        primaryStage.setTitle("Grand Hamada Hotel — Admin Panel");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
    }

    public static void showReceptionistDashboard(Receptionist rec) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/ReceptionistDashboard.fxml"));
        Parent root = loader.load();
        ReceptionistDashboardController ctrl = loader.getController();
        ctrl.setReceptionist(rec);
        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(MainApp.class.getResource("/css/styles.css").toExternalForm());
        primaryStage.setTitle("Grand Hamada Hotel — Receptionist Panel");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
    }

    public static Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) {
        launch(args);
    }
}
