package li.litech.assignment4.view;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import li.litech.assignment4.controller.Controller;
import li.litech.assignment4.model.Money;

/**
 * Handles all interaction with the converter JSF page.
 */
@Named("convertManager")
@RequestScoped
public class ConvertManager implements Serializable {

    @EJB
    private Controller controller;
    private Money convertedMoney;
    private String[] currencies;

    /**
     * @return The currency of the converted money.
     */
    public String getConvertedCurrency() {
        return convertedMoney == null ? null : convertedMoney.getCurrency().getName();
    }

    /**
     * @return The amount of the converted money.
     */
    public double getConvertedAmount() {
        return convertedMoney == null ? 0 : convertedMoney.getAmount();
    }

    /**
     * @return A String list of all available currencies.
     */
    public String[] getCurrencies() {
        if (currencies == null) {
            currencies = controller.getCurrencies();
        }
        return currencies;
    }

    /**
     * Converts the selected currency to the new currency.
     */
    public void convert() {
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            double amount = Double.parseDouble(request.getParameter("convert:amount"));
            String currency = request.getParameter("convert:currency");
            String toCurrency = request.getParameter("convert:toCurrency");
            convertedMoney = controller.convertMoney(amount, currency, toCurrency);
        } catch (Exception ex) {
            /* 
             Do nothing, just tell the server an invalid request was performed.
             Should be improved by informing the user too.
             */
            System.out.println("Invalid request.");
        }
    }
}
