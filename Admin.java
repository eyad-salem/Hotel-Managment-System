import java.time.LocalDate;
import java.util.Scanner;

public class Admin extends Staff {
Admin(String username , String password , int year , int month , int day ,int workinghours){

        super(username , password , Role.ADMIN,LocalDate.of(year, month, day), workinghours);

    }

public void addRoom(){

  RoomType rt = null;
    Scanner s = new Scanner(System.in);
    System.out.println("Enter Room Number");
    int num = s.nextInt();
    s.nextLine();

      Room exists = HotelDatabase.findRoomByNumber(num);
      if(exists!=null){
        System.out.println("Room Number "+ num +" already exists!");
      }
    System.out.println("Enter a Room Type Single , Double , Deluxe or Family");
    while(true){
    String type= s.nextLine().trim();
    if(type.equalsIgnoreCase("Single")|| type.equalsIgnoreCase("Double")|| type.equalsIgnoreCase("Deluxe")||type.equalsIgnoreCase("Family"))
        {
             rt = HotelDatabase.findRoomTypeByName(type);
          break;
        }
    else
        {
       System.out.println("Invalid Room Type");
    }
}
    
 if (rt ==null){
    System.out.println("Room not Found!");
    return;
 }
    Room newroom = new Room(num,rt);
    HotelDatabase.allRooms.add(newroom);

}
 
public void removeRoom(){
    Scanner s = new Scanner(System.in);
    System.out.println("Enter the ROOM number you want to remove");
    int num = s.nextInt();
    Room r = HotelDatabase.findRoomByNumber(num);
    if(r == null){ // Fixed this from != null
        System.out.println("Room Not found");
        return;
    }

    for(Reservation res : HotelDatabase.allReservations){
        if(res.getRoom().getRoomNumber()==num && (res.getStatus()==ReservationStatus.CONFIRMED||res.getStatus()==ReservationStatus.PENDING  )){
            System.out.println("Cannot Remove Room. It has an active Reservation!");
            return;
        }
    }
    HotelDatabase.allRooms.remove(r);
    System.out.println("Room Numb : " + num + " was Removed");

}    


  
public void readRooms(){
    System.out.println(HotelDatabase.allRooms);

    }

public void updateRoom(){
    Scanner s = new Scanner(System.in);
    System.out.println("Enter Room Number you want to update");
    int rnum = s.nextInt();
    System.out.println("Enter a New Room Type for this Room");
    String ntype = s.nextLine();
    Room r = HotelDatabase.findRoomByNumber(rnum);
    if(r==null){
        System.out.println("Room not found");
        return; // Added this return so it doesn't crash!
    }
    RoomType rt = HotelDatabase.findRoomTypeByName(ntype);
    if(rt!=null){
        r.setRoomType(rt);
    }
}

public void addRoomType(){
    Scanner s = new Scanner(System.in);
    System.out.println("Enter a new Room Type");
    String name = s.nextLine().trim();

    RoomType exists= HotelDatabase.findRoomTypeByName(name);
    if(exists!=null){
        System.out.println("This RoomType already exists");
return;
    }
    if(name.isEmpty()){
        System.out.println("Room Type name cannot be empty");
    }
System.out.println(" Enter new RoomType name ");
String newname = s.nextLine().trim();
System.out.println("Enter Price per Night");
double price = s.nextDouble();
if(price<=0){
    System.out.println("Price must be bigger than 0!");
    return;
}
System.out.println("Enter Capacity in the Room");
int capacity = s.nextInt();

if(capacity<=0){
    System.out.println("capactiy must be greater than 0!");
    return;
}


 RoomType newtype = new RoomType(newname,price,capacity);
 HotelDatabase.allRoomTypes.add(newtype);
    
   }

public void removeRoomType(){
    Scanner s = new Scanner(System.in);
    System.out.println("Enter the Name of the RoomType to remove");
    String rname= s.nextLine();
    RoomType r = HotelDatabase.findRoomTypeByName(rname);
    if(r!=null){
        HotelDatabase.allRoomTypes.remove(r);
        System.out.println("This Room type has been removed "+ rname);
    }
    else if(r==null){
        System.out.println("Room Type not found!");
        return;
    }

    boolean inuse = false;
    for(Room room : HotelDatabase.allRooms){
        if(room.getRoomType().getName().equalsIgnoreCase(rname)){
            inuse = true;
            break;
        }
    }
    
    if(inuse){
        System.out.println("Cannot delete Room Type - it's being used by existing rooms!");
        return;
    }

   }
public void readRoomType(){
System.out.println(HotelDatabase.allRoomTypes);
   }
public void updateRoomType(){
    Scanner s = new Scanner(System.in);
    System.out.println("Enter Old RoomType name ");
    String oldname = s.nextLine();
    RoomType r = HotelDatabase.findRoomTypeByName(oldname);
    if(r == null){
        System.out.println("RoomType Not Found");
        return;
    }

    System.out.println("Enter new RoomType Name ");
    String newname = s.nextLine();
    if(newname.isEmpty()){
        System.out.println("RoomType Name Can't Be Empty");
        return;
    }
    System.out.println("Enter a new Price");
    double nprice = s.nextDouble();
    if(nprice<=0){
        System.out.println("Price can't be < 0 ");
        return;
    }
    System.out.println("Enter the capacity for this new Type");
    int ncapacity = s.nextInt();
    if(ncapacity <=0 ){
        System.out.println("Capacity Can't Be <= 0!");
        return;
    }
    r.setName(newname);
    r.setPricePerNight(nprice);
    r.setCapacity(ncapacity);
System.out.println("Room Updated Succesfully!");
}

public void addAmenity(){
    Scanner s = new Scanner(System.in);
    System.out.println("Enter amenity name");
    String name = s.nextLine().trim();
    Amenity exists =  HotelDatabase.findAmenityByName(name);
    if(exists!=null){
        System.out.println("it already exists");
        return;
    }
    Amenity newamenity = new Amenity(name);
    HotelDatabase.allAmenities.add(newamenity);
    System.out.println("Amenity"+ name + " added ");

   }

public void removeAmenity() {
    Scanner s = new Scanner(System.in);
    System.out.println("Enter the Amenity You want to Remove");
   String name = s.nextLine();
    Amenity a = HotelDatabase.findAmenityByName(name);
    if (a == null) {
        System.out.println("Amenity Not found!");
        return;
    }
    HotelDatabase.allAmenities.remove(a);
    System.out.println("Amenity Removed");
}
public void updateAmenity() {
    Scanner s = new Scanner(System.in);
    System.out.println("Enter Old Amenity");
    String oldName = s.nextLine();
    Amenity a = HotelDatabase.findAmenityByName(oldName);
    if (a == null) {
        System.out.println("This amenity is Not found!");
        return;
    }
    System.out.println("Enter new Amenity name");
    String newName = s.nextLine();
    try{
    a.setName(newName);
    System.out.println("Amenity Updated");
    }catch(InvalidInputException e ){
        System.out.println("Error Updating amenity : " + e.getMessage());
    }
}

public void readAmenity(){
    System.out.println(HotelDatabase.allAmenities);
   
   }

    public static void displayAdminMenu(Admin admin){

    
    Scanner s = new Scanner(System.in);
    boolean flag = true;
    while (flag) {
    System.out.println("---Admin Menu---");
    System.out.println("1. Add a Room");
    System.out.println("2. Remove a  Rooms");
    System.out.println("3. View the Rooms");
    System.out.println("4. Update a Room");
    System.out.println("5. Add an Amenity");
    System.out.println("6. Remove an Amenity");
    System.out.println("7. View the Amenities");
    System.out.println("8. Update an Amenity");
    System.out.println("9. Add a RoomType");
    System.out.println("10. Remove a RoomType");
    System.out.println("11. View a RoomType");
    System.out.println("12. Update a RoomType");
    System.out.println("13. View all Guests");
    System.out.println("14. View all Reservations");
    System.out.println("15. Exit Admin Menu");
    System.out.println("Enter Option you want");
int choice = s.nextInt();



switch(choice){
    case 1 : admin.addRoom();
    break;
    case 2 : admin.removeRoom();
    break;
    case 3: admin.readRooms();
    break;
    case 4: admin.updateRoom();
    break;
    case 5:admin.addAmenity();
    break;
    case 6: admin.removeAmenity();
    break;
    case 7: admin.readAmenity();
    break;
    case 8: admin.updateAmenity();
    break;
    case 9: admin.addRoomType();
    break;
    case 10: admin.removeRoomType();
    break;
    case 11: admin.readRoomType();
    break;
    case 12: admin.updateRoomType();
    break;
    case 13: admin.viewAllGuests();
    break;
    case 14:admin.viewAllReservations();
    break;
    case 15: {flag = false;}
    break;
    default:{
        System.out.println("Invalid Choice");
        break;
    }
}
    }
    }

public void viewAllGuests(){
    System.out.println("View whole Guest List");

    System.out.println(HotelDatabase.allGuests);
}

public void viewAllReservations(){
    System.out.println("View Whole Reservation List");
    System.out.println(HotelDatabase.allReservations);

}

}








