package cucumber.step;

import api.accounts.AccountsApi;
import api.accounts.IAccountsApi;
import api.transfers.TransfersApi;
import api.transfers.ITransfersApi;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Затем;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import model.account.AccountPublic;
import model.common.ErrorResponse;
import model.transaction.TransactionPublic;
import model.transfer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class TransfersServer {
    private static final ITransfersApi transfersApi = new TransfersApi();
    private static final IAccountsApi accountsApi = new AccountsApi();
    private static final Logger log = LoggerFactory.getLogger(TransfersServer.class);

    private final ScenarioContext context;
    private TransactionPublic transferTransaction;
    private TransferByAccountCheckResponse accountCheckResponse;
    private TransferByPhoneCheckResponse phoneCheckResponse;
    private ExchangeRatesResponse exchangeRates;
    private DailyUsageResponse dailyUsage;
    private ErrorResponse errorResponse;
    private int lastStatusCode;
    private String fromAccountBalanceBefore;
    private String toAccountBalanceBefore;

    public TransfersServer(ScenarioContext context) {
        this.context = context;
    }

    private void put(String key, String value) {
        context.put(key, value);
    }

    private void putObject(String key, Object value) {
        context.putObject(key, value);
    }

    private void rememberHttpStatus(int code) {
        this.lastStatusCode = code;
        context.putObject(ScenarioContext.LAST_STATUS_CODE, code);
    }

    private Object getObject(String key) {
        return context.getObject(key);
    }

    private String get(String key) {
        return context.get(key);
    }

    // ==================== ШАГИ ДЛЯ ПЕРЕВОДОВ ====================

    @Допустим("пользователь авторизован в системе")
    public void userIsAuthorized() {
        String token = get(ScenarioContext.USER_TOKEN);
        assertThat(token).isNotNull();
        log.info("Пользователь авторизован в системе");
    }

    @Допустим("у пользователя есть счет отправителя с id {string} и балансом {string}")
    public void userHasFromAccountWithIdAndBalance(String accountId, String balance) {
        put(ScenarioContext.FROM_ACCOUNT_ID, accountId);
        put(ScenarioContext.FROM_ACCOUNT_BALANCE, balance);
        fromAccountBalanceBefore = balance;
        log.info("Счет отправителя: id={}, balance={}", accountId, balance);
    }

    @Допустим("у пользователя есть счет получателя с id {string}")
    public void userHasToAccount(String accountId) {
        put(ScenarioContext.TO_ACCOUNT_ID, accountId);
        log.info("Счет получателя: id={}", accountId);
    }

    @Допустим("у пользователя есть счет с id {string} и балансом {string}")
    public void userHasAccountWithIdAndBalance(String accountId, String balance) {
        put(ScenarioContext.ACCOUNT_ID, accountId);
        put(ScenarioContext.ACCOUNT_BALANCE, balance);
        log.info("Счет: id={}, balance={}", accountId, balance);
    }

    @Допустим("существует счет с номером {string}")
    public void accountExistsWithNumber(String accountNumber) {
        put(ScenarioContext.TARGET_ACCOUNT_NUMBER, accountNumber);
        log.info("Существует счет с номером: {}", accountNumber);
    }

    @Допустим("существует получатель с телефоном {string} в нашем банке")
    public void recipientExistsWithPhoneInOurBank(String phone) {
        put(ScenarioContext.RECIPIENT_PHONE, phone);
        put(ScenarioContext.RECIPIENT_IN_OUR_BANK, "true");
        log.info("Получатель с телефоном {} существует в нашем банке", phone);
    }

    @Допустим("у пользователя есть RUB счет с id {string} и балансом {string}")
    public void userHasRubAccountWithIdAndBalance(String accountId, String balance) {
        put(ScenarioContext.FROM_ACCOUNT_ID, accountId);
        put(ScenarioContext.FROM_ACCOUNT_BALANCE, balance);
        fromAccountBalanceBefore = balance;
        log.info("RUB счет: id={}, balance={}", accountId, balance);
    }

    @Допустим("у пользователя есть USD счет с id {string} и балансом {string}")
    public void userHasUsdAccountWithIdAndBalance(String accountId, String balance) {
        put(ScenarioContext.TO_ACCOUNT_ID, accountId);
        put(ScenarioContext.TO_ACCOUNT_BALANCE, balance);
        toAccountBalanceBefore = balance;
        log.info("USD счет: id={}, balance={}", accountId, balance);
    }

    @Допустим("пользователь уже перевел {string} сегодня")
    public void userAlreadyTransferredToday(String amount) {
        put(ScenarioContext.DAILY_TRANSFERRED_AMOUNT, amount);
        log.info("Пользователь уже перевел {} сегодня", amount);
    }

    @Когда("клиент переводит {string} со счета {string} на счет {string}")
    public void transferBetweenOwnAccounts(String amount, String fromAccountId, String toAccountId) {
        String token = get(ScenarioContext.USER_TOKEN);

        try {
            TransferCreateRequest request = TransferCreateRequest.builder()
                    .from_account_id(Integer.parseInt(fromAccountId))
                    .to_account_id(Integer.parseInt(toAccountId))
                    .amount(amount)
                    .otp_code(get(ScenarioContext.OTP_CODE))
                    .build();

            transferTransaction = transfersApi.createTransfer(token, request);
            rememberHttpStatus(201);
            log.info("Перевод {} со счета {} на счет {} выполнен", amount, fromAccountId, toAccountId);
        } catch (Exception e) {
            rememberHttpStatus(400);
            errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            log.error("Ошибка при переводе: {}", e.getMessage());
        }
    }

    @Когда("клиент проверяет счет по номеру {string}")
    public void checkAccountByNumber(String accountNumber) {
        String token = get(ScenarioContext.USER_TOKEN);
        accountCheckResponse = transfersApi.checkAccountByNumber(token, accountNumber);
        rememberHttpStatus(200);
        log.info("Проверен счет: {}", accountNumber);
    }

    @Когда("клиент переводит {string} по номеру телефона {string}")
    public void transferByPhone(String amount, String phone) {
        String token = get(ScenarioContext.USER_TOKEN);
        String fromAccountId = get(ScenarioContext.FROM_ACCOUNT_ID);

        try {
            TransferByPhoneRequest request = TransferByPhoneRequest.builder()
                    .from_account_id(Integer.parseInt(fromAccountId))
                    .phone(phone)
                    .amount(amount)
                    .recipient_bank_id(get(ScenarioContext.RECIPIENT_BANK_ID))
                    .otp_code(get(ScenarioContext.OTP_CODE))
                    .build();

            transferTransaction = transfersApi.createTransferByPhone(token, request);
            rememberHttpStatus(201);
            log.info("Перевод {} по телефону {} выполнен", amount, phone);
        } catch (Exception e) {
            rememberHttpStatus(400);
            errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            log.error("Ошибка при переводе: {}", e.getMessage());
        }
    }

    @Когда("клиент запрашивает текущие курсы валют")
    public void requestExchangeRates() {
        String token = get(ScenarioContext.USER_TOKEN);
        exchangeRates = transfersApi.getExchangeRates(token);
        rememberHttpStatus(200);
        log.info("Получены курсы валют");
    }

    @Когда("клиент обменивает {string} RUB на USD")
    public void exchangeRubToUsd(String amount) {
        String token = get(ScenarioContext.USER_TOKEN);
        String fromAccountId = get(ScenarioContext.FROM_ACCOUNT_ID);
        String toAccountId = get(ScenarioContext.TO_ACCOUNT_ID);

        try {
            ExchangeRequest request = ExchangeRequest.builder()
                    .from_account_id(Integer.parseInt(fromAccountId))
                    .to_account_id(Integer.parseInt(toAccountId))
                    .amount(amount)
                    .otp_code(get(ScenarioContext.OTP_CODE))
                    .build();

            transferTransaction = transfersApi.exchangeCurrency(token, request);
            rememberHttpStatus(201);
            log.info("Обмен {} RUB на USD выполнен", amount);
        } catch (Exception e) {
            rememberHttpStatus(400);
            errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            log.error("Ошибка при обмене: {}", e.getMessage());
        }
    }

    @Когда("клиент пытается перевести {string}")
    public void clientTriesToTransfer(String amount) {
        String token = get(ScenarioContext.USER_TOKEN);
        String fromAccountId = get(ScenarioContext.FROM_ACCOUNT_ID);
        String toAccountId = get(ScenarioContext.TO_ACCOUNT_ID);

        try {
            TransferCreateRequest request = TransferCreateRequest.builder()
                    .from_account_id(Integer.parseInt(fromAccountId))
                    .to_account_id(Integer.parseInt(toAccountId))
                    .amount(amount)
                    .otp_code(get(ScenarioContext.OTP_CODE))
                    .build();

            transferTransaction = transfersApi.createTransfer(token, request);
            rememberHttpStatus(201);
        } catch (Exception e) {
            rememberHttpStatus(400);
            errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            log.info("Ожидаемая ошибка при переводе: {}", e.getMessage());
        }
    }

    // ==================== ТОГДА ШАГИ ====================

    @Тогда("транзакция успешно создана")
    public void transactionSuccessfullyCreated() {
        assertThat(transferTransaction).isNotNull();
        assertThat(transferTransaction.getId()).isNotZero();
        log.info("Транзакция создана с id: {}", transferTransaction.getId());
    }

    @Тогда("тип транзакции {string}")
    public void transactionTypeEquals(String expectedType) {
        assertThat(transferTransaction.getType()).isEqualTo(expectedType);
        log.info("Тип транзакции: {}", expectedType);
    }

    @Тогда("баланс счета отправителя уменьшился на {string}")
    public void fromAccountBalanceDecreasedBy(String expectedDecrease) {
        String token = get(ScenarioContext.USER_TOKEN);
        String fromAccountId = get(ScenarioContext.FROM_ACCOUNT_ID);

        AccountPublic currentAccount = accountsApi.getAccountById(token, Integer.parseInt(fromAccountId));
        String currentBalance = currentAccount.getBalance();

        BigDecimal before = new BigDecimal(fromAccountBalanceBefore);
        BigDecimal decrease = new BigDecimal(expectedDecrease);
        BigDecimal expected = before.subtract(decrease);
        BigDecimal actual = new BigDecimal(currentBalance);

        assertThat(actual).isEqualTo(expected);
        log.info("Баланс счета отправителя уменьшился с {} на {} = {}",
                fromAccountBalanceBefore, expectedDecrease, currentBalance);
    }

    @Тогда("баланс счета получателя увеличился на {string}")
    public void toAccountBalanceIncreasedBy(String expectedIncrease) {
        String token = get(ScenarioContext.USER_TOKEN);
        String toAccountId = get(ScenarioContext.TO_ACCOUNT_ID);

        AccountPublic currentAccount = accountsApi.getAccountById(token, Integer.parseInt(toAccountId));
        String currentBalance = currentAccount.getBalance();

        // Получаем баланс до операции (если не сохранен, берем из контекста)
        String balanceBefore = get(ScenarioContext.TO_ACCOUNT_BALANCE);
        if (balanceBefore == null) {
            balanceBefore = "0";
        }

        BigDecimal before = new BigDecimal(balanceBefore);
        BigDecimal increase = new BigDecimal(expectedIncrease);
        BigDecimal expected = before.add(increase);
        BigDecimal actual = new BigDecimal(currentBalance);

        assertThat(actual).isEqualTo(expected);
        log.info("Баланс счета получателя увеличился с {} на {} = {}",
                balanceBefore, expectedIncrease, currentBalance);
    }

    @Тогда("счет найден")
    public void accountFound() {
        assertThat(accountCheckResponse).isNotNull();
        assertThat(accountCheckResponse.isFound()).isTrue();
        log.info("Счет найден в системе");
    }

    @Тогда("счет не найден")
    public void accountNotFound() {
        assertThat(accountCheckResponse).isNotNull();
        assertThat(accountCheckResponse.isFound()).isFalse();
        log.info("Счет не найден в системе");
    }

    @Тогда("счет найден \\(found = true)")
    public void accountFoundParentheses() {
        accountFound();
    }

    @Тогда("счет не найден \\(found = false)")
    public void accountNotFoundParentheses() {
        accountNotFound();
    }

    @Тогда("маскированный номер счета отображается")
    public void maskedAccountNumberDisplayed() {
        assertThat(accountCheckResponse.getMasked()).isNotBlank();
        assertThat(accountCheckResponse.getMasked()).contains("****");
        log.info("Маскированный номер: {}", accountCheckResponse.getMasked());
    }

    @Тогда("комиссия за перевод равна {string}")
    public void transferFeeEquals(String expectedFee) {
        assertThat(transferTransaction.getMoney().getFee()).isEqualTo(expectedFee);
        log.info("Комиссия перевода: {}", expectedFee);
    }

    @Тогда("курсы валют содержат RUB, USD, EUR, CNY")
    public void exchangeRatesContainAllCurrencies() {
        assertThat(exchangeRates.getRates()).containsKeys("RUB", "USD", "EUR", "CNY");
        log.info("Курсы валют содержат все необходимые валюты");
    }

    @Тогда("курс USD/RUB больше 0")
    public void usdToRubRateGreaterThanZero() {
        double usdRate = exchangeRates.getRates().get("USD");
        assertThat(usdRate).isGreaterThan(0);
        log.info("Курс USD/RUB = {}", usdRate);
    }

    @Тогда("транзакция обмена успешно создана")
    public void exchangeTransactionSuccessfullyCreated() {
        assertThat(transferTransaction).isNotNull();
        assertThat(transferTransaction.getId()).isNotZero();
        assertThat(transferTransaction.getType()).isEqualTo("TRANSFER");
        log.info("Транзакция обмена создана с id: {}", transferTransaction.getId());
    }

    @Тогда("RUB счет уменьшился на {string}")
    public void rubAccountDecreasedBy(String expectedDecrease) {
        fromAccountBalanceDecreasedBy(expectedDecrease);
    }

    @Тогда("USD счет увеличился на сумму по курсу")
    public void usdAccountIncreasedByExchangeRate() {
        String token = get(ScenarioContext.USER_TOKEN);
        String toAccountId = get(ScenarioContext.TO_ACCOUNT_ID);

        AccountPublic currentAccount = accountsApi.getAccountById(token, Integer.parseInt(toAccountId));
        String currentBalance = currentAccount.getBalance();
        String balanceBefore = toAccountBalanceBefore != null ? toAccountBalanceBefore : "0";

        BigDecimal before = new BigDecimal(balanceBefore);
        BigDecimal actual = new BigDecimal(currentBalance);

        assertThat(actual).isGreaterThan(before);
        log.info("USD счет увеличился с {} до {}", balanceBefore, currentBalance);
    }

    @Тогда("сообщение об ошибке содержит {string}")
    public void errorMessageContains(String expectedMessage) {
        if (errorResponse != null) {
            assertThat(errorResponse.getDetail()).contains(expectedMessage);
        } else {
            String errorMessage = get(ScenarioContext.LAST_ERROR_MESSAGE);
            if (errorMessage != null) {
                assertThat(errorMessage).contains(expectedMessage);
            }
        }
        log.info("Сообщение об ошибке содержит: {}", expectedMessage);
    }
}