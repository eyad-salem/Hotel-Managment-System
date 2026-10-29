import java.time.LocalDate;
import java.util.Scanner;

public abstract class Staff{
    private String username;
    private String password;
    private LocalDate dob;
    private int workinghours;
    private Role role;

    Staff(String username , String password , Role role , LocalDate dob,int workinghours){
        this.username = username;
        this.password = password;
        this.role = role;
        this.dob = dob;
        this.workinghours = workinghours;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LocalDate getDob() {
        return dob;
    }
    public Role getRole(){
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDob(int year , int month , int day) {
        this.dob = LocalDate.of(year, month, day);
    }

    public int getWorkinghours() {
        return workinghours;
    }

    public void setWorkinghours(int workinghours) {
        this.workinghours = workinghours;
    }

    public void viewAllGuests(){
    }

    public void viewAllRooms(){
    }

    public void viewAllReservations(){
    }

    public static Staff loginStaff(){
        final int max_attempts=5;
        int attempts=0;
        Scanner s = new Scanner(System.in);

        while(attempts<max_attempts){
            System.out.println("Enter Username");
            String username = s.nextLine();

            System.out.println("Enter Password");
            String password = s.nextLine();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Please Type A Username and A Password ");
                attempts++;
                continue;  
            }
       
            for(Staff staff : HotelDatabase.allStaffs ){
                if(staff.getUsername().equals(username)&& staff.getPassword().equals(password)){
                    System.out.println("Login Succesfully");
                    return staff;
                }
            }
            attempts++;
            int remain= max_attempts-attempts;
            if (remain>0){
                System.out.println("Detailes Invalid " + remain + " Trys Left");
            }
        }
        System.out.println("Max Amount Of Attempts Reached !!");
        return null;
    }

    public static Staff loginStaff(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        if (password == null || password.trim().isEmpty()) {
            return null;
        }
        for (Staff staff : HotelDatabase.allStaffs) {
            if (staff.getUsername().equals(username) && staff.getPassword().equals(password)) {
                return staff;
            }
        }
        return null;
    }
}