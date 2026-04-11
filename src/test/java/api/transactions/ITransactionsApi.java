package api.transactions;

import model.transaction.TransactionPublic;

import java.util.List;

public interface ITransactionsApi {
    /**
     * Получить историю операций
     */
    List<TransactionPublic> getTransactions(String token);

    /**
     * Скачать чек по операции
     */
    String getReceipt(String token, int transactionId);
}