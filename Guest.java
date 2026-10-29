import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;


public class Guest  {
  
    private String username;
    private String password;
    private LocalDate dateOfBirth;
    private String address;
    private Gender gender;
    private ArrayList<Reservation> reservations = new ArrayList<>();
    public static ArrayList<Guest> allGuests = new ArrayList<>();

public String toString() {
    return "Guest Name: " + this.getUsername() + "  DOB : " + this.getDateOfBirth()  + "  Address : " + this.getAddress()  + "  Gender : " + this.getGender();
}
    public Guest(){}

    public Guest(String username, String password, LocalDate dateOfBirth, String address, Gender gender) throws InvalidInputException {
        setUsername(username);
        setPassword(password);
        if (dateOfBirth == null || dateOfBirth.isAfter(LocalDate.now()))
         throw new InvalidInputException("Invalid date of birth.");
        this.dateOfBirth = dateOfBirth;
        setAddress(address);
        if (gender == null) throw new InvalidInputException("Gender cannot be null.");
        this.gender = gender;
        
        this.reservations = new ArrayList<>();
        
    }


    
    public String getUsername() {
         return username;
        }
    public String getPassword() { return password; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
  
    public String getAddress() { return address; }
    public Gender getGender() { return gender; }
    public ArrayList<Reservation> getReservations() { return reservations; }

    public void setUsername(String username) throws InvalidInputException {
        if (username == null || username.trim().isEmpty())
            throw new InvalidInputException("Username cannot be empty.");
        this.username = username.trim();
    }

    public void setPassword(String password) throws InvalidInputException {
        if (password == null || password.length() < 6)
            throw new InvalidInputException("Password must be at least 6 characters.");
        this.password = password;
    }

    

    public void setAddress(String address) throws InvalidInputException {
        if (address == null || address.trim().isEmpty())
            throw new InvalidInputException("Address cannot be empty.");
        this.address = address.trim();
    }

    public void setGender(Gender gender) throws InvalidInputException {
        if (gender == null) throw new InvalidInputException("Gender cannot be null.");
        this.gender = gender;
    }

    
    public void addReservation(Reservation reservation) {
        if (reservation != null) reservations.add(reservation);
    }


    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }

    public void viewAvailableRoom(){
        for (Room room : HotelDatabase.allRooms) {
            if (room.isAvailable()) { 
                System.out.println(room);
            }
        }
    }

public void Register() throws InvalidInputException{{

            Scanner  s = new Scanner(System.in);
        System.out.println("Enter Your username");
           String username = s.nextLine();
            if (username.isEmpty())
            throw new InvalidInputException("Username cannot be empty.");
        for(Guest existing : allGuests){
            if(existing.getUsername().equals(username)){
                throw new InvalidInputException("UserName already taken");
            }

        }
        for (Guest existing : HotelDatabase.allGuests) {
        if (existing.getUsername().equalsIgnoreCase(username)) {
            throw new InvalidInputException("UserName already taken");
        }
    }
               
        System.out.println("Enter your password");
         String password = s.nextLine().trim();
            if ( password.length() < 4){
            throw new InvalidInputException("Password must be at least 4 characters.");
           }
         
           System.out.println("Enter DateOfBirth YYYY-MM-DD");
           LocalDate dob = LocalDate.parse(s.nextLine());

           System.out.println("Enter address");
           String add = s.nextLine();

           System.out.println("Enter Gender");
          Gender gender = Gender.valueOf(s.nextLine().toUpperCase());
          

            Guest newGuest = new Guest(username , password,dob,add,gender);
            HotelDatabase.allGuests.add(newGuest);
            System.out.println("Regestiration Succesful!");
}
}


    public Guest login() throws InvalidInputException {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter Your username");
        String username = s.nextLine();
            if ( username.trim().isEmpty()){
            throw new InvalidInputException("Username cannot be empty.");
            }
        System.out.println("Enter your password");
         String password = s.nextLine().trim();
            if ( password.length() < 4){
            throw new InvalidInputException("Password must be at least 4 characters.");
           }

           for (Guest g : HotelDatabase.allGuests) {
        if (g.getUsername().equals(username) && g.getPassword().equals(password)) {
            System.out.println("Login Successful! Welcome back : " + g.getUsername());
            return g; 
        }
    }
        throw new InvalidInputException("Invalid Username or Password");
    }

    public static Guest login(String username, String password) throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty.");
        }
        if (password == null || password.length() < 4) {
            throw new InvalidInputException("Password must be at least 4 characters.");
        }
        for (Guest g : HotelDatabase.allGuests) {
            if (g.getUsername().equals(username) && g.getPassword().equals(password)) {
                return g;
            }
        }
        throw new InvalidInputException("Invalid Username or Password");
    }

    public static Guest findGuestByUsername(String username) {
        if (username == null) return null;
        for (Guest g : HotelDatabase.allGuests) {
            if (g.getUsername().equals(username)) {
                return g;
            }
        }
        return null;
    }

  public void makeReservation(){
       Scanner s = new Scanner(System.in);

    System.out.println("-- Available Rooms --");
    boolean roomsAvailable = false;
    
for (Room room : HotelDatabase.allRooms) {
        if (room.isAvailable()) {
            roomsAvailable = true;
            System.out.println("Room #" + room.getRoomNumber() +  " | Type: " + room.getRoomType().getName() + " | Price: $" + room.getPricePerNight() + "per night" + " | Capacity: " + room.getRoomType().getCapacity() +" | Amenities: " + room.getAmenities());
        }
        
    }
if(!roomsAvailable){
    System.out.println("There are no rooms available");
}

System.out.println("Enter room number you want");
int roomnum = s.nextInt();
s.nextLine();
Room chosenroom = HotelDatabase.findRoomByNumber(roomnum);
if(chosenroom == null){
    System.out.println("Room wasn't found");
    return;
}
if(!chosenroom.isAvailable()){
    System.out.println("Room is not Available");

}

LocalDate checkin = null;
        while (checkin == null) {
            try {
                System.out.println("Enter Check-in date (YYYY-MM-DD):");
                String checkininput = s.nextLine().trim();
                checkin = LocalDate.parse(checkininput);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format! Please use YYYY-MM-DD (e.g., 2026-05-24).");
            }
        }

        LocalDate checkout = null;
        while (checkout == null) {
            try {
                System.out.println("Enter Check-out date (YYYY-MM-DD):");
                String checkoutinput = s.nextLine().trim();
                checkout = LocalDate.parse(checkoutinput);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format! Please use YYYY-MM-DD.");
            }
        }
try{
    Reservation newreservation = new Reservation(this,chosenroom,checkin,checkout);

    HotelDatabase.allReservations.add(newreservation);
    this.getReservations().add(newreservation);

     System.out.println("---Reservation details---");
        System.out.println("Reservation ID: " + newreservation.getId());
        System.out.println("Room: " + chosenroom.getRoomNumber());
        System.out.println("Check-in: " + checkin);
        System.out.println("Check-out: " + checkout);
        System.out.println("Nights: " + newreservation.getNights());
        System.out.println("Total Cost: " + newreservation.getTotalCost()+ " egp");
        System.out.println("Status: " + newreservation.getStatus());
        
} catch (InvalidInputException e){
    System.out.println("Error " + e.getMessage());
}

}

  public void cancelreservation(){
    Scanner s = new Scanner(System.in);
     if (HotelDatabase.allReservations.isEmpty()) {
        System.out.println("No reservations in system.");
        return;
    }

    System.out.println("Enter Reservation ID");
    int rid = s.nextInt();
    for(Reservation r : HotelDatabase.allReservations){
        if(r.getId()==rid){
            if(r.getStatus()==ReservationStatus.CONFIRMED){
                System.out.println("Guest already checked-in can't cancel");
                return;
            }
             if (r.getStatus() == ReservationStatus.COMPLETED) {
                System.out.println("Reservation already done.");
                return;
            }
            r.cancel();
            System.out.println("Reservation ID " + rid + " is cancelled");
            return;
        }
    }
    System.out.println("Can't Find Reservation");
  }

 

  public void setDateOfBirth(LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public void viewReservations() {
        System.out.println("--- Your Reservations ---");
        boolean found = false;

        for (Reservation res : HotelDatabase.allReservations) {


            if (res.getGuest().getUsername().equals(this.username)) {
                System.out.println(res.toString()); 
                found = true;
            }
        }

        if (!found) {
            System.out.println("No reservations found for " + this.username);
        }
    }

  public void GuestChekout(){
    Scanner s = new Scanner(System.in);
    System.out.println("Enter Reservation ID to Check-out");
int resid = s.nextInt();
for(Reservation r : HotelDatabase.allReservations){
    if(resid == r.getId() && r.getStatus()==ReservationStatus.CONFIRMED){
        System.out.println("---Check-out Bill---");
        System.out.println("Guest --> "+ r.getGuest().getUsername());
        System.out.println("Room --> " + r.getRoom().getRoomNumber());
        System.out.println("Nights --> " + r.getNights());
        System.out.println("Total Amount --> egp "+ r.getTotalCost());

        Payment payment = null;

        while(payment == null){
        System.out.println("Select Payment Method");
        System.out.println("1. Cash");
        System.out.println("2. Credit Card");
        System.out.println("3. Online");
        System.out.println(" Enter a Choice ");
        int choice = s.nextInt();
        switch(choice){
        case 1: payment = Payment.CASH;
        System.out.println("Pay " + r.getTotalCost() + " in Cash");
        break;
        case 2: payment = Payment.CARD;
        System.out.println("Pay "+ r.getTotalCost() + " in Card ");
        break;
        case 3: payment= Payment.ONLINE;
        System.out.println("Pay "+r.getTotalCost()+ " using Online payment");
        break;
        default:
        System.out.println("Invalid Choice! Re-enter a valid choice");
        break;
        }
        }
      try{
        Invoice invoice = new Invoice(r, payment);
        HotelDatabase.allInvoices.add(invoice);
        r.complete();
        System.out.println("Check-out Completed!");
      }catch(InvalidPaymentException e){
        System.out.println("Payment Failed " + e.getMessage());
        return;
      }
        return;
            }
        }
        System.out.println("Reservation wasn't found ");

  }

  public void GuestMenu() throws InvalidInputException{
    Scanner s = new Scanner(System.in);
    int choise = 0;

    
    while( choise !=7){
        System.out.println("Choose desired option");
    System.out.println("------------");
    System.out.println("1- Register ");
    System.out.println("2- Login ");
    System.out.println("3- Make new Reservation ");
    System.out.println("4- View Reservations ");
    System.out.println("5- Cancel Reservation");
    System.out.println("6- Checkout and pay Invoice");
    System.out.println("7- Exit");
        choise = s.nextInt();
         s.nextLine();
        if(choise == 1){
            Register();
        }
        if(choise == 2){
            login();
        }
        if(choise == 3){
            makeReservation();
        }
        if(choise == 4){
            viewReservations();
        }
        if(choise == 5){
            cancelreservation();
        }
        if(choise == 6){
            GuestChekout();
        }

    }
  }


}
