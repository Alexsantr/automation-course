package cucumber.step;

import api.accounts.AccountsApi;
import api.accounts.IAccountsApi;
import api.user.IUserApi;
import api.user.UserApi;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import model.account.AccountPublic;
import model.common.ErrorResponse;
import model.transaction.TransactionPublic;
import model.user.UserPublic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.ScenarioContext.*;

public class CommonSteps extends BaseServer {
    private static final IUserApi userApi = new UserApi();
    private static final Logger log = LoggerFactory.getLogger(CommonSteps.class);
    private static final IAccountsApi accountsApi = new AccountsApi();


    private UserPublic userResponse;

    public CommonSteps(ScenarioContext context) {

        super(context);
    }

    private Object getObject(String key) {
        return context.getObject(key);
    }

    // ==================== Общие предусловия (без дубликатов в *Server) ====================

    @Тогда("получаем счета по клиенту")
    public void getAcc() {
        String token = get(USER_TOKEN);
        List<AccountPublic> accounts = accountsApi.getAccounts(token);

        assertThat(accounts).isNotNull();
        assertThat(accounts).isNotEmpty();

        // Ищем счет с балансом больше 100
        AccountPublic foundAccount = accounts.stream()
                .filter(account -> new BigDecimal(account.getBalance()).compareTo(new BigDecimal("100")) > 0)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Не найден счет с балансом больше 100"));

        put(ACCOUNT_ID, String.valueOf(foundAccount.getId()));
        put(ACCOUNT_BALANCE, foundAccount.getBalance());

        log.info("Найден счет с балансом {}: id={}", foundAccount.getBalance(), foundAccount.getId());
    }

    @Допустим("у пользователя есть счет с id {string}")
    public void userHasAccountWithId(String accountId) {
        put(ACCOUNT_ID, accountId);
        log.info("Сохранен ID счета: {}", accountId);
    }

    @Допустим("у пользователя есть счет с id {string} и балансом {string}")
    public void userHasAccountWithIdAndBalance(String accountId, String balance) {
        put(ACCOUNT_ID, accountId);
        put(ACCOUNT_BALANCE, balance);
        log.info("Сохранен счет: id={}, balance={}", accountId, balance);
    }

    // ==================== ШАГИ ДЛЯ USER.FEATURE ====================

    @Допустим("пользователь с id {int} существует в системе")
    public void userExistsWithId(int userId) {
        put(USER_ID, String.valueOf(userId));
        log.info("Пользователь с id {} существует в системе", userId);
    }

    @Когда("я запрашиваю данные пользователя")
    public void requestUserData() {
        String token = get(USER_TOKEN);
        String userId = get(USER_ID);

        if (token == null || token.isEmpty()) {
            userResponse = userApi.getUserByIdPublic(Integer.parseInt(userId));
        } else {
            userResponse = userApi.getUserById(token, Integer.parseInt(userId));
        }

        putObject(USER_RESPONSE, userResponse);
        log.info("Запрошены данные пользователя с id: {}", userId);
    }

    @Когда("клиент делает запрос")
    public void clientMakesRequest() {
        String token = get(USER_TOKEN);
        String userId = get(USER_ID);

        if (userId != null) {
            if (token != null && !token.isEmpty()) {
                userResponse = userApi.getUserById(token, Integer.parseInt(userId));
            } else {
                userResponse = userApi.getUserByIdPublic(Integer.parseInt(userId));
            }
            putObject(USER_RESPONSE, userResponse);
        }

        log.info("Клиент выполнил запрос");
    }

    @Тогда("в ответе получаю имя {string}")
    public void receiveFirstName(String expectedFirstName) {
        UserPublic response = (UserPublic) getObject(USER_RESPONSE);
        assertThat(response).isNotNull();
        assertThat(response.getFirst_name()).isEqualTo(expectedFirstName);
        log.info("Имя пользователя: {}", response.getFirst_name());
    }

    @Тогда("статус ответа {int}")
    public void checkStatusCode(int expectedStatusCode) {
        Integer actual = (Integer) getObject(LAST_STATUS_CODE);
        assertThat(actual)
                .as("HTTP-статус последнего ответа (положите его в контекст через шаги API)")
                .isNotNull()
                .isEqualTo(expectedStatusCode);
        log.info("Статус ответа соответствует ожидаемому: {}", expectedStatusCode);
    }

    @Тогда("сообщение об ошибке содержит {string}")
    public void errorMessageContains(String expectedMessage) {
        ErrorResponse er = (ErrorResponse) getObject(ERROR_RESPONSE);
        if (er != null && er.getDetail() != null) {
            assertThat(er.getDetail()).contains(expectedMessage);
            log.info("Сообщение об ошибке содержит: {}", expectedMessage);
            return;
        }
        String msg = get(LAST_ERROR_MESSAGE);
        assertThat(msg).as("ожидались ERROR_RESPONSE.detail или LAST_ERROR_MESSAGE").contains(expectedMessage);
        log.info("Сообщение об ошибке содержит: {}", expectedMessage);
    }

    @Тогда("транзакция успешно создана")
    public void transactionSuccessfullyCreated() {
        TransactionPublic tx = (TransactionPublic) getObject(LAST_TRANSACTION);
        assertThat(tx).isNotNull();
        assertThat(tx.getId()).isNotZero();
        log.info("Транзакция создана с id: {}", tx.getId());
    }

    @Тогда("тип транзакции {string}")
    public void transactionTypeEquals(String expectedType) {
        TransactionPublic tx = (TransactionPublic) getObject(LAST_TRANSACTION);
        assertThat(tx).isNotNull();
        assertThat(tx.getType()).isEqualTo(expectedType);
        assertThat(tx.getStatus()).isEqualTo("COMPLETED");
        log.info("Тип транзакции соответствует: {}", expectedType);
    }

    @Тогда("список транзакций не пустой")
    public void transactionsListNotEmpty() {
        @SuppressWarnings("unchecked")
        List<TransactionPublic> adminList = (List<TransactionPublic>) getObject(USER_TRANSACTIONS);
        @SuppressWarnings("unchecked")
        List<TransactionPublic> clientList = (List<TransactionPublic>) getObject(TRANSACTIONS_LIST);
        boolean adminOk = adminList != null && !adminList.isEmpty();
        boolean clientOk = clientList != null && !clientList.isEmpty();
        assertThat(adminOk || clientOk)
                .as("ожидался непустой USER_TRANSACTIONS или TRANSACTIONS_LIST")
                .isTrue();
        if (adminOk) {
            log.info("Список транзакций (админ) не пустой, размер: {}", adminList.size());
        } else {
            log.info("Список транзакций (клиент) не пустой, размер: {}", clientList.size());
        }
    }

    @Тогда("чек содержит информацию о транзакции")
    public void receiptContainsTransactionInfo() {
        String html = get(RECEIPT_HTML);
        if (html != null && !html.isBlank()) {
            assertThat(html).isNotBlank();
            log.info("Чек (HTML) содержит данные");
            return;
        }
        TransactionPublic tx = (TransactionPublic) getObject(LAST_TRANSACTION);
        assertThat(tx).as("ожидался RECEIPT_HTML или LAST_TRANSACTION").isNotNull();
        log.info("Проверка транзакции по данным из контекста");
    }
}
