import java.sql.*;
public class QueryUsers {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        Connection c = DriverManager.getConnection("jdbc:sqlite:hotel_data/hotel.db");
        Statement s = c.createStatement();
        System.out.println("=============================================");
        System.out.println("|          GUEST ACCOUNTS                   |");
        System.out.println("=============================================");
        System.out.printf("| %-15s | %-15s |%n", "USERNAME", "PASSWORD");
        System.out.println("----------------------------------------------");
        ResultSet rs = s.executeQuery("SELECT username, password FROM guests");
        while(rs.next()) {
            System.out.printf("| %-15s | %-15s |%n", rs.getString(1), rs.getString(2));
        }
        System.out.println("=============================================");
        System.out.println("|          STAFF ACCOUNTS                   |");
        System.out.println("=============================================");
        System.out.printf("| %-15s | %-15s | %-12s |%n", "USERNAME", "PASSWORD", "ROLE");
        System.out.println("------------------------------------------------------");
        rs = s.executeQuery("SELECT username, password, role FROM staff");
        while(rs.next()) {
            System.out.printf("| %-15s | %-15s | %-12s |%n", rs.getString(1), rs.getString(2), rs.getString(3));
        }
        c.close();
    }
}