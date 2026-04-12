package cucumber.step;

import api.accounts.AccountsApi;
import api.accounts.IAccountsApi;
import helpers.TestConstants;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import model.account.*;
import model.common.ErrorResponse;
import model.transaction.TransactionPublic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import java.util.List;

import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;
import static utils.ScenarioContext.*;
import static utils.ScenarioContext.USER_TOKEN;

public class AccountsServer extends BaseServer {
    private static final IAccountsApi accountsApi = new AccountsApi();
    private static final Logger log = LoggerFactory.getLogger(AccountsServer.class);


    private List<AccountPublic> accountsList;
    private AccountPublic createdAccount;
    private TransactionPublic topupTransaction;
    private String actionResponse;

    public AccountsServer(ScenarioContext context) {
        super(context);
    }

    private void rememberHttpStatus(int code) {
        context.putObject(LAST_STATUS_CODE, code);
    }

    @Когда("клиент запрашивает список своих счетов")
    public void requestAccountsList() {
        String token = get(USER_TOKEN);
        accountsList = accountsApi.getAccounts(token);
        log.info("Получен список счетов, количество: {}", accountsList.size());
    }

    @Когда("клиент открывает новый счет типа {string} с валютой {string}")
    public void openNewAccount(String accountType, String currency) {
        String token = get(USER_TOKEN);

        AccountCreateRequest request = AccountCreateRequest.builder()
                .account_type(AccountType.valueOf(accountType))
                .currency(Currency.valueOf(currency))
                .build();

        createdAccount = accountsApi.createAccount(token, request);
        put(ACCOUNT_ID, String.valueOf(createdAccount.getId()));
        rememberHttpStatus(201);
        log.info("Создан новый счет: id={}, type={}, currency={}",
                createdAccount.getId(), accountType, currency);
    }

    @Когда("клиент пополняет счет на сумму {string}")
    public void topUpAccount(String amount) {
        String token = get(USER_TOKEN);
        String accountId = get(ACCOUNT_ID);

        AccountTopupRequest request = AccountTopupRequest.builder()
                .amount(amount)
                .otp_code(get(OTP_CODE))
                .build();

        topupTransaction = accountsApi.topUpAccount(token, Integer.parseInt(accountId), request);
        rememberHttpStatus(201);
        context.putObject(LAST_TRANSACTION, topupTransaction);
        log.info("Счет {} пополнен на сумму {}", accountId, amount);
    }

    @Когда("клиент закрывает счет")
    public void closeAccount() {
        String token = get(USER_TOKEN);
        String accountId = get(ACCOUNT_ID);

        try {
            actionResponse = accountsApi.closeAccount(token, parseInt(accountId));
            rememberHttpStatus(200);
            log.info("Счет {} закрыт", accountId);
        } catch (Exception e) {
            log.error("Ошибка при закрытии счета: {}", e.getMessage());
        }
    }

    // ==================== НОВЫЕ ШАГИ ДЛЯ НЕГАТИВНЫХ СЦЕНАРИЕВ ====================

    @Когда("клиент пытается закрыть счет")
    public void clientTriesToCloseAccount() {
        String token = get(USER_TOKEN);
        String accountId = get(ACCOUNT_ID);

        try {
            // Пытаемся закрыть счет, но ожидаем ошибку
            accountsApi.closeAccount(token, parseInt(accountId));
            rememberHttpStatus(200);
        } catch (Exception e) {
            // Сохраняем информацию об ошибке
            rememberHttpStatus(400);
            // ← для хранения ошибки
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            context.putObject(ERROR_RESPONSE, errorResponse);
            log.info("Ожидаемая ошибка при закрытии счета: {}", e.getMessage());
        }
    }

    @Когда("клиент пытается закрыть счет с положительным балансом")
    public void clientTriesToCloseAccountWithPositiveBalance() {
        clientTriesToCloseAccount();
    }

    @Тогда("регистрация должна завершиться ошибкой с кодом {int}")
    public void registrationShouldFailWithStatusCode(int expectedStatusCode) {
        Integer code = (Integer) context.getObject(LAST_STATUS_CODE);
        assertThat(code).isEqualTo(expectedStatusCode);
        log.info("Регистрация завершилась ошибкой с кодом: {}", expectedStatusCode);
    }

    @Тогда("авторизация должна завершиться ошибкой с кодом {int}")
    public void authorizationShouldFailWithStatusCode(int expectedStatusCode) {
        Integer code = (Integer) context.getObject(LAST_STATUS_CODE);
        assertThat(code).isEqualTo(expectedStatusCode);
        log.info("Авторизация завершилась ошибкой с кодом: {}", expectedStatusCode);
    }

    @Тогда("попытка закрыть счет должна завершиться ошибкой")
    public void closeAccountShouldFail() {
        Integer code = (Integer) context.getObject(LAST_STATUS_CODE);
        assertThat(code).isNotNull().isNotEqualTo(200);
        log.info("Попытка закрыть счет завершилась ошибкой");
    }

    // ==================== СУЩЕСТВУЮЩИЕ ШАГИ ====================

    @Тогда("список счетов не пустой")
    public void accountsListNotEmpty() {
        assertThat(accountsList).isNotNull();
        assertThat(accountsList).isNotEmpty();
        log.info("Список счетов не пустой, размер: {}", accountsList.size());
    }

    @Тогда("каждый счет содержит id, account_number, currency, balance")
    public void eachAccountContainsRequiredFields() {
        for (AccountPublic account : accountsList) {
            assertThat(account.getId()).isNotZero();
            assertThat(account.getAccount_number()).isNotBlank();
            assertThat(account.getCurrency()).isNotNull();
            assertThat(account.getBalance()).isNotNull();
        }
        log.info("Все счета содержат обязательные поля");
    }

    @Тогда("счет успешно создан")
    public void accountSuccessfullyCreated() {
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getId()).isNotZero();
        log.info("Счет успешно создан с id: {}", createdAccount.getId());
    }

    @Тогда("тип счета {string}")
    public void accountTypeIs(String expectedType) {
        assertThat(createdAccount.getAccount_type()).isEqualTo(expectedType);
        log.info("Тип счета соответствует: {}", expectedType);
    }

    @Тогда("валюта счета {string}")
    public void accountCurrencyIs(String expectedCurrency) {
        assertThat(createdAccount.getCurrency()).isEqualTo(expectedCurrency);
        log.info("Валюта счета соответствует: {}", expectedCurrency);
    }

    @Тогда("баланс счета равен {string}")
    public void accountBalanceEquals(String expectedBalance) {
        assertThat(createdAccount.getBalance()).isEqualTo(expectedBalance);
        log.info("Баланс счета соответствует: {}", expectedBalance);
    }

    @Тогда("сумма транзакции равна {string}")
    public void transactionAmountEquals(String expectedAmount) {
        assertThat(topupTransaction.getMoney().getAmount()).isEqualTo(expectedAmount);
        log.info("Сумма транзакции соответствует: {}", expectedAmount);
    }

    @Тогда("счет успешно закрыт")
    public void accountSuccessfullyClosed() {
        assertThat(actionResponse).contains("closed");
        log.info("Счет успешно закрыт");
    }
}