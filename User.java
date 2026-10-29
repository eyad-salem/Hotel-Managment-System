import java.util.InputMismatchException;
import java.util.Scanner;

public class User {
   
    public static void main(String [] args ){
        HotelDatabase.initialize();
        Scanner s = new Scanner(System.in);
        int choice = 0;

        System.out.println("Welcome to 7amada Hotel ");
        System.out.println("-------------------------\n");

        while(choice != 3){
        System.out.println("Choose Which user type you are");
        System.out.println("-------------");
        System.out.println("1- Staff");
        System.out.println("2- Guest");
        System.out.println("3- Exit Program");
    try {
            choice = s.nextInt();
            s.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input! Please enter a number.");
            s.nextLine(); // clear the bad input
            continue; // restart the menu loop
        }
        switch(choice){
            case 1:
             Staff loggedin =  Staff.loginStaff();
            if(loggedin==null){
                System.out.println("Login Failed");
                break;
            }
            if(loggedin instanceof Admin){
                Admin.displayAdminMenu((Admin) loggedin);
                break;
            }
            else if(loggedin instanceof Receptionist) {
                Receptionist.displayReceptionistmenu((Receptionist) loggedin);
                break;
            }
            case 2: Guest G1 = new Guest();
            try{
                    G1.GuestMenu();     
                    break;

            }catch(InvalidInputException e ){
                System.out.println("Error " + e.getMessage());
            }                  
            break;
           case 3 : System.out.println("Thank you for visiting this Hotel!");
           break;
       default: System.out.println("Invalid Choice! Please enter option 1, 2 or 3");
}

}
    }
}
