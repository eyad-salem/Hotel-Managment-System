import java.sql.*;
import java.time.LocalDate;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:hotel_data/hotel.db";
    private static Connection conn;

    public static void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            createTables();
            loadAllData();
        } catch (Exception e) {
            System.out.println("[DatabaseManager] Error: " + e.getMessage());
        }
    }

    private static void createTables() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS room_types (name TEXT PRIMARY KEY, price REAL, capacity INTEGER)");
        stmt.execute("CREATE TABLE IF NOT EXISTS amenities (name TEXT PRIMARY KEY)");
        stmt.execute("CREATE TABLE IF NOT EXISTS rooms (room_number INTEGER PRIMARY KEY, type_name TEXT, available INTEGER, FOREIGN KEY(type_name) REFERENCES room_types(name))");
        stmt.execute("CREATE TABLE IF NOT EXISTS room_amenities (room_number INTEGER, amenity_name TEXT, PRIMARY KEY(room_number, amenity_name))");
        stmt.execute("CREATE TABLE IF NOT EXISTS guests (username TEXT PRIMARY KEY, password TEXT, dob TEXT, address TEXT, gender TEXT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS staff (username TEXT PRIMARY KEY, password TEXT, role TEXT, dob TEXT, working_hours INTEGER)");
        stmt.execute("CREATE TABLE IF NOT EXISTS reservations (id INTEGER PRIMARY KEY, guest_username TEXT, room_number INTEGER, check_in TEXT, check_out TEXT, status TEXT, total_cost REAL, FOREIGN KEY(guest_username) REFERENCES guests(username), FOREIGN KEY(room_number) REFERENCES rooms(room_number))");
        stmt.execute("CREATE TABLE IF NOT EXISTS invoices (reservation_id INTEGER PRIMARY KEY, payment TEXT, total REAL, payment_date TEXT, FOREIGN KEY(reservation_id) REFERENCES reservations(id))");
    }

    public static void saveAllData() {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE FROM room_amenities");
            stmt.execute("DELETE FROM invoices");
            stmt.execute("DELETE FROM reservations");
            stmt.execute("DELETE FROM rooms");
            stmt.execute("DELETE FROM amenities");
            stmt.execute("DELETE FROM room_types");
            stmt.execute("DELETE FROM staff");
            stmt.execute("DELETE FROM guests");

            for (RoomType rt : HotelDatabase.allRoomTypes) {
                stmt.execute(String.format("INSERT INTO room_types VALUES ('%s', %f, %d)", rt.getName(), rt.getPricePerNight(), rt.getCapacity()));
            }
            for (Amenity a : HotelDatabase.allAmenities) {
                stmt.execute(String.format("INSERT INTO amenities VALUES ('%s')", a.getName()));
            }
            for (Room r : HotelDatabase.allRooms) {
                stmt.execute(String.format("INSERT INTO rooms VALUES (%d, '%s', %b)", r.getRoomNumber(), r.getRoomType().getName(), r.isAvailable()));
                for (Amenity a : r.getAmenities()) {
                    stmt.execute(String.format("INSERT INTO room_amenities VALUES (%d, '%s')", r.getRoomNumber(), a.getName()));
                }
            }
            for (Guest g : HotelDatabase.allGuests) {
                stmt.execute(String.format("INSERT INTO guests VALUES ('%s', '%s', '%s', '%s', '%s')", g.getUsername(), g.getPassword(), g.getDateOfBirth(), g.getAddress(), g.getGender()));
            }
            for (Staff s : HotelDatabase.allStaffs) {
                String role = s instanceof Admin ? "ADMIN" : (s instanceof Housekeeper ? "HOUSEKEEPER" : "RECEPTIONIST");
                stmt.execute(String.format("INSERT INTO staff VALUES ('%s', '%s', '%s', '%s', %d)", s.getUsername(), s.getPassword(), role, s.getDob(), s.getWorkinghours()));
            }
            for (Reservation r : HotelDatabase.allReservations) {
                stmt.execute(String.format("INSERT INTO reservations VALUES (%d, '%s', %d, '%s', '%s', '%s', %f)", r.getId(), r.getGuest().getUsername(), r.getRoom().getRoomNumber(), r.getCheckInDate(), r.getCheckOutDate(), r.getStatus(), r.getTotalCost()));
            }
            for (Invoice inv : HotelDatabase.allInvoices) {
                stmt.execute(String.format("INSERT INTO invoices VALUES (%d, '%s', %f, '%s')", inv.getReservation().getId(), inv.getPayment(), inv.getTotalamount(), inv.getPaymentdate()));
            }
        } catch (SQLException e) {
            System.out.println("[DatabaseManager] Save error: " + e.getMessage());
        }
    }

    private static void loadAllData() {
        try {
            loadRoomTypes();
            loadAmenities();
            loadRooms();
            loadGuests();
            loadStaff();
            loadReservations();
            loadInvoices();

            if (HotelDatabase.allGuests.isEmpty() && HotelDatabase.allStaffs.isEmpty() && HotelDatabase.allRooms.isEmpty()) {
                HotelDatabase.initialize();
                saveAllData();
                System.out.println("[DatabaseManager] Initialized with default data and saved to SQLite.");
            } else {
                System.out.println("[DatabaseManager] Loaded " + HotelDatabase.allGuests.size() + " guests, " + HotelDatabase.allStaffs.size() + " staff, " + HotelDatabase.allRooms.size() + " rooms from database.");
            }
        } catch (Exception e) {
            System.out.println("[DatabaseManager] Load error: " + e.getMessage());
        }
    }

    private static void loadRoomTypes() throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM room_types");
        while (rs.next()) {
            RoomType rt = new RoomType(rs.getString("name"), rs.getDouble("price"), rs.getInt("capacity"));
            HotelDatabase.allRoomTypes.add(rt);
        }
    }

    private static void loadAmenities() throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM amenities");
        while (rs.next()) {
            HotelDatabase.allAmenities.add(new Amenity(rs.getString("name")));
        }
    }

    private static void loadRooms() throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM rooms");
        while (rs.next()) {
            RoomType rt = HotelDatabase.findRoomTypeByName(rs.getString("type_name"));
            if (rt != null) {
                Room r = new Room(rs.getInt("room_number"), rt);
                Statement stmt2 = conn.createStatement();
                ResultSet rs2 = stmt2.executeQuery("SELECT amenity_name FROM room_amenities WHERE room_number = " + r.getRoomNumber());
                while (rs2.next()) {
                    Amenity a = HotelDatabase.findAmenityByName(rs2.getString("amenity_name"));
                    if (a != null) r.addAmenity(a);
                }
                HotelDatabase.allRooms.add(r);
            }
        }
    }

    private static void loadGuests() throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM guests");
        while (rs.next()) {
            Guest g = new Guest(rs.getString("username"), rs.getString("password"), LocalDate.parse(rs.getString("dob")), rs.getString("address"), Gender.valueOf(rs.getString("gender")));
            HotelDatabase.allGuests.add(g);
        }
    }

    private static void loadStaff() throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM staff");
        while (rs.next()) {
            String username = rs.getString("username");
            String password = rs.getString("password");
            LocalDate dob = LocalDate.parse(rs.getString("dob"));
            int hours = rs.getInt("working_hours");
            String role = rs.getString("role");
            if ("ADMIN".equals(role)) {
                HotelDatabase.allStaffs.add(new Admin(username, password, dob.getYear(), dob.getMonthValue(), dob.getDayOfMonth(), hours));
            } else if ("HOUSEKEEPER".equals(role)) {
                HotelDatabase.allStaffs.add(new Housekeeper(username, password, dob.getYear(), dob.getMonthValue(), dob.getDayOfMonth(), hours));
            } else {
                HotelDatabase.allStaffs.add(new Receptionist(username, password, dob.getYear(), dob.getMonthValue(), dob.getDayOfMonth(), hours));
            }
        }
    }

    private static void loadReservations() throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM reservations");
        while (rs.next()) {
            Guest g = Guest.findGuestByUsername(rs.getString("guest_username"));
            Room room = HotelDatabase.findRoomByNumber(rs.getInt("room_number"));
            if (g != null && room != null) {
                Reservation r = new Reservation(g, room, LocalDate.parse(rs.getString("check_in")), LocalDate.parse(rs.getString("check_out")));
                HotelDatabase.allReservations.add(r);
            }
        }
    }

    private static void loadInvoices() throws Exception {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM invoices");
        while (rs.next()) {
            int resId = rs.getInt("reservation_id");
            for (Reservation r : HotelDatabase.allReservations) {
                if (r.getId() == resId) {
                    Invoice inv = new Invoice(r, Payment.valueOf(rs.getString("payment")));
                    HotelDatabase.allInvoices.add(inv);
                    break;
                }
            }
        }
    }
}