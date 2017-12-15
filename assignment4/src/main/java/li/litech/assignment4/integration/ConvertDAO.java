package li.litech.assignment4.integration;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import li.litech.assignment4.model.Currency;

@TransactionAttribute(TransactionAttributeType.MANDATORY)
@Stateless
public class ConvertDAO {

    @PersistenceContext(unitName = "convertPU")
    private EntityManager em;

    public Currency getCurrencyByName(String name) {
        Currency currency = em.find(Currency.class, name);
        if (currency == null) {
            throw new EntityNotFoundException("No currency with the name " + name + " found.");
        }
        return currency;
    }

    public List<Currency> getAllCurrencies() {
        Query query = em.createQuery("SELECT e FROM Currency e");
        return query.getResultList();
    }
    
}
