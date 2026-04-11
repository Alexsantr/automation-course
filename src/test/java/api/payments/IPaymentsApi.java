package api.payments;

import model.transaction.TransactionPublic;
import model.payment.MobilePaymentRequest;
import model.payment.VendorPaymentRequest;

import java.util.List;
import java.util.Map;

public interface IPaymentsApi {
    /**
     * Получить список мобильных операторов
     */
    List<Map<String, String>> getMobileOperators(String token);

    /**
     * Оплатить мобильную связь
     */
    TransactionPublic payMobile(String token, MobilePaymentRequest request);

    /**
     * Получить список поставщиков услуг
     */
    List<Map<String, String>> getVendorProviders(String token);

    /**
     * Оплатить поставщику
     */
    TransactionPublic payVendor(String token, VendorPaymentRequest request);
}