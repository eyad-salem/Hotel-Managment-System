import java.time.LocalDate;
import java.util.Scanner;


public class Receptionist extends Staff {

    Receptionist(String username , String password , int year , int month , int day ,int workinghours){
        super(username , password , Role.RECEPTIONIST,LocalDate.of(year,month,day),workinghours);
    }

public void checkIn(){
Scanner s = new Scanner(System.in);

System.out.println("Showing Pending Check-in ");
boolean pend = false;
for (Reservation r : HotelDatabase.allReservations){
    if(r.getStatus() == ReservationStatus.PENDING){
        pend = true;
        System.out.println("Pending ID : " + r.getId());
    }
}
if(!pend){
    System.out.println("No Pending Check-ins");
    return;
}
System.out.println("Enter Reservation ID");
int resid = s.nextInt();
for(Reservation r : HotelDatabase.allReservations){
    if(resid == r.getId()&& r.getStatus()==ReservationStatus.PENDING){

        LocalDate tdy = LocalDate.now();
        LocalDate chin =r.getCheckInDate();
        if(tdy.isBefore(chin.minusDays(1))){
            System.out.println("Too early for  Check-in. Check in Date is " + r.getCheckInDate());
            return;
        }
        if(tdy.isAfter(chin.plusDays(1))){
            System.out.println("Check in day has passed. Please make a new Reservation.");
        }
        r.confirm();
        System.out.println("Check In Succesfull");
        System.out.println(" Guest Name : "+ r.getGuest().getUsername());
        System.out.println("Room : "+ r.getRoom().getRoomNumber());
        return;
    }
}
System.out.println("Reservation Not Found or Already Checked-in");
    }

 public void checkOut(){
Scanner s = new Scanner ( System.in);
System.out.println("Currently Checked-in Guests IDs");
for(Reservation r : HotelDatabase.allReservations){
    if(r.getStatus()==ReservationStatus.CONFIRMED){
        System.out.println("ID --> " + r.getId());
    }
}
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
            try {
                throw new InvalidPaymentException("Invalid Choice! Re-enter a valid choice");
            } catch (InvalidPaymentException e) {
                System.out.println(e.getMessage());
            }
            break;
        }
        }
      try{
    Invoice invoice = new Invoice(r, payment);
HotelDatabase.allInvoices.add(invoice);
r.complete();
System.out.println("Check-out Completed!");
      }catch(InvalidPaymentException e){
        System.out.println("Payment Failed "+ e.getMessage());
        return;
      }
      return;
    }
}
System.out.println("Reservation wasn't found ");
 }







public static void displayReceptionistmenu(Receptionist rec){
Scanner r = new Scanner(System.in);
boolean flag = true;


while(flag){
    System.out.println("---Receptionist Menu---");
    System.out.println("1. check in");
    System.out.println("2. check out");
    System.out.println("3. View all Rooms");
    System.out.println("4. View all Guests");
    System.out.println("5. View all Reservations");
    System.out.println("6. Exit Menu");
    System.out.println(" Enter Choice");
int choice = r.nextInt();

switch(choice){
    case 1 : rec.checkIn();
    break;
    case 2 : rec.checkOut();
    break;
    case 3: rec.viewAllRooms();
    break;
    case 4: rec.viewAllGuests();
    break;
    case 5: rec.viewAllReservations();
    break;
    case 6 : { flag = false;}
    break;
    default: {
        System.out.println("Invalid choice");
        break;
    }


}

}

}
public void viewAllGuests(){
    System.out.println("View whole Guest List");

    System.out.println(HotelDatabase.allGuests);
}

public void viewAllRooms(){
    System.out.println("View Whole Room List");
    System.out.println(HotelDatabase.allRooms);

}

public void viewAllReservations(){
    System.out.println("View Whole Reservation List");
    System.out.println(HotelDatabase.allReservations);

}
    

    
}
