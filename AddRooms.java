import java.sql.*;
public class AddRooms {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        Connection c = DriverManager.getConnection("jdbc:sqlite:hotel_data/hotel.db");
        Statement stmt = c.createStatement();
        
        // Insert room types
        stmt.execute("INSERT INTO room_types VALUES ('Single', 100.0, 2)");
        stmt.execute("INSERT INTO room_types VALUES ('Double', 200.0, 4)");
        stmt.execute("INSERT INTO room_types VALUES ('Deluxe', 400.0, 8)");
        stmt.execute("INSERT INTO room_types VALUES ('Family', 800.0, 10)");
        
        // Insert rooms
        stmt.execute("INSERT INTO rooms VALUES (100, 'Single', 1)");
        stmt.execute("INSERT INTO rooms VALUES (101, 'Single', 1)");
        stmt.execute("INSERT INTO rooms VALUES (200, 'Double', 1)");
        stmt.execute("INSERT INTO rooms VALUES (201, 'Double', 1)");
        stmt.execute("INSERT INTO rooms VALUES (300, 'Deluxe', 1)");
        stmt.execute("INSERT INTO rooms VALUES (301, 'Deluxe', 1)");
        stmt.execute("INSERT INTO rooms VALUES (400, 'Family', 1)");
        stmt.execute("INSERT INTO rooms VALUES (401, 'Family', 1)");
        
        // Add amenities
        stmt.execute("INSERT INTO amenities VALUES ('TV')");
        stmt.execute("INSERT INTO amenities VALUES ('Mini-bar')");
        stmt.execute("INSERT INTO amenities VALUES ('Wifi')");
        stmt.execute("INSERT INTO amenities VALUES ('Breakfast')");
        
        // Add room amenities
        stmt.execute("INSERT INTO room_amenities VALUES (100, 'TV')");
        stmt.execute("INSERT INTO room_amenities VALUES (100, 'Wifi')");
        stmt.execute("INSERT INTO room_amenities VALUES (101, 'Mini-bar')");
        stmt.execute("INSERT INTO room_amenities VALUES (101, 'Breakfast')");
        stmt.execute("INSERT INTO room_amenities VALUES (200, 'TV')");
        stmt.execute("INSERT INTO room_amenities VALUES (200, 'Breakfast')");
        stmt.execute("INSERT INTO room_amenities VALUES (201, 'Mini-bar')");
        stmt.execute("INSERT INTO room_amenities VALUES (201, 'Breakfast')");
        stmt.execute("INSERT INTO room_amenities VALUES (300, 'Wifi')");
        stmt.execute("INSERT INTO room_amenities VALUES (300, 'TV')");
        stmt.execute("INSERT INTO room_amenities VALUES (301, 'Mini-bar')");
        stmt.execute("INSERT INTO room_amenities VALUES (301, 'Breakfast')");
        stmt.execute("INSERT INTO room_amenities VALUES (400, 'TV')");
        stmt.execute("INSERT INTO room_amenities VALUES (400, 'Breakfast')");
        stmt.execute("INSERT INTO room_amenities VALUES (401, 'Mini-bar')");
        stmt.execute("INSERT INTO room_amenities VALUES (401, 'Breakfast')");
        
        System.out.println("Added 4 room types, 8 rooms, 4 amenities, and room amenities");
        
        c.close();
    }
}