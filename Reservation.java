import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reservation  {
    
    private int id;
    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private ReservationStatus status;
    private double totalCost;
    private int stayDuration;

    public Reservation(Guest guest, Room room, LocalDate checkInDate, LocalDate checkOutDate)
            throws InvalidInputException {
        if (guest == null) throw new InvalidInputException("Guest cannot be null.");
        if (room == null) throw new InvalidInputException("Room cannot be null.");
        if (checkInDate == null || checkOutDate == null)
            throw new InvalidInputException("Dates cannot be null.");
        if (!checkOutDate.isAfter(checkInDate))
            throw new InvalidInputException("Check-out date must be after check-in date.");
        if (checkInDate.isBefore(LocalDate.now()))
            throw new InvalidInputException("Check-in date cannot be in the past.");

    
        this.guest = guest;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = ReservationStatus.PENDING;
        this.totalCost = calculateTotalCost();
        this.room.setReservation(this);
        this.id = HotelDatabase.getNextReservationId();
    }
public String toString() {
    return "Reservation ID: " + id +    "  Guest: " + guest.getUsername() +  "  Room: " + room.getRoomNumber() +  "  Total: egp" + totalCost;
}
    private double calculateTotalCost() {
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return nights * room.getPricePerNight();
    }

    public int getId() { return id; }
    public Guest getGuest() { return guest; }
    public Room getRoom() { return room; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public ReservationStatus getStatus() { return status; }
    public double getTotalCost() { return totalCost; }
    public int getStayDuration(){ return stayDuration; }
    public long getNights() { return ChronoUnit.DAYS.between(checkInDate, checkOutDate);}


    public void setStatus(ReservationStatus status) { this.status = status;}

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        room.setAvailable(true);
        room.setReservation(null);
    }

    public void complete() {
        this.status = ReservationStatus.COMPLETED;
        room.setAvailable(true);   
        room.setReservation(null);
         }     
         
    public void confirm(){
        this.status = ReservationStatus.CONFIRMED;
        room.setAvailable(false);
        }

    
    


   

    
   

}
