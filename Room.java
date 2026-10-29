import java.util.ArrayList;

public class Room {
    private int roomNumber;
    private RoomType roomType;
    private ArrayList<Amenity> amenities;
    private boolean isAvailable;
    private Reservation currentReservation;

    public Room(int roomNumber, RoomType roomtype) {
        if (roomNumber <= 0) {
            throw new IllegalArgumentException("Room number must be positive");
        }
        if (roomtype == null) {
            throw new IllegalArgumentException("Room must have a RoomType");
        }
        this.roomNumber = roomNumber;
        this.roomType = roomtype;
        this.amenities = new ArrayList<>();
        this.isAvailable=true;
    }

    public void addAmenity(Amenity amenity) {
        amenities.add(amenity);
    }

     public void removeAmenity(Amenity amenity) {
        amenities.remove(amenity);
    }

    public int getRoomNumber() { return roomNumber; }
    public RoomType getRoomType() { return roomType;}
    public ArrayList<Amenity> getAmenities() { return amenities;}
    public boolean isAvailable() { return isAvailable;}
    public Reservation getReservation(){ return currentReservation;}
    public double  getPricePerNight(){ return roomType.getPricePerNight();}

    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber;}
    public void setRoomType(RoomType roomType) { this.roomType = roomType;}
    public void setAmenities(ArrayList<Amenity> amenities) { this.amenities = amenities;}
    public void setAvailable(boolean isAvailable) { this.isAvailable = isAvailable;}
    public void setReservation(Reservation reservation) { this.currentReservation = reservation;}



    
    @Override
    public String toString() {
        return "Room " + roomNumber + 
        ": " + roomType + 
        ", Amenities: " + amenities;
    }

   
}
