package li.litech.assignment4.model;

public class Money {

    double amount;
    Currency currency;

    /**
     * Constructor.
     *
     * @param amount The amount.
     * @param currency The currency.
     */
    public Money(Double amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * @return The currency.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @return The amount.
     */
    public double getAmount() {
        return amount;
    }

}
