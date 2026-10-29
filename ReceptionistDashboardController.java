import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDate;

public class ReceptionistDashboardController {

    @FXML private Label recepNameLabel;

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, Integer> roomNumCol;
    @FXML private TableColumn<Room, String> roomTypeCol;
    @FXML private TableColumn<Room, Double> roomPriceCol;
    @FXML private TableColumn<Room, String> roomAvailCol;

    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest, String> guestUserCol;
    @FXML private TableColumn<Guest, LocalDate> guestDobCol;
    @FXML private TableColumn<Guest, String> guestAddrCol;

    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, Integer> resIdCol;
    @FXML private TableColumn<Reservation, String> resGuestCol;
    @FXML private TableColumn<Reservation, Integer> resRoomCol;
    @FXML private TableColumn<Reservation, LocalDate> resInCol;
    @FXML private TableColumn<Reservation, LocalDate> resOutCol;
    @FXML private TableColumn<Reservation, String> resStatusCol;
    @FXML private TableColumn<Reservation, Double> resTotalCol;
    @FXML private Label resStatusLabel;

    @FXML private Label checkInStatusLabel;
    @FXML private Label checkOutStatusLabel;
    @FXML private ComboBox<String> checkoutPaymentCombo;
    @FXML private Label invoiceLabel;

    @FXML private TextArea chatArea;
    @FXML private TextField chatInput;
    @FXML private Label chatStatusLabel;

    private ChatClient chatClient;

    @FXML
    public void initialize() {
        Staff staff = SessionManager.getCurrentStaff();
        if (staff != null) recepNameLabel.setText("Receptionist: " + staff.getUsername());

        setupRoomsTable();
        setupGuestsTable();
        setupReservationsTable();
        checkoutPaymentCombo.getItems().addAll("CASH", "CARD", "ONLINE");
        checkoutPaymentCombo.getSelectionModel().selectFirst();
        loadAll();
        startAutoRefresh();
    }

    private void setupRoomsTable() {
        roomNumCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRoomType().getName()));
        roomPriceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        roomAvailCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().isAvailable() ? "✓ Available" : "✗ Occupied"));
        roomsTable.setRowFactory(tv -> new TableRow<Room>() {
            @Override protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setStyle("");
                else setStyle(item.isAvailable() ? "-fx-background-color: rgba(46,204,113,0.12);" : "-fx-background-color: rgba(231,76,60,0.1);");
            }
        });
    }

    private void setupGuestsTable() {
        guestUserCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        guestDobCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        guestAddrCol.setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    private void setupReservationsTable() {
        resIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        resGuestCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getGuest().getUsername()));
        resRoomCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getRoom().getRoomNumber()).asObject());
        resInCol.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        resOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        resStatusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus().toString()));
        resTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        reservationsTable.setRowFactory(tv -> new TableRow<Reservation>() {
            @Override protected void updateItem(Reservation item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setStyle(""); return; }
                switch (item.getStatus()) {
                    case PENDING: setStyle("-fx-background-color: rgba(241,196,15,0.2);"); break;
                    case CONFIRMED: setStyle("-fx-background-color: rgba(46,204,113,0.15);"); break;
                    case CANCELLED: setStyle("-fx-background-color: rgba(231,76,60,0.1);"); break;
                    case COMPLETED: setStyle("-fx-background-color: rgba(52,152,219,0.1);"); break;
                    default: setStyle("");
                }
            }
        });
    }

    private void loadAll() {
        roomsTable.setItems(FXCollections.observableArrayList(HotelDatabase.allRooms));
        guestsTable.setItems(FXCollections.observableArrayList(HotelDatabase.allGuests));
        reservationsTable.setItems(FXCollections.observableArrayList(HotelDatabase.allReservations));
    }

    @FXML
    private void handleCheckIn() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus(checkInStatusLabel, "⚠ Select a PENDING reservation.", false); return;
        }
        if (selected.getStatus() != ReservationStatus.PENDING) {
            setStatus(checkInStatusLabel, "⚠ Only PENDING reservations can be checked in.", false); return;
        }
        LocalDate today = LocalDate.now();
        LocalDate checkIn = selected.getCheckInDate();
        if (today.isBefore(checkIn.minusDays(1))) {
            setStatus(checkInStatusLabel, "⚠ Too early. Check-in date: " + checkIn, false); return;
        }
        if (today.isAfter(checkIn.plusDays(1))) {
            setStatus(checkInStatusLabel, "⚠ Check-in date passed. Make a new reservation.", false); return;
        }
        selected.confirm();
        loadAll();
        setStatus(checkInStatusLabel, "✓ Guest " + selected.getGuest().getUsername() + " checked in to Room #" + selected.getRoom().getRoomNumber(), true);
    }

    @FXML
    private void handleCheckOut() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus(checkOutStatusLabel, "⚠ Select a CONFIRMED reservation.", false); return;
        }
        if (selected.getStatus() != ReservationStatus.CONFIRMED) {
            setStatus(checkOutStatusLabel, "⚠ Only CONFIRMED reservations can be checked out.", false); return;
        }
        String payStr = checkoutPaymentCombo.getValue();
        Payment payment = Payment.valueOf(payStr);
        try {
            Invoice invoice = new Invoice(selected, payment);
            HotelDatabase.allInvoices.add(invoice);
            selected.complete();
            DatabaseManager.saveAllData();
            loadAll();
            invoiceLabel.setText(
                    "─── Invoice ───\n" +
                    "Res #" + selected.getId() + "\n" +
                    "Guest: " + selected.getGuest().getUsername() + "\n" +
                    "Room: " + selected.getRoom().getRoomNumber() + "\n" +
                    "Nights: " + selected.getNights() + "\n" +
                    "Payment: " + payment + "\n" +
                    "Total: EGP " + selected.getTotalCost() + "\n" +
                    "Date: " + invoice.getPaymentdate()
            );
            setStatus(checkOutStatusLabel, "✓ Check-out complete!", true);
        } catch (InvalidPaymentException e) {
            setStatus(checkOutStatusLabel, "⚠ " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleConnectChat() {
        if (chatClient != null && chatClient.isConnected()) {
            setStatus(chatStatusLabel, "Already connected.", true); return;
        }
        Staff staff = SessionManager.getCurrentStaff();
        chatClient = new ChatClient("[Receptionist] " + staff.getUsername(), msg -> chatArea.appendText(msg + "\n"));
        boolean ok = chatClient.connect();
        setStatus(chatStatusLabel, ok ? "✓ Connected." : "⚠ Could not connect.", ok);
    }

    @FXML
    private void handleSendChat() {
        String msg = chatInput.getText().trim();
        if (msg.isEmpty()) return;
        if (chatClient == null || !chatClient.isConnected()) {
            setStatus(chatStatusLabel, "⚠ Connect first.", false); return;
        }
        chatClient.sendMessage(msg);
        chatInput.clear();
    }

    private void startAutoRefresh() {
        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                while (!isCancelled()) {
                    Thread.sleep(4000);
                    Platform.runLater(() -> loadAll());
                }
                return null;
            }
        };
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    private void setStatus(Label label, String msg, boolean success) {
        label.setText(msg);
        label.setStyle("-fx-text-fill: " + (success ? "#2ecc71" : "#e74c3c") + ";");
    }

    @FXML
    private void handleLogout() {
        if (chatClient != null) chatClient.disconnect();
        SessionManager.logout();
        try { SceneManager.switchTo("Login.fxml"); } catch (IOException e) { e.printStackTrace(); }
    }
}
