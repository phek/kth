package li.litech.assignment4.controller;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import li.litech.assignment4.integration.ConvertDAO;
import li.litech.assignment4.model.Converter;
import li.litech.assignment4.model.Currency;
import li.litech.assignment4.model.Money;

/**
 *
 * @author Philip Ekblom
 */
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Stateless
public class Controller {

    @EJB ConvertDAO db;
    private Converter converter = new Converter();
    
    public Money convertMoney(double amount, String currency, String toCurrency) {
        Currency cur = db.getCurrencyByName(currency);
        Currency toCur = db.getCurrencyByName(toCurrency);
        return converter.convert(amount, cur, toCur);
    }

    public String[] getCurrencies() {
        List<Currency> curList = db.getAllCurrencies();
        String[] strCurList = new String[curList.size()];
        for (int i = 0; i < curList.size(); i++) {
            strCurList[i] = curList.get(i).getName();
        }
        return strCurList;
    }
}
