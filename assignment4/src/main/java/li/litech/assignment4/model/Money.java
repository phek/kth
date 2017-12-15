package li.litech.assignment4.model;

public class Money {

    double amount;
    Currency currency;

    public Money(Double amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Currency getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }
    
}
