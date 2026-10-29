import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GuestDashboardController {

    @FXML private Label usernameLabel;
    @FXML private Label dobLabel;
    @FXML private Label genderLabel;
    @FXML private Label addressLabel;
    @FXML private Label activeResLabel;

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, Integer> roomNumCol;
    @FXML private TableColumn<Room, String> roomTypeCol;
    @FXML private TableColumn<Room, Double> roomPriceCol;
    @FXML private TableColumn<Room, Integer> roomCapCol;
    @FXML private TableColumn<Room, String> roomAmenCol;
    @FXML private TableColumn<Room, String> roomAvailCol;
    @FXML private ComboBox<String> filterTypeCombo;
    @FXML private Label roomStatusLabel;

    @FXML private DatePicker checkInPicker;
    @FXML private DatePicker checkOutPicker;
    @FXML private Label reservationStatusLabel;

    @FXML private TableView<Reservation> myResTable;
    @FXML private TableColumn<Reservation, Integer> resIdCol;
    @FXML private TableColumn<Reservation, Integer> resRoomCol;
    @FXML private TableColumn<Reservation, LocalDate> resCheckInCol;
    @FXML private TableColumn<Reservation, LocalDate> resCheckOutCol;
    @FXML private TableColumn<Reservation, ReservationStatus> resStatusCol;
    @FXML private TableColumn<Reservation, Double> resTotalCol;
    @FXML private Label myResStatusLabel;

    @FXML private ComboBox<String> paymentCombo;
    @FXML private Label checkoutStatusLabel;
    @FXML private VBox invoiceBox;
    @FXML private Label invoiceLabel;

    @FXML private TextArea chatArea;
    @FXML private TextField chatInput;
    @FXML private Label chatStatusLabel;

    private Guest currentGuest;
    private ChatClient chatClient;

    @FXML
    public void initialize() {
        currentGuest = SessionManager.getCurrentGuest();
        if (currentGuest == null) return;

        setupProfileTab();
        setupRoomsTable();
        setupReservationsTable();
        setupCheckoutTab();
        loadRooms(null);
        loadMyReservations();
        startRoomAutoRefresh();
    }

    private void setupProfileTab() {
        usernameLabel.setText(currentGuest.getUsername());
        dobLabel.setText(currentGuest.getDateOfBirth().toString());
        genderLabel.setText(currentGuest.getGender().toString());
        addressLabel.setText(currentGuest.getAddress());
        updateActiveResCount();
    }

    private void updateActiveResCount() {
        long count = HotelDatabase.allReservations.stream()
                .filter(r -> r.getGuest().getUsername().equals(currentGuest.getUsername()))
                .filter(r -> r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.CONFIRMED)
                .count();
        activeResLabel.setText(String.valueOf(count));
    }

    private void setupRoomsTable() {
        roomNumCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getRoomType().getName()));
        roomPriceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        roomCapCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getRoomType().getCapacity()).asObject());
        roomAmenCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAmenities().toString()));
        roomAvailCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().isAvailable() ? "✓ Available" : "✗ Occupied"));

        roomsTable.setRowFactory(tv -> new TableRow<Room>() {
            @Override
            protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (item.isAvailable()) {
                    setStyle("-fx-background-color: rgba(46,204,113,0.12);");
                } else {
                    setStyle("-fx-background-color: rgba(231,76,60,0.1);");
                }
            }
        });

        filterTypeCombo.getItems().add("All Types");
        for (RoomType rt : HotelDatabase.allRoomTypes) {
            filterTypeCombo.getItems().add(rt.getName());
        }
        filterTypeCombo.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleFilterRooms() {
        String selected = filterTypeCombo.getValue();
        loadRooms("All Types".equals(selected) ? null : selected);
    }

    private void loadRooms(String typeFilter) {
        List<Room> filtered = HotelDatabase.allRooms.stream()
                .filter(r -> typeFilter == null || r.getRoomType().getName().equalsIgnoreCase(typeFilter))
                .collect(Collectors.toList());
        roomsTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void handleMakeReservation() {
        reservationStatusLabel.setText("");
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            reservationStatusLabel.setText("⚠ Please select a room from the Rooms tab first.");
            reservationStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        if (!selected.isAvailable()) {
            reservationStatusLabel.setText("⚠ This room is not available.");
            reservationStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        LocalDate checkIn = checkInPicker.getValue();
        LocalDate checkOut = checkOutPicker.getValue();
        if (checkIn == null || checkOut == null) {
            reservationStatusLabel.setText("⚠ Please select both check-in and check-out dates.");
            reservationStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        try {
            Reservation res = new Reservation(currentGuest, selected, checkIn, checkOut);
            HotelDatabase.allReservations.add(res);
            currentGuest.addReservation(res);

            reservationStatusLabel.setText("✓ Reservation #" + res.getId() + " created! Total: EGP " + res.getTotalCost());
            reservationStatusLabel.setStyle("-fx-text-fill: #2ecc71;");
            loadRooms(null);
            loadMyReservations();
            updateActiveResCount();
        } catch (InvalidInputException e) {
            reservationStatusLabel.setText("⚠ " + e.getMessage());
            reservationStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private void setupReservationsTable() {
        resIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        resRoomCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getRoom().getRoomNumber()).asObject());
        resCheckInCol.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        resCheckOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        resStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        resTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        myResTable.setRowFactory(tv -> new TableRow<Reservation>() {
            @Override
            protected void updateItem(Reservation item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                switch (item.getStatus()) {
                    case PENDING: setStyle("-fx-background-color: rgba(241,196,15,0.15);"); break;
                    case CONFIRMED: setStyle("-fx-background-color: rgba(46,204,113,0.15);"); break;
                    case CANCELLED: setStyle("-fx-background-color: rgba(231,76,60,0.12);"); break;
                    case COMPLETED: setStyle("-fx-background-color: rgba(52,152,219,0.12);"); break;
                    default: setStyle("");
                }
            }
        });
    }

    private void loadMyReservations() {
        List<Reservation> myRes = HotelDatabase.allReservations.stream()
                .filter(r -> r.getGuest().getUsername().equals(currentGuest.getUsername()))
                .collect(Collectors.toList());
        myResTable.setItems(FXCollections.observableArrayList(myRes));
    }

    @FXML
    private void handleCancelReservation() {
        Reservation selected = myResTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            myResStatusLabel.setText("⚠ Please select a reservation to cancel.");
            myResStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        if (selected.getStatus() == ReservationStatus.CONFIRMED) {
            myResStatusLabel.setText("⚠ Cannot cancel after check-in.");
            myResStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        if (selected.getStatus() == ReservationStatus.COMPLETED) {
            myResStatusLabel.setText("⚠ Reservation already completed.");
            myResStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        if (selected.getStatus() == ReservationStatus.CANCELLED) {
            myResStatusLabel.setText("⚠ Already cancelled.");
            myResStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Cancel reservation #" + selected.getId() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText("Confirm Cancellation");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                selected.cancel();
                loadMyReservations();
                loadRooms(null);
                updateActiveResCount();
                myResStatusLabel.setText("✓ Reservation #" + selected.getId() + " cancelled.");
                myResStatusLabel.setStyle("-fx-text-fill: #2ecc71;");
            }
        });
    }

    private void setupCheckoutTab() {
        paymentCombo.getItems().addAll("CASH", "CARD", "ONLINE");
        paymentCombo.getSelectionModel().selectFirst();
        invoiceBox.setVisible(false);
    }

    @FXML
    private void handleCheckout() {
        checkoutStatusLabel.setText("");
        Reservation selected = myResTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            checkoutStatusLabel.setText("⚠ Please select a confirmed reservation from 'My Reservations'.");
            checkoutStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        if (selected.getStatus() != ReservationStatus.CONFIRMED) {
            checkoutStatusLabel.setText("⚠ Only CONFIRMED reservations can be checked out.");
            checkoutStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        String payStr = paymentCombo.getValue();
        Payment payment = Payment.valueOf(payStr);
        try {
            Invoice invoice = new Invoice(selected, payment);
            HotelDatabase.allInvoices.add(invoice);
            selected.complete();
            DatabaseManager.saveAllData();
            loadMyReservations();
            loadRooms(null);
            updateActiveResCount();

            invoiceLabel.setText(
                    "Invoice #" + selected.getId() + "\n" +
                    "Guest: " + currentGuest.getUsername() + "\n" +
                    "Room: " + selected.getRoom().getRoomNumber() + "\n" +
                    "Nights: " + selected.getNights() + "\n" +
                    "Payment: " + payment + "\n" +
                    "Total: EGP " + selected.getTotalCost() + "\n" +
                    "Date: " + invoice.getPaymentdate()
            );
            invoiceBox.setVisible(true);
            checkoutStatusLabel.setText("✓ Checkout successful!");
            checkoutStatusLabel.setStyle("-fx-text-fill: #2ecc71;");
        } catch (InvalidPaymentException e) {
            checkoutStatusLabel.setText("⚠ Payment error: " + e.getMessage());
            checkoutStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    @FXML
    private void handleConnectChat() {
        if (chatClient != null && chatClient.isConnected()) {
            chatStatusLabel.setText("Already connected.");
            return;
        }
        chatClient = new ChatClient(currentGuest.getUsername(), this::appendChatMessage);
        boolean connected = chatClient.connect();
        if (connected) {
            chatStatusLabel.setText("✓ Connected to chat server.");
            chatStatusLabel.setStyle("-fx-text-fill: #2ecc71;");
        } else {
            chatStatusLabel.setText("⚠ Could not connect. Is the server running?");
            chatStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    @FXML
    private void handleSendChat() {
        String msg = chatInput.getText().trim();
        if (msg.isEmpty()) return;
        if (chatClient == null || !chatClient.isConnected()) {
            chatStatusLabel.setText("⚠ Connect to chat first.");
            chatStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        try {
            chatClient.sendMessage(msg);
            chatInput.clear();
        } catch (Exception e) {
            chatStatusLabel.setText("⚠ Send error: " + e.getMessage());
            chatStatusLabel.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private void appendChatMessage(String message) {
        try {
            chatArea.appendText(message + "\n");
        } catch (Exception e) {
            System.out.println("[Chat] Error appending message: " + e.getMessage());
        }
    }

    private void startRoomAutoRefresh() {
        Task<Void> refreshTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    Thread.sleep(5000); // refresh every 5 seconds
                    Platform.runLater(() -> {
                        String filter = filterTypeCombo.getValue();
                        loadRooms("All Types".equals(filter) ? null : filter);
                    });
                }
                return null;
            }
        };
        Thread t = new Thread(refreshTask);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void handleLogout() {
        if (chatClient != null) chatClient.disconnect();
        SessionManager.logout();
        try {
            SceneManager.switchTo("Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
