import java.time.LocalDate;

public class Housekeeper extends Staff {

    Housekeeper(String username, String password, int year, int month, int day, int workinghours) {
        super(username, password, Role.HOUSEKEEPER, LocalDate.of(year, month, day), workinghours);
    }

    public void cleanRoom(Room room) {
        System.out.println("Housekeeper cleaning room " + room.getRoomNumber());
    }

    public void viewAssignedRooms() {
        System.out.println("Assigned rooms for " + getUsername());
    }
}