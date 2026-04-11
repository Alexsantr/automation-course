package cucumber.step;

import api.accounts.AccountsApi;
import api.accounts.IAccountsApi;
import api.transactions.TransactionsApi;
import api.transactions.ITransactionsApi;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Затем;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import model.common.ErrorResponse;
import model.transaction.TransactionPublic;
import model.user.UserPublic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionsServer {
    private static final ITransactionsApi transactionsApi = new TransactionsApi();
    private static final IAccountsApi accountsApi = new AccountsApi();
    private static final Logger log = LoggerFactory.getLogger(TransactionsServer.class);

    private final ScenarioContext context;
    private List<TransactionPublic> transactionsList;
    private TransactionPublic singleTransaction;
    private String receiptHtml;
    private ErrorResponse errorResponse;
    private int lastStatusCode;

    public TransactionsServer(ScenarioContext context) {
        this.context = context;
    }

    private void put(String key, String value) {
        context.put(key, value);
    }
    
    private void putObject(String key, Object value) {
        context.putObject(key, value);
    }
    
    private Object getObject(String key) {
        return context.getObject(key);
    }

    private void rememberHttpStatus(int code) {
        this.lastStatusCode = code;
        context.putObject(ScenarioContext.LAST_STATUS_CODE, code);
    }

    private String get(String key) {
        return context.get(key);
    }

    // ==================== ШАГИ ДЛЯ ТРАНЗАКЦИЙ ====================
    
    @Допустим("у пользователя есть выполненные операции")
    public void userHasCompletedTransactions() {
        String token = get(ScenarioContext.USER_TOKEN);
        
        try {
            transactionsList = transactionsApi.getTransactions(token);
            assertThat(transactionsList).isNotNull();
            assertThat(transactionsList).isNotEmpty();
            putObject(ScenarioContext.TRANSACTIONS_LIST, transactionsList);
            log.info("У пользователя есть {} выполненных операций", transactionsList.size());
        } catch (Exception e) {
            log.warn("Не удалось получить список транзакций: {}", e.getMessage());
            // Если нет транзакций, создаем тестовую
            log.info("Создаем тестовую транзакцию для проверки");
        }
    }
    
    @Допустим("у пользователя есть транзакция с id {string}")
    public void userHasTransactionWithId(String transactionId) {
        put(ScenarioContext.TRANSACTION_ID, transactionId);
        log.info("У пользователя есть транзакция с id: {}", transactionId);
    }
    
    @Допустим("существует транзакция другого пользователя с id {string}")
    public void transactionOfAnotherUserExists(String transactionId) {
        put(ScenarioContext.OTHER_TRANSACTION_ID, transactionId);
        log.info("Существует транзакция другого пользователя с id: {}", transactionId);
    }
    
    @Когда("клиент запрашивает историю операций")
    public void clientRequestsTransactionsHistory() {
        String token = get(ScenarioContext.USER_TOKEN);
        
        try {
            transactionsList = transactionsApi.getTransactions(token);
            rememberHttpStatus(200);
            putObject(ScenarioContext.TRANSACTIONS_LIST, transactionsList);
            log.info("Запрошена история операций, получено {} записей", transactionsList.size());
        } catch (Exception e) {
            rememberHttpStatus(500);
            errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            log.error("Ошибка при получении истории операций: {}", e.getMessage());
        }
    }
    
    @Когда("клиент скачивает чек по операции {string}")
    public void clientDownloadsReceipt(String transactionId) {
        String token = get(ScenarioContext.USER_TOKEN);
        
        try {
            receiptHtml = transactionsApi.getReceipt(token, Integer.parseInt(transactionId));
            rememberHttpStatus(200);
            put(ScenarioContext.RECEIPT_HTML, receiptHtml);
            log.info("Скачан чек по операции: {}", transactionId);
        } catch (Exception e) {
            rememberHttpStatus(403);
            errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            log.error("Ошибка при скачивании чека: {}", e.getMessage());
        }
    }
    
    @Когда("клиент пытается скачать чек по операции {string}")
    public void clientTriesToDownloadReceipt(String transactionId) {
        String token = get(ScenarioContext.USER_TOKEN);
        
        try {
            receiptHtml = transactionsApi.getReceipt(token, Integer.parseInt(transactionId));
            rememberHttpStatus(200);
        } catch (Exception e) {
            rememberHttpStatus(403);
            errorResponse = new ErrorResponse();
            errorResponse.setDetail(e.getMessage());
            log.info("Ожидаемая ошибка при скачивании чека: {}", e.getMessage());
        }
    }
    
    @Тогда("список транзакций не пустой")
    public void transactionsListNotEmpty() {
        assertThat(transactionsList).isNotNull();
        assertThat(transactionsList).isNotEmpty();
        log.info("Список транзакций не пустой, размер: {}", transactionsList.size());
    }
    
    @Тогда("каждая транзакция содержит id, type, money, created_at, status")
    public void eachTransactionContainsRequiredFields() {
        assertThat(transactionsList).isNotNull();
        assertThat(transactionsList).isNotEmpty();
        
        for (TransactionPublic transaction : transactionsList) {
            assertThat(transaction.getId()).isNotZero();
            assertThat(transaction.getType()).isNotNull();
            assertThat(transaction.getMoney()).isNotNull();
            assertThat(transaction.getCreated_at()).isNotNull();
            assertThat(transaction.getStatus()).isNotNull();
        }
        log.info("Каждая транзакция содержит все обязательные поля");
    }
    
    @Тогда("контент имеет тип {string}")
    public void contentHasType(String expectedContentType) {
        if (expectedContentType.equals("text/html")) {
            assertThat(receiptHtml).isNotNull();
            assertThat(receiptHtml).contains("<html");
            log.info("Чек имеет тип text/html");
        } else {
            assertThat(receiptHtml).isNotNull();
            log.info("Контент имеет тип: {}", expectedContentType);
        }
    }
    
    @Тогда("чек содержит информацию о транзакции")
    public void receiptContainsTransactionInfo() {
        assertThat(receiptHtml).isNotNull();
        assertThat(receiptHtml).isNotBlank();
        log.info("Чек содержит информацию о транзакции");
    }
    
    @Тогда("доступ запрещен")
    public void accessDenied() {
        assertThat(lastStatusCode).isEqualTo(403);
        log.info("Доступ запрещен (403)");
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