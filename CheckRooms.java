import java.sql.*;
public class CheckRooms {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        Connection c = DriverManager.getConnection("jdbc:sqlite:hotel_data/hotel.db");
        Statement stmt = c.createStatement();
        
        ResultSet rs = stmt.executeQuery("SELECT * FROM rooms");
        System.out.println("Rooms in database:");
        while (rs.next()) {
            System.out.println("Room: " + rs.getInt(1) + ", Type: " + rs.getString(2) + ", Available: " + rs.getBoolean(3));
        }
        
        rs = stmt.executeQuery("SELECT * FROM room_types");
        System.out.println("\nRoom types in database:");
        while (rs.next()) {
            System.out.println("Type: " + rs.getString(1) + ", Price: " + rs.getDouble(2) + ", Capacity: " + rs.getInt(3));
        }
        
        c.close();
    }
}