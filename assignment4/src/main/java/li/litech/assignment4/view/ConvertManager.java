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

    public String getConvertedCurrency() {
        return convertedMoney == null ? null : convertedMoney.getCurrency().getName();
    }

    public double getConvertedAmount() {
        return convertedMoney == null ? 0 : convertedMoney.getAmount();
    }

    public String[] getCurrencies() {
        return controller.getCurrencies();
    }

    public void convert() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        System.out.println(request.getParameter("convert:amount"));
        double amount = Double.parseDouble(request.getParameter("convert:amount"));
        String currency = request.getParameter("convert:currency");
        String toCurrency = request.getParameter("convert:toCurrency");
        convertedMoney = controller.convertMoney(amount, currency, toCurrency);
    }
}
