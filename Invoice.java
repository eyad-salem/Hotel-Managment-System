import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Invoice {
    private Reservation reservation;
 private Payment payment;
    private double totalamount;
    private LocalDate paymentdate;
    private boolean ispaid ;


    public Invoice(Reservation reservation,Payment payment)throws InvalidPaymentException{   
    if(reservation==null){
        throw new IllegalArgumentException("Reservation cannot be empty");
    }
    if(payment==null){
        throw new IllegalArgumentException("Payment cannot be empty");
    }


      this.reservation = reservation;
      this.totalamount = calculateTotal();
      this.payment = payment; 
      this.ispaid=false;
      this.paymentdate = LocalDate.now() ;
    }

    public double getTotalamount() {
        return totalamount; 
    }
    public void setTotalamount(int totalamount) {
        this.totalamount = totalamount;
    }
    public LocalDate getPaymentdate() {
        return paymentdate;
    }
    
    public Reservation getReservation() {
        return reservation;
    }
    
    public Payment getPayment() {
        return payment;
    }

    private double calculateTotal(){
      Long nights = ChronoUnit.DAYS.between( reservation.getCheckInDate(),reservation.getCheckOutDate());
       return nights*reservation.getRoom().getPricePerNight();
        }
        

        public String toString(){
            return "Invoice --> " + " Reservation ID "+reservation.getId() + "\n Total: egp " + totalamount + " \n Payment "+payment+ "\n Date " + paymentdate;
        }

        

    }


