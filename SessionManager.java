public class SessionManager {
    private static Guest currentGuest = null;
    private static Staff currentStaff = null;

    public static Guest getCurrentGuest() { return currentGuest; }
    public static Staff getCurrentStaff() { return currentStaff; }

    public static void setCurrentGuest(Guest guest) {
        currentGuest = guest;
        currentStaff = null;
    }

    public static void setCurrentStaff(Staff staff) {
        currentStaff = staff;
        currentGuest = null;
    }

    public static void logout() {
        currentGuest = null;
        currentStaff = null;
    }

    public static boolean isLoggedIn() {
        return currentGuest != null || currentStaff != null;
    }
}
