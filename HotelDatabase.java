
import java.time.LocalDate;
import java.util.ArrayList;


public class HotelDatabase {

public  static ArrayList <Staff> allStaffs = new ArrayList<>();
public static  ArrayList<Room> allRooms = new ArrayList<>();
public static ArrayList<RoomType> allRoomTypes =  new ArrayList<>();
public static ArrayList<Amenity> allAmenities = new ArrayList<>();
public static ArrayList<Guest> allGuests = new ArrayList<>();
public static ArrayList<Reservation> allReservations = new ArrayList<>();
public static ArrayList<Invoice> allInvoices = new ArrayList<>();

private static int resid = 1000;
public static int getNextReservationId() {
    return resid++;  
}

    public static RoomType findRoomTypeByName(String name) {
   
    for (RoomType roomType : allRoomTypes) {
        
        if (roomType.getName().equalsIgnoreCase(name)) {
            return roomType;  
        }
    }
    return null;  
}

public static Amenity findAmenityByName(String name) {
    for (Amenity amenity : allAmenities) {
        if (amenity.getName().equalsIgnoreCase(name)) {
            return amenity;
        }
    }
    return null;
}

public static Room findRoomByNumber(int roomNumber) {
    for (Room room : allRooms) {
        if (room.getRoomNumber() == roomNumber) {
            return room;
        }
    }
    return null;
}

    public static void initialize(){


        allStaffs.add(new Admin("admin","admin123",1990,5,15,8));
        allStaffs.add(new Receptionist("recep","recep123",1992,3,20,6));

        allStaffs.add(new Admin("Admin1","ad123",2000,10,2,10));
        allStaffs.add(new Admin("Admin2","ad1234",2005,1,24,8));
        allStaffs.add(new Admin("Admin3","ad12345",2003,3,12,12));
         allStaffs.add(new Admin("Admin4","ad123456",1998,7,7,6));
         allStaffs.add(new Admin("Admin5","ad1234567",1995,5,5,9));

        allStaffs.add(new Receptionist("Recep1","R123",2001,4,1,6));
        allStaffs.add(new Receptionist("Recep2","R1234",2002,7,4,8));
        allStaffs.add(new Receptionist("Recep3","R12345",2006,4,9,10));
        allStaffs.add(new Receptionist("Recep4","R123456",2007,8,4,11));
        allStaffs.add(new Receptionist("Recep5","R1234567",1994,7,3,12));

        try{

        allGuests.add(new Guest("ali","ali123",LocalDate.of(1995,8,15),"123 Main Street",Gender.MALE));
        
        allGuests.add(new Guest("Guest1","Ges123",LocalDate.of(2007,07,17),"kokoStreet",Gender.MALE));
        allGuests.add(new Guest("Guest2","Ges1234",LocalDate.of(2002,10,22),"pipiStreet",Gender.FEMALE));
        allGuests.add(new Guest("Guest3","Ges12345",LocalDate.of(2004,06,15),"luluStreet",Gender.MALE));
        allGuests.add(new Guest("Guest4","Ges123456",LocalDate.of(2000,01,01),"sosoStreet",Gender.FEMALE));
        allGuests.add(new Guest("Guest5","Ges1234567",LocalDate.of(2005,11,17),"pipiStreet",Gender.MALE));
        } catch(InvalidInputException e ){
            System.out.println("Can't Add Guest"+e.getMessage());
        }


        allRoomTypes.add(new RoomType("Single",100,2));
        allRoomTypes.add(new RoomType("Double",200,4));
        allRoomTypes.add(new RoomType("Deluxe",400,8));
        allRoomTypes.add(new RoomType("Family",800,10));


    allAmenities.add(new Amenity("TV"));
    allAmenities.add(new Amenity("Mini-bar"));
    allAmenities.add(new Amenity("Wifi"));
    allAmenities.add(new Amenity("Breakfast"));


 RoomType single= findRoomTypeByName("Single");
 RoomType doubler= findRoomTypeByName("Double");
 RoomType deluxe = findRoomTypeByName("deluxe");
 RoomType family = findRoomTypeByName("family");

 Amenity wifi = findAmenityByName("wifi");
 Amenity minibar = findAmenityByName("Mini-bar");
 Amenity tv = findAmenityByName("TV");
 Amenity breakfast = findAmenityByName("Breakfast");

if(single != null){
    Room room100 = new Room(100,single);
    if(wifi!=null){
        room100.addAmenity(wifi);
    }
    if(tv!=null){
        room100.addAmenity(tv);
    }
    allRooms.add(room100);

    Room room101 = new Room(101,single);
    if(minibar!=null){
        room101.addAmenity(minibar);
    }
    if(breakfast!=null){
        room101.addAmenity(breakfast);
    }
allRooms.add(room101);

}
if(doubler!=null){
    Room room200 = new Room(200,doubler);
    if(tv!=null){
        room200.addAmenity(tv);
    }
    if(breakfast!=null){
        room200.addAmenity(breakfast);
    }
allRooms.add(room200);

     Room room201 = new Room(201,doubler);
    if(minibar!=null){
        room201.addAmenity(minibar);
    }
    if(breakfast!=null){
        room201.addAmenity(breakfast);
    }
    allRooms.add(room201);

}
if(deluxe!=null){
    Room room300 = new Room(300,deluxe);
        if(wifi!=null){
            room300.addAmenity(wifi);
        }
        if(tv!=null){
            room300.addAmenity(tv);
        }
        allRooms.add(room300);
    
    Room room301 = new Room(301,deluxe);
    if(minibar!=null){
        room301.addAmenity(minibar);
    }
    if(breakfast!=null){
        room301.addAmenity(breakfast);
    }
    allRooms.add(room301);
}
if(family!= null){
    Room room400 = new Room (400,family);
      if(tv!=null){
        room400.addAmenity(tv);
    }
    if(breakfast!=null){
        room400.addAmenity(breakfast);
    }
    allRooms.add(room400);
    Room room401 = new Room(401,family);
    if(minibar!=null){
        room401.addAmenity(minibar);
    }
    if(breakfast!=null){
        room401.addAmenity(breakfast);
    }
    allRooms.add(room401);

}



}









    }





    







