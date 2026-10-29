import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;

public class LoginController {

    @FXML private TabPane tabPane;
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private ToggleGroup userTypeGroup;
    @FXML private RadioButton guestRadio;
    @FXML private RadioButton staffRadio;
    @FXML private Label loginErrorLabel;

    @FXML private TextField regUsernameField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmPasswordField;
    @FXML private DatePicker regDobPicker;
    @FXML private TextField regAddressField;
    @FXML private ComboBox<String> regGenderCombo;
    @FXML private Label regErrorLabel;
    @FXML private Label regSuccessLabel;

    @FXML
    public void initialize() {
        regGenderCombo.getItems().addAll("MALE", "FEMALE");
        regGenderCombo.getSelectionModel().selectFirst();
        loginErrorLabel.setVisible(false);
        regErrorLabel.setVisible(false);
        regSuccessLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        loginErrorLabel.setVisible(false);
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Please enter username and password.");
            return;
        }

        boolean isGuest = guestRadio.isSelected();

        if (isGuest) {
            try {
                Guest guest = Guest.login(username, password);
                SessionManager.setCurrentGuest(guest);
                SceneManager.switchTo("GuestDashboard.fxml");
            } catch (InvalidInputException e) {
                showLoginError(e.getMessage());
            } catch (IOException e) {
                showLoginError("Could not load dashboard: " + e.getMessage());
            }
        } else {
            Staff staff = Staff.loginStaff(username, password);
            if (staff == null) {
                showLoginError("Invalid staff credentials.");
                return;
            }
            SessionManager.setCurrentStaff(staff);
            try {
                if (staff instanceof Admin) {
                    SceneManager.switchTo("AdminDashboard.fxml");
                } else if (staff instanceof Receptionist) {
                    SceneManager.switchTo("ReceptionistDashboard.fxml");
                }
            } catch (IOException e) {
                showLoginError("Could not load dashboard: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRegister() {
        regErrorLabel.setVisible(false);
        regSuccessLabel.setVisible(false);

        String username = regUsernameField.getText().trim();
        String password = regPasswordField.getText();
        String confirm = regConfirmPasswordField.getText();
        LocalDate dob = regDobPicker.getValue();
        String address = regAddressField.getText().trim();
        String genderStr = regGenderCombo.getValue();

        if (username.isEmpty() || password.isEmpty() || address.isEmpty() || dob == null) {
            showRegError("All fields are required.");
            return;
        }

        if (!password.equals(confirm)) {
            showRegError("Passwords do not match.");
            return;
        }

        if (Guest.findGuestByUsername(username) != null) {
            showRegError("Username already taken.");
            return;
        }

        try {
            Gender gender = Gender.valueOf(genderStr);
            Guest newGuest = new Guest(username, password, dob, address, gender);
            HotelDatabase.allGuests.add(newGuest);
            regSuccessLabel.setText("Registration successful! You can now log in.");
            regSuccessLabel.setVisible(true);
            clearRegForm();

            tabPane.getSelectionModel().select(0);
            loginUsernameField.setText(username);
        } catch (InvalidInputException e) {
            showRegError(e.getMessage());
        }
    }

    private void showLoginError(String msg) {
        loginErrorLabel.setText(msg);
        loginErrorLabel.setVisible(true);
    }

    private void showRegError(String msg) {
        regErrorLabel.setText(msg);
        regErrorLabel.setVisible(true);
    }

    private void clearRegForm() {
        regUsernameField.clear();
        regPasswordField.clear();
        regConfirmPasswordField.clear();
        regDobPicker.setValue(null);
        regAddressField.clear();
        regGenderCombo.getSelectionModel().selectFirst();
    }
}
