import java.sql.*;
public class RestructureUsers {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        Connection c = DriverManager.getConnection("jdbc:sqlite:hotel_data/hotel.db");
        Statement stmt = c.createStatement();
        
        System.out.println("=== Phase 2: Clearing existing user data ===");
        stmt.execute("DELETE FROM guests");
        stmt.execute("DELETE FROM staff");
        System.out.println("Cleared all existing users.");
        
        System.out.println("\n=== Phase 3: Creating 10 Guest Accounts ===");
        stmt.execute("INSERT INTO guests VALUES ('guest', 'guest123', '2000-01-01', 'Cairo, Egypt', 'MALE')");
        stmt.execute("INSERT INTO guests VALUES ('mohamed', 'moh123', '1995-03-15', 'Alexandria, Egypt', 'MALE')");
        stmt.execute("INSERT INTO guests VALUES ('ahmed', 'ahmed123', '1998-07-22', 'Giza, Egypt', 'MALE')");
        stmt.execute("INSERT INTO guests VALUES ('omar', 'omar123', '1992-11-08', 'Mansoura, Egypt', 'MALE')");
        stmt.execute("INSERT INTO guests VALUES ('ali', 'ali123', '1997-04-30', 'Tanta, Egypt', 'MALE')");
        stmt.execute("INSERT INTO guests VALUES ('fatima', 'fatima123', '2001-09-12', 'Cairo, Egypt', 'FEMALE')");
        stmt.execute("INSERT INTO guests VALUES ('sara', 'sara123', '1999-06-25', 'Suez, Egypt', 'FEMALE')");
        stmt.execute("INSERT INTO guests VALUES ('youssef', 'youssef123', '1994-12-18', 'Luxor, Egypt', 'MALE')");
        stmt.execute("INSERT INTO guests VALUES ('lena', 'lena123', '2003-02-14', 'Aswan, Egypt', 'FEMALE')");
        stmt.execute("INSERT INTO guests VALUES ('hassan', 'hassan123', '1996-08-05', 'Port Said, Egypt', 'MALE')");
        System.out.println("Created 10 guest accounts.");
        
        System.out.println("\n=== Phase 4: Creating 3 Admin Accounts ===");
        stmt.execute("INSERT INTO staff VALUES ('admin', 'admin123', 'ADMIN', '1985-05-15', 8)");
        stmt.execute("INSERT INTO staff VALUES ('khaled', 'khaled123', 'ADMIN', '1990-03-20', 8)");
        stmt.execute("INSERT INTO staff VALUES ('mostafa', 'mostafa123', 'ADMIN', '1988-09-10', 8)");
        System.out.println("Created 3 admin accounts.");
        
        System.out.println("\n=== Phase 5: Creating 2 Receptionist Accounts ===");
        stmt.execute("INSERT INTO staff VALUES ('reception1', '7amadareception', 'RECEPTIONIST', '1995-01-01', 6)");
        stmt.execute("INSERT INTO staff VALUES ('reception2', '7amadareception', 'RECEPTIONIST', '1996-06-15', 6)");
        System.out.println("Created 2 receptionist accounts.");
        
        System.out.println("\n=== Phase 6: Creating 2 Housekeeper Accounts ===");
        stmt.execute("INSERT INTO staff VALUES ('mahmoud', 'mahmoud123', 'HOUSEKEEPER', '1992-04-10', 8)");
        stmt.execute("INSERT INTO staff VALUES ('nadia', 'nadia123', 'HOUSEKEEPER', '1994-08-22', 8)");
        System.out.println("Created 2 housekeeper accounts.");
        
        System.out.println("\n=== Phase 7: Verification ===");
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM guests");
        rs.next();
        System.out.println("Guests count: " + rs.getInt(1));
        
        rs = stmt.executeQuery("SELECT COUNT(*) FROM staff WHERE role = 'ADMIN'");
        rs.next();
        System.out.println("Admins count: " + rs.getInt(1));
        
        rs = stmt.executeQuery("SELECT COUNT(*) FROM staff WHERE role = 'RECEPTIONIST'");
        rs.next();
        System.out.println("Receptionists count: " + rs.getInt(1));
        
        rs = stmt.executeQuery("SELECT COUNT(*) FROM staff WHERE role = 'HOUSEKEEPER'");
        rs.next();
        System.out.println("Housekeepers count: " + rs.getInt(1));
        
        rs = stmt.executeQuery("SELECT COUNT(*) FROM staff");
        rs.next();
        System.out.println("Total staff: " + rs.getInt(1));
        
        System.out.println("\n=== All Users ===");
        System.out.println("\n--- Guests ---");
        rs = stmt.executeQuery("SELECT username, password FROM guests");
        while (rs.next()) {
            System.out.println(rs.getString(1) + " | " + rs.getString(2));
        }
        
        System.out.println("\n--- Staff ---");
        rs = stmt.executeQuery("SELECT username, password, role FROM staff");
        while (rs.next()) {
            System.out.println(rs.getString(1) + " | " + rs.getString(2) + " | " + rs.getString(3));
        }
        
        c.close();
        System.out.println("\n=== DONE ===");
    }
}