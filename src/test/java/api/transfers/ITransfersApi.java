package api.transfers;

import model.transaction.TransactionPublic;
import model.transfer.*;

public interface ITransfersApi {
    /**
     * Перевод между своими счетами
     */
    TransactionPublic createTransfer(String token, TransferCreateRequest request);

    /**
     * Перевод по номеру счета
     */
    TransactionPublic createTransferByAccount(String token, TransferByAccountRequest request);

    /**
     * Проверить существование счета по номеру
     */
    TransferByAccountCheckResponse checkAccountByNumber(String token, String accountNumber);

    /**
     * Перевод на счет в другом банке (с комиссией 5%)
     */
    TransactionPublic createExternalTransfer(String token, TransferByAccountRequest request);

    /**
     * Проверить телефон и получить доступные банки
     */
    TransferByPhoneCheckResponse checkPhone(String token, String phone);

    /**
     * Перевод по номеру телефона
     */
    TransactionPublic createTransferByPhone(String token, TransferByPhoneRequest request);

    /**
     * Обмен валюты
     */
    TransactionPublic exchangeCurrency(String token, ExchangeRequest request);

    /**
     * Получить остаток суточного лимита
     */
    DailyUsageResponse getDailyUsage(String token);

    /**
     * Получить курсы валют
     */
    ExchangeRatesResponse getExchangeRates(String token);
}