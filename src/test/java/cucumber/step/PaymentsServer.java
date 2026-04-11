package cucumber.step;

import api.accounts.AccountsApi;
import api.accounts.IAccountsApi;
import api.payments.PaymentsApi;
import api.payments.IPaymentsApi;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import model.account.AccountPublic;
import model.common.ErrorResponse;
import model.payment.MobilePaymentRequest;
import model.payment.VendorPaymentRequest;
import model.transaction.TransactionPublic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentsServer {
    private static final IPaymentsApi paymentsApi = new PaymentsApi();
    private static final IAccountsApi accountsApi = new AccountsApi();
    private static final Logger log = LoggerFactory.getLogger(PaymentsServer.class);

    private final ScenarioContext context;
    private List<Map<String, String>> mobileOperators;
    private List<Map<String, String>> vendorProviders;
    private TransactionPublic paymentTransaction;
    private ErrorResponse errorResponse;
    private int lastStatusCode;
    private String balanceBefore;

    public PaymentsServer(ScenarioContext context) {
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

    // ==================== ШАГИ ДЛЯ ПЛАТЕЖЕЙ ====================

    @Допустим("у пользователя есть счет с id {string} и балансом {string}")
    public void userHasAccountWithIdAndBalance(String accountId, String balance) {
        put(ScenarioContext.ACCOUNT_ID, accountId);
        put(ScenarioContext.ACCOUNT_BALANCE, balance);
        balanceBefore = balance;
        log.info("У пользователя есть счет: id={}, balance={}", accountId, balance);
    }

    @Когда("клиент запрашивает список мобильных операторов")
    public void requestMobileOperators() {
        String token = get(ScenarioContext.USER_TOKEN);
        mobileOperators = paymentsApi.getMobileOperators(token);
        log.info("Получен список мобильных операторов, количество: {}", mobileOperators.size());
    }

    @Когда("клиент оплачивает мобильную связь оператору {string} на номер {string} сумму {string}")
    public void payMobile(String operator, String phone, String amount) {
        String token = get(ScenarioContext.USER_TOKEN);
        String accountId = get(ScenarioContext.ACCOUNT_ID);

        // Сохраняем баланс до оплаты
        try {
            AccountPublic accountBefore = accountsApi.getAccountById(token, Integer.parseInt(accountId));
            balanceBefore = accountBefore.getBalance();
            put(ScenarioContext.ACCOUNT_BALANCE, balanceBefore);
        } catch (Exception e) {
            log.warn("Не удалось получить баланс до оплаты: {}", e.getMessage());
        }

        MobilePaymentRequest request = MobilePaymentRequest.builder()
                .account_id(Integer.parseInt(accountId))
                .operator(operator)
                .phone(phone)
                .amount(amount)
                .otp_code(get(ScenarioContext.OTP_CODE))
                .build();

        try {
            paymentTransaction = paymentsApi.payMobile(token, request);
            rememberHttpStatus(201);
            log.info("Оплачена мобильная связь: оператор={}, телефон={}, сумма={}", operator, phone, amount);
        } catch (Exception e) {
            rememberHttpStatus(400);
            errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            log.error("Ошибка при оплате: {}", e.getMessage());
        }
    }

    @Когда("клиент оплачивает поставщику {string} по лицевому счету {string} сумму {string}")
    public void payVendor(String provider, String accountNumber, String amount) {
        String token = get(ScenarioContext.USER_TOKEN);
        String accountId = get(ScenarioContext.ACCOUNT_ID);

        // Сохраняем баланс до оплаты
        try {
            AccountPublic accountBefore = accountsApi.getAccountById(token, Integer.parseInt(accountId));
            balanceBefore = accountBefore.getBalance();
            put(ScenarioContext.ACCOUNT_BALANCE, balanceBefore);
        } catch (Exception e) {
            log.warn("Не удалось получить баланс до оплаты: {}", e.getMessage());
        }

        VendorPaymentRequest request = VendorPaymentRequest.builder()
                .account_id(Integer.parseInt(accountId))
                .provider(provider)
                .account_number(accountNumber)
                .amount(amount)
                .otp_code(get(ScenarioContext.OTP_CODE))
                .build();

        try {
            paymentTransaction = paymentsApi.payVendor(token, request);
            rememberHttpStatus(201);
            log.info("Оплачен поставщик: provider={}, account={}, сумма={}", provider, accountNumber, amount);
        } catch (Exception e) {
            rememberHttpStatus(400);
            errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            log.error("Ошибка при оплате: {}", e.getMessage());
        }
    }

    @Когда("клиент пытается оплатить сумму {string}")
    public void clientTriesToPayAmount(String amount) {
        String token = get(ScenarioContext.USER_TOKEN);
        String accountId = get(ScenarioContext.ACCOUNT_ID);

        // Сохраняем баланс до оплаты
        try {
            AccountPublic accountBefore = accountsApi.getAccountById(token, Integer.parseInt(accountId));
            balanceBefore = accountBefore.getBalance();
            put(ScenarioContext.ACCOUNT_BALANCE, balanceBefore);
        } catch (Exception e) {
            log.warn("Не удалось получить баланс до оплаты: {}", e.getMessage());
        }

        MobilePaymentRequest request = MobilePaymentRequest.builder()
                .account_id(Integer.parseInt(accountId))
                .operator("MTSha")
                .phone("+79991234567")
                .amount(amount)
                .otp_code(get(ScenarioContext.OTP_CODE))
                .build();

        try {
            paymentTransaction = paymentsApi.payMobile(token, request);
            rememberHttpStatus(201);
        } catch (Exception e) {
            rememberHttpStatus(400);
            errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            log.error("Ожидаемая ошибка при оплате: {}", e.getMessage());
        }
    }

    @Когда("клиент запрашивает список поставщиков услуг")
    public void requestVendorProviders() {
        String token = get(ScenarioContext.USER_TOKEN);
        vendorProviders = paymentsApi.getVendorProviders(token);
        log.info("Получен список поставщиков, количество: {}", vendorProviders.size());
    }

    @Тогда("список операторов не пустой")
    public void operatorsListNotEmpty() {
        assertThat(mobileOperators).isNotNull();
        assertThat(mobileOperators).isNotEmpty();
        log.info("Список операторов не пустой");
    }

    @Тогда("список поставщиков не пустой")
    public void providersListNotEmpty() {
        assertThat(vendorProviders).isNotNull();
        assertThat(vendorProviders).isNotEmpty();
        log.info("Список поставщиков не пустой");
    }

    @Тогда("операторы содержат названия и id")
    public void operatorsContainNamesAndIds() {
        for (Map<String, String> operator : mobileOperators) {
            assertThat(operator).containsKey("id");
            assertThat(operator).containsKey("label");
        }
        log.info("Все операторы содержат id и label");
    }

    @Тогда("транзакция успешно создана")
    public void transactionSuccessfullyCreated() {
        assertThat(paymentTransaction).isNotNull();
        assertThat(paymentTransaction.getId()).isNotZero();
        log.info("Транзакция создана с id: {}", paymentTransaction.getId());
    }

    @Тогда("тип транзакции {string}")
    public void transactionTypeEquals(String expectedType) {
        assertThat(paymentTransaction.getType()).isEqualTo(expectedType);
        log.info("Тип транзакции: {}", expectedType);
    }

    @Тогда("баланс счета уменьшился на {string}")
    public void accountBalanceDecreasedBy(String expectedDecrease) {
        String token = get(ScenarioContext.USER_TOKEN);
        String accountId = get(ScenarioContext.ACCOUNT_ID);

        // Получаем текущий баланс
        AccountPublic currentAccount = accountsApi.getAccountById(token, Integer.parseInt(accountId));
        String currentBalance = currentAccount.getBalance();

        // Вычисляем ожидаемый баланс
        BigDecimal before = new BigDecimal(balanceBefore);
        BigDecimal decrease = new BigDecimal(expectedDecrease);
        BigDecimal expected = before.subtract(decrease);
        BigDecimal actual = new BigDecimal(currentBalance);

        assertThat(actual).isEqualTo(expected);
        log.info("Баланс счета уменьшился с {} на {} = {}",
                balanceBefore, expectedDecrease, currentBalance);
    }

    @Тогда("операция не выполнена из-за недостаточного баланса")
    public void operationFailedDueToInsufficientFunds() {
        assertThat(lastStatusCode).isEqualTo(400);
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getDetail()).contains("Insufficient funds");
        log.info("Операция не выполнена из-за недостаточного баланса");
    }

    @Тогда("сообщение об ошибке содержит {string}")
    public void errorMessageContains(String expectedMessage) {
        if (errorResponse != null) {
            assertThat(errorResponse.getDetail()).contains(expectedMessage);
        } else {
            String errorMessage = get(ScenarioContext.LAST_ERROR_MESSAGE);
            assertThat(errorMessage).contains(expectedMessage);
        }
        log.info("Сообщение об ошибке содержит: {}", expectedMessage);
    }

    @Тогда("чек содержит информацию о транзакции")
    public void receiptContainsTransactionInfo() {
        // Проверяем, что чек содержит информацию
        assertThat(paymentTransaction).isNotNull();
        log.info("Чек содержит информацию о транзакции");
    }
}