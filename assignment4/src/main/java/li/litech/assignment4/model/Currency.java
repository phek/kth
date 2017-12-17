package li.litech.assignment4.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Currency implements Serializable {

    @Id
    private String name;
    private double rate;

    /**
     * Empty constructor, required for JPA.
     */
    public Currency() {
    }

    /**
     * Constructor
     *
     * @param name The name of the currency.
     * @param rate The rate between the currency and SEK.
     */
    public Currency(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }

    /**
     * @return The name of the currency.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return The rate between the currency and SEK.
     */
    public double getRate() {
        return this.rate;
    }
}
