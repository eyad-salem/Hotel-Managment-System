import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardController {

    @FXML private Label adminNameLabel;

    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, Integer> roomNumCol;
    @FXML private TableColumn<Room, String> roomTypeCol;
    @FXML private TableColumn<Room, Double> roomPriceCol;
    @FXML private TableColumn<Room, String> roomAvailCol;
    @FXML private TableColumn<Room, String> roomAmenCol;
    @FXML private TextField addRoomNumField;
    @FXML private ComboBox<String> addRoomTypeCombo;
    @FXML private ComboBox<String> roomAmenityCombo;
    @FXML private Label roomStatusLabel;

    @FXML private TableView<RoomType> roomTypeTable;
    @FXML private TableColumn<RoomType, String> rtNameCol;
    @FXML private TableColumn<RoomType, Double> rtPriceCol;
    @FXML private TableColumn<RoomType, Integer> rtCapCol;
    @FXML private TextField rtNameField;
    @FXML private TextField rtPriceField;
    @FXML private TextField rtCapField;
    @FXML private Label rtStatusLabel;

    @FXML private TableView<Amenity> amenityTable;
    @FXML private TableColumn<Amenity, String> amenNameCol;
    @FXML private TextField amenNameField;
    @FXML private Label amenStatusLabel;

    @FXML private TableView<Guest> guestsTable;
    @FXML private TableColumn<Guest, String> guestUserCol;
    @FXML private TableColumn<Guest, LocalDate> guestDobCol;
    @FXML private TableColumn<Guest, Gender> guestGenderCol;
    @FXML private TableColumn<Guest, String> guestAddressCol;

    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, Integer> resIdCol;
    @FXML private TableColumn<Reservation, String> resGuestCol;
    @FXML private TableColumn<Reservation, Integer> resRoomCol;
    @FXML private TableColumn<Reservation, LocalDate> resInCol;
    @FXML private TableColumn<Reservation, LocalDate> resOutCol;
    @FXML private TableColumn<Reservation, String> resStatusCol;
    @FXML private TableColumn<Reservation, Double> resTotalCol;

    @FXML private TextArea chatArea;
    @FXML private TextField chatInput;
    @FXML private Label chatStatusLabel;

    private ChatClient chatClient;

    @FXML
    public void initialize() {
        System.out.println("[AdminDashboard] Initializing...");
        Staff staff = SessionManager.getCurrentStaff();
        if (staff != null) adminNameLabel.setText("Admin: " + staff.getUsername());

        setupRoomsTable();
        setupRoomTypesTable();
        setupAmenityTable();
        setupGuestsTable();
        setupReservationsTable();

        loadAll();
        updateAmenityCombo();
    }

    @FXML
    private void handleRefresh() {
        loadAll();
    }

    private void setupRoomsTable() {
        roomNumCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomNumCol.setCellFactory(col -> new TableCell<Room, Integer>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setText(null);
                else { setText(String.valueOf(item)); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        roomNumCol.setResizable(false);
        
        roomTypeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getRoomType().getName()));
        roomTypeCol.setCellFactory(col -> new TableCell<Room, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setText(null);
                else { setText(item); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        roomTypeCol.setResizable(false);
        
        roomPriceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        roomPriceCol.setCellFactory(col -> new TableCell<Room, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setText(null);
                else { setText(String.valueOf(item)); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        roomPriceCol.setResizable(false);
        
        roomAvailCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().isAvailable() ? "✓ Available" : "✗ Occupied"));
        roomAvailCol.setCellFactory(col -> new TableCell<Room, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setText(null);
                else { setText(item); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        roomAvailCol.setResizable(false);
        
        roomAmenCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAmenities().toString()));
        roomAmenCol.setCellFactory(col -> new TableCell<Room, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setText(null);
                else { setText(item); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        roomAmenCol.setResizable(false);

        roomsTable.setRowFactory(tv -> new TableRow<Room>() {
            @Override protected void updateItem(Room item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setStyle("");
                else setStyle(item.isAvailable() ? "-fx-background-color: rgba(46,204,113,0.12);" : "-fx-background-color: rgba(231,76,60,0.1);");
            }
        });
    }

    @FXML
    private void handleAddRoom() {
        String numStr = addRoomNumField.getText().trim();
        String typeName = addRoomTypeCombo.getValue();

        if (numStr.isEmpty()) {
            setRoomStatus("Please enter room number.", "#e74c3c");
            return;
        }

        if (typeName == null || typeName.isEmpty()) {
            setRoomStatus("Please select room type.", "#e74c3c");
            return;
        }

        try {
            int roomNum = Integer.parseInt(numStr);

            if (HotelDatabase.findRoomByNumber(roomNum) != null) {
                setRoomStatus("Room #" + roomNum + " already exists!", "#e74c3c");
                return;
            }

            RoomType rt = HotelDatabase.findRoomTypeByName(typeName);
            if (rt == null) {
                setRoomStatus("Room type not found.", "#e74c3c");
                return;
            }

            Room newRoom = new Room(roomNum, rt);
            String selectedAmenity = roomAmenityCombo.getValue();
            if (selectedAmenity != null && !selectedAmenity.isEmpty()) {
                Amenity amenity = HotelDatabase.findAmenityByName(selectedAmenity);
                if (amenity != null) {
                    newRoom.addAmenity(amenity);
                }
            }
            HotelDatabase.allRooms.add(newRoom);
            addRoomNumField.clear();
            loadRooms();
            setRoomStatus("✓ Room #" + roomNum + " added successfully with " + (selectedAmenity != null ? selectedAmenity : "no") + " amenity!", "#2ecc71");
        } catch (NumberFormatException e) {
            setRoomStatus("Invalid room number.", "#e74c3c");
        }
    }

    @FXML
    private void handleAddAmenityToRoom() {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setRoomStatus("Please select a room first.", "#e74c3c");
            return;
        }
        String amenityName = roomAmenityCombo.getValue();
        if (amenityName == null || amenityName.isEmpty()) {
            setRoomStatus("Please select an amenity.", "#e74c3c");
            return;
        }
        Amenity amenity = HotelDatabase.findAmenityByName(amenityName);
        if (amenity == null) {
            setRoomStatus("Amenity not found.", "#e74c3c");
            return;
        }
        selected.addAmenity(amenity);
        loadRooms();
        setRoomStatus("✓ Amenity added to Room #" + selected.getRoomNumber(), "#2ecc71");
    }

    @FXML
    private void handleRemoveAmenityFromRoom() {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setRoomStatus("Please select a room first.", "#e74c3c");
            return;
        }
        String amenityName = roomAmenityCombo.getValue();
        if (amenityName == null || amenityName.isEmpty()) {
            setRoomStatus("Please select an amenity to remove.", "#e74c3c");
            return;
        }
        Amenity amenity = HotelDatabase.findAmenityByName(amenityName);
        if (amenity != null) {
            selected.removeAmenity(amenity);
            loadRooms();
            setRoomStatus("✓ Amenity removed from Room #" + selected.getRoomNumber(), "#2ecc71");
        }
    }

    @FXML
    private void handleRemoveRoom() {
        Room selected = roomsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setRoomStatus("Please select a room to remove.", "#e74c3c");
            return;
        }

        boolean hasActiveRes = HotelDatabase.allReservations.stream()
                .filter(r -> r.getRoom().getRoomNumber() == selected.getRoomNumber())
                .anyMatch(r -> r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.CONFIRMED);

        if (hasActiveRes) {
            setRoomStatus("Cannot remove room with active reservations!", "#e74c3c");
            return;
        }

        HotelDatabase.allRooms.remove(selected);
        loadRooms();
        setRoomStatus("✓ Room #" + selected.getRoomNumber() + " removed.", "#2ecc71");
    }

    private void setRoomStatus(String msg, String color) {
        roomStatusLabel.setText(msg);
        roomStatusLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    private void loadRooms() {
        roomsTable.setItems(FXCollections.observableArrayList(HotelDatabase.allRooms));
    }

    private void setupRoomTypesTable() {
        rtNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        rtNameCol.setCellFactory(col -> new javafx.scene.control.TableCell<RoomType, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setText(null); }
                else { setText(item); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        rtNameCol.setResizable(false);
        rtPriceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        rtPriceCol.setCellFactory(col -> new javafx.scene.control.TableCell<RoomType, Double>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setText(null); }
                else { setText(String.valueOf(item)); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        rtPriceCol.setResizable(false);
        rtCapCol.setCellValueFactory(d -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().getCapacity()).asObject());
        rtCapCol.setCellFactory(col -> new javafx.scene.control.TableCell<RoomType, Integer>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setText(null); }
                else { setText(String.valueOf(item)); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        rtCapCol.setResizable(false);
    }

    @FXML
    private void handleAddRoomType() {
        String name = rtNameField.getText().trim();
        String priceStr = rtPriceField.getText().trim();
        String capStr = rtCapField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || capStr.isEmpty()) {
            setRtStatus("All fields required.", "#e74c3c");
            return;
        }

        if (HotelDatabase.findRoomTypeByName(name) != null) {
            setRtStatus("Room type '" + name + "' already exists!", "#e74c3c");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int capacity = Integer.parseInt(capStr);

            if (price <= 0 || capacity <= 0) {
                setRtStatus("Price and capacity must be positive.", "#e74c3c");
                return;
            }

            RoomType rt = new RoomType(name, price, capacity);
            HotelDatabase.allRoomTypes.add(rt);
            clearRoomTypeFields();
            loadRoomTypes();
            updateRoomTypeCombo();
            setRtStatus("✓ Room type '" + name + "' added!", "#2ecc71");
        } catch (NumberFormatException e) {
            setRtStatus("Invalid price or capacity.", "#e74c3c");
        }
    }

    @FXML
    private void handleUpdateRoomType() {
        RoomType selected = roomTypeTable.getSelectionModel().getSelectedItem();
        String name = rtNameField.getText().trim();
        String priceStr = rtPriceField.getText().trim();
        String capStr = rtCapField.getText().trim();

        if (selected == null) {
            setRtStatus("Select a room type to update.", "#e74c3c");
            return;
        }

        if (!name.isEmpty()) {
            if (HotelDatabase.findRoomTypeByName(name) != null && !name.equals(selected.getName())) {
                setRtStatus("Room type name already exists.", "#e74c3c");
                return;
            }
            selected.setName(name);
        }

        if (!priceStr.isEmpty()) {
            try {
                double price = Double.parseDouble(priceStr);
                if (price > 0) selected.setPricePerNight(price);
            } catch (NumberFormatException e) {
                setRtStatus("Invalid price.", "#e74c3c");
                return;
            }
        }

        if (!capStr.isEmpty()) {
            try {
                int cap = Integer.parseInt(capStr);
                if (cap > 0) selected.setCapacity(cap);
            } catch (NumberFormatException e) {
                setRtStatus("Invalid capacity.", "#e74c3c");
                return;
            }
        }

        loadRoomTypes();
        updateRoomTypeCombo();
        clearRoomTypeFields();
        setRtStatus("✓ Room type updated!", "#2ecc71");
    }

    @FXML
    private void handleRemoveRoomType() {
        RoomType selected = roomTypeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setRtStatus("Select a room type to remove.", "#e74c3c");
            return;
        }

        boolean inUse = HotelDatabase.allRooms.stream()
                .anyMatch(r -> r.getRoomType().getName().equals(selected.getName()));

        if (inUse) {
            setRtStatus("Cannot remove - room type is in use!", "#e74c3c");
            return;
        }

        HotelDatabase.allRoomTypes.remove(selected);
        loadRoomTypes();
        updateRoomTypeCombo();
        setRtStatus("✓ Room type removed.", "#2ecc71");
    }

    private void setRtStatus(String msg, String color) {
        rtStatusLabel.setText(msg);
        rtStatusLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    private void clearRoomTypeFields() {
        rtNameField.clear();
        rtPriceField.clear();
        rtCapField.clear();
    }

    private void loadRoomTypes() {
        roomTypeTable.setItems(FXCollections.observableArrayList(HotelDatabase.allRoomTypes));
    }

    private void updateRoomTypeCombo() {
        addRoomTypeCombo.getItems().clear();
        for (RoomType rt : HotelDatabase.allRoomTypes) {
            addRoomTypeCombo.getItems().add(rt.getName());
        }
        if (!addRoomTypeCombo.getItems().isEmpty()) {
            addRoomTypeCombo.getSelectionModel().selectFirst();
        }
    }

    private void updateAmenityCombo() {
        roomAmenityCombo.getItems().clear();
        for (Amenity a : HotelDatabase.allAmenities) {
            roomAmenityCombo.getItems().add(a.getName());
        }
        if (!roomAmenityCombo.getItems().isEmpty()) {
            roomAmenityCombo.getSelectionModel().selectFirst();
        }
    }

    private void setupAmenityTable() {
        amenNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    @FXML
    private void handleAddAmenity() {
        String name = amenNameField.getText().trim();
        if (name.isEmpty()) {
            setAmenStatus("Please enter amenity name.", "#e74c3c");
            return;
        }

        if (HotelDatabase.findAmenityByName(name) != null) {
            setAmenStatus("Amenity '" + name + "' already exists!", "#e74c3c");
            return;
        }

        Amenity amenity = new Amenity(name);
        HotelDatabase.allAmenities.add(amenity);
        amenNameField.clear();
        loadAmenities();
        updateAmenityCombo();
        setAmenStatus("✓ Amenity '" + name + "' added!", "#2ecc71");
    }

    @FXML
    private void handleUpdateAmenity() {
        Amenity selected = amenityTable.getSelectionModel().getSelectedItem();
        String name = amenNameField.getText().trim();

        if (selected == null) {
            setAmenStatus("Select an amenity to update.", "#e74c3c");
            return;
        }

        if (name.isEmpty()) {
            setAmenStatus("Please enter new name.", "#e74c3c");
            return;
        }

        if (HotelDatabase.findAmenityByName(name) != null && !name.equals(selected.getName())) {
            setAmenStatus("Amenity name already exists.", "#e74c3c");
            return;
        }

        try {
            selected.setName(name);
            amenNameField.clear();
            loadAmenities();
            setAmenStatus("✓ Amenity updated!", "#2ecc71");
        } catch (InvalidInputException e) {
            setAmenStatus("Error: " + e.getMessage(), "#e74c3c");
        }
    }

    @FXML
    private void handleRemoveAmenity() {
        Amenity selected = amenityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setAmenStatus("Select an amenity to remove.", "#e74c3c");
            return;
        }

        HotelDatabase.allAmenities.remove(selected);
        loadAmenities();
        setAmenStatus("✓ Amenity removed.", "#2ecc71");
    }

    private void setAmenStatus(String msg, String color) {
        amenStatusLabel.setText(msg);
        amenStatusLabel.setStyle("-fx-text-fill: " + color + ";");
    }

    private void loadAmenities() {
        amenityTable.setItems(FXCollections.observableArrayList(HotelDatabase.allAmenities));
    }

    private void setupGuestsTable() {
        guestUserCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        guestUserCol.setCellFactory(col -> new TableCell<Guest, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setText(null);
                else { setText(item); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        guestUserCol.setResizable(false);
        
        guestDobCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        guestDobCol.setCellFactory(col -> new TableCell<Guest, LocalDate>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setText(null);
                else { setText(item.toString()); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        guestDobCol.setResizable(false);
        
        guestGenderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        guestGenderCol.setCellFactory(col -> new TableCell<Guest, Gender>() {
            @Override protected void updateItem(Gender item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setText(null);
                else { setText(item.toString()); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        guestGenderCol.setResizable(false);
        
        guestAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        guestAddressCol.setCellFactory(col -> new TableCell<Guest, String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setText(null);
                else { setText(item); setAlignment(javafx.geometry.Pos.CENTER); }
            }
        });
        guestAddressCol.setResizable(false);
    }

    private void loadGuests() {
        guestsTable.setItems(FXCollections.observableArrayList(HotelDatabase.allGuests));
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
                    case CONFIRMED: setStyle("-fx-background-color: rgba(46,204,113,0.2);"); break;
                    case CANCELLED: setStyle("-fx-background-color: rgba(231,76,60,0.15);"); break;
                    case COMPLETED: setStyle("-fx-background-color: rgba(52,152,219,0.15);"); break;
                    default: setStyle("");
                }
            }
        });
    }

    private void loadReservations() {
        reservationsTable.setItems(FXCollections.observableArrayList(HotelDatabase.allReservations));
    }

    @FXML
    private void handleConnectChat() {
        if (chatClient != null && chatClient.isConnected()) {
            chatStatusLabel.setText("Already connected.");
            return;
        }
        chatClient = new ChatClient("Admin", this::appendChatMessage);
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
        chatClient.sendMessage(msg);
        chatInput.clear();
    }

    private void appendChatMessage(String message) {
        chatArea.appendText(message + "\n");
    }

    private void loadAll() {
        loadRooms();
        loadRoomTypes();
        loadAmenities();
        loadGuests();
        loadReservations();
        updateRoomTypeCombo();
        updateAmenityCombo();
    }

    private Timeline autoRefreshTimeline;

    private void startAutoRefresh() {
        System.out.println("[AdminDashboard] Starting auto-refresh...");
        autoRefreshTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            System.out.println("[AdminDashboard] Auto-refresh triggered");
            loadAll();
        }));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    @FXML
    private void handleLogout() {
        if (autoRefreshTimeline != null) autoRefreshTimeline.stop();
        if (chatClient != null) chatClient.disconnect();
        SessionManager.logout();
        try {
            SceneManager.switchTo("Login.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}