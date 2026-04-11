package api.accounts;

import model.account.AccountCreateRequest;
import model.account.AccountPublic;
import model.account.AccountTopupRequest;
import model.transaction.TransactionPublic;

import java.util.List;

public interface IAccountsApi {
    /**
     * Получить список счетов пользователя
     */
    List<AccountPublic> getAccounts(String token);

    /**
     * Открыть новый счет
     */
    AccountPublic createAccount(String token, AccountCreateRequest request);

    /**
     * Пополнить счет
     */
    TransactionPublic topUpAccount(String token, int accountId, AccountTopupRequest request);

    /**
     * Закрыть счет
     */
    String closeAccount(String token, int accountId);

    /**
     * Установить приоритетные счета
     */
    String setPrimaryAccounts(String token, List<Integer> accountIds);

    /**
     * Получить счет по ID
     */
    AccountPublic getAccountById(String token, int accountId);
}