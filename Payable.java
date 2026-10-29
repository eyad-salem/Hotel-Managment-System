public interface Payable {
    void pay(double amount, PaymentMethod method) throws InvalidPaymentException;
    double getTotalAmount();
}
