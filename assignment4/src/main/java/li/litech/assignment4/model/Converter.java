package li.litech.assignment4.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Converter {

    public Money convert(double amount, Currency currency, Currency toCurrency) {
        double swedishMoney = curToSek(amount, currency);
        return sekToCurrency(swedishMoney, toCurrency);
    }

    private Money sekToCurrency(double amount, Currency currency) {
        double roundedResult = round(amount * currency.getRate(), 2);
        return new Money(roundedResult, currency);
    }

    private double curToSek(double amount, Currency currency) {
        return amount / currency.getRate();
    }

    private double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
