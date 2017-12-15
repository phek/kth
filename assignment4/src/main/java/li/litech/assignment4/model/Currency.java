package li.litech.assignment4.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author Philip Ekblom
 */

@Entity
public class Currency implements Serializable {
    
    @Id
    private String name;
    private double rate;

    public Currency() {
    }

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
     * @return The rate between 1 SEK and the currency.
     */
    public double getRate() {
        return this.rate;
    }
}
