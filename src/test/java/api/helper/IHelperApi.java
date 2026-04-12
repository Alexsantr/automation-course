package api.helper;

import model.account.AccountPublic;

import java.util.List;

public interface IHelperApi {
    /**
     * Получить список счетов через helper (для тестов)
     */
    List<AccountPublic> getHelperAccounts(String token);

    /**
     * Получить OTP код для подтверждения операций
     */
    String getOtpCode(String token);

    /**
     * Увеличить баланс счета (без OTP)
     */
    AccountPublic increaseBalance(String token, int accountId, String amount, String purpose);

    /**
     * Уменьшить баланс счета
     */
    AccountPublic decreaseBalance(String token, int accountId, String amount);

    /**
     * Обнулить баланс счета
     */
    AccountPublic zeroBalance(String token, int accountId);

    /**
     * Очистить кеш браузера
     */
    String clearBrowserCache(String token);
}