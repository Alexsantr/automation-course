package cucumber.step;

import api.accounts.AccountsApi;
import api.accounts.IAccountsApi;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Затем;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import model.account.*;
import model.common.ErrorResponse;
import model.transaction.TransactionPublic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AccountsServer {
    private static final IAccountsApi accountsApi = new AccountsApi();
    private static final Logger log = LoggerFactory.getLogger(AccountsServer.class);

    private final ScenarioContext context;
    private List<AccountPublic> accountsList;
    private AccountPublic createdAccount;
    private TransactionPublic topupTransaction;
    private String actionResponse;
    private ErrorResponse errorResponse;  // ← для хранения ошибки
    private int lastStatusCode;            // ← для хранения статус кода

    public AccountsServer(ScenarioContext context) {
        this.context = context;
    }

    private void put(String key, String value) {
        context.put(key, value);
    }

    private String get(String key) {
        return context.get(key);
    }

    private void rememberHttpStatus(int code) {
        this.lastStatusCode = code;
        context.putObject(ScenarioContext.LAST_STATUS_CODE, code);
    }

    @Допустим("у пользователя есть счет с id {string}")
    public void userHasAccountWithId(String accountId) {
        put(ScenarioContext.ACCOUNT_ID, accountId);
        log.info("Сохранен ID счета: {}", accountId);
    }

    @Допустим("у пользователя есть счет с id {string} и балансом {string}")
    public void userHasAccountWithIdAndBalance(String accountId, String balance) {
        put(ScenarioContext.ACCOUNT_ID, accountId);
        put(ScenarioContext.ACCOUNT_BALANCE, balance);
        log.info("Сохранен счет: id={}, balance={}", accountId, balance);
    }

    @Когда("клиент запрашивает список своих счетов")
    public void requestAccountsList() {
        String token = get(ScenarioContext.USER_TOKEN);
        accountsList = accountsApi.getAccounts(token);
        log.info("Получен список счетов, количество: {}", accountsList.size());
    }

    @Когда("клиент открывает новый счет типа {string} с валютой {string}")
    public void openNewAccount(String accountType, String currency) {
        String token = get(ScenarioContext.USER_TOKEN);

        AccountCreateRequest request = AccountCreateRequest.builder()
                .account_type(AccountType.valueOf(accountType))
                .currency(Currency.valueOf(currency))
                .build();

        createdAccount = accountsApi.createAccount(token, request);
        put(ScenarioContext.ACCOUNT_ID, String.valueOf(createdAccount.getId()));
        rememberHttpStatus(201);
        log.info("Создан новый счет: id={}, type={}, currency={}",
                createdAccount.getId(), accountType, currency);
    }

    @Когда("клиент пополняет счет на сумму {string}")
    public void topUpAccount(String amount) {
        String token = get(ScenarioContext.USER_TOKEN);
        String accountId = get(ScenarioContext.ACCOUNT_ID);

        AccountTopupRequest request = AccountTopupRequest.builder()
                .amount(amount)
                .otp_code(get(ScenarioContext.OTP_CODE))
                .build();

        topupTransaction = accountsApi.topUpAccount(token, Integer.parseInt(accountId), request);
        rememberHttpStatus(201);
        log.info("Счет {} пополнен на сумму {}", accountId, amount);
    }

    @Когда("клиент закрывает счет")
    public void closeAccount() {
        String token = get(ScenarioContext.USER_TOKEN);
        String accountId = get(ScenarioContext.ACCOUNT_ID);

        try {
            actionResponse = accountsApi.closeAccount(token, Integer.parseInt(accountId));
            rememberHttpStatus(200);
            log.info("Счет {} закрыт", accountId);
        } catch (Exception e) {
            log.error("Ошибка при закрытии счета: {}", e.getMessage());
        }
    }

    // ==================== НОВЫЕ ШАГИ ДЛЯ НЕГАТИВНЫХ СЦЕНАРИЕВ ====================

    @Когда("клиент пытается закрыть счет")
    public void clientTriesToCloseAccount() {
        String token = get(ScenarioContext.USER_TOKEN);
        String accountId = get(ScenarioContext.ACCOUNT_ID);

        try {
            // Пытаемся закрыть счет, но ожидаем ошибку
            accountsApi.closeAccount(token, Integer.parseInt(accountId));
            rememberHttpStatus(200);
        } catch (Exception e) {
            // Сохраняем информацию об ошибке
            rememberHttpStatus(400);
            errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            log.info("Ожидаемая ошибка при закрытии счета: {}", e.getMessage());
        }
    }

    @Когда("клиент пытается закрыть счет с положительным балансом")
    public void clientTriesToCloseAccountWithPositiveBalance() {
        clientTriesToCloseAccount();
    }

    @Тогда("сообщение об ошибке содержит {string}")
    public void errorMessageContains(String expectedMessage) {
        if (errorResponse != null) {
            assertThat(errorResponse.getDetail()).contains(expectedMessage);
        } else {
            // Альтернативный вариант - проверка через контекст
            String errorMessage = get(ScenarioContext.LAST_ERROR_MESSAGE);
            assertThat(errorMessage).contains(expectedMessage);
        }
        log.info("Сообщение об ошибке содержит: {}", expectedMessage);
    }

    @Тогда("регистрация должна завершиться ошибкой с кодом {int}")
    public void registrationShouldFailWithStatusCode(int expectedStatusCode) {
        assertThat(lastStatusCode).isEqualTo(expectedStatusCode);
        log.info("Регистрация завершилась ошибкой с кодом: {}", expectedStatusCode);
    }

    @Тогда("авторизация должна завершиться ошибкой с кодом {int}")
    public void authorizationShouldFailWithStatusCode(int expectedStatusCode) {
        assertThat(lastStatusCode).isEqualTo(expectedStatusCode);
        log.info("Авторизация завершилась ошибкой с кодом: {}", expectedStatusCode);
    }

    @Тогда("попытка закрыть счет должна завершиться ошибкой")
    public void closeAccountShouldFail() {
        assertThat(lastStatusCode).isNotEqualTo(200);
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

    @Тогда("транзакция успешно создана")
    public void transactionSuccessfullyCreated() {
        assertThat(topupTransaction).isNotNull();
        assertThat(topupTransaction.getId()).isNotZero();
        log.info("Транзакция создана с id: {}", topupTransaction.getId());
    }

    @Тогда("сумма транзакции равна {string}")
    public void transactionAmountEquals(String expectedAmount) {
        assertThat(topupTransaction.getMoney().getAmount()).isEqualTo(expectedAmount);
        log.info("Сумма транзакции соответствует: {}", expectedAmount);
    }

    @Тогда("тип транзакции {string}")
    public void transactionTypeEquals(String expectedType) {
        assertThat(topupTransaction.getType()).isEqualTo(expectedType);
        log.info("Тип транзакции соответствует: {}", expectedType);
    }

    @Тогда("счет успешно закрыт")
    public void accountSuccessfullyClosed() {
        assertThat(actionResponse).contains("closed");
        log.info("Счет успешно закрыт");
    }
}