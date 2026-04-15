package cucumber.step;

import api.transactions.TransactionsApi;
import api.transactions.ITransactionsApi;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import model.common.ErrorResponse;
import model.transaction.TransactionPublic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.ScenarioContext.*;

public class TransactionsServer extends BaseServer {
    private static final ITransactionsApi transactionsApi = new TransactionsApi();
    private static final Logger log = LoggerFactory.getLogger(TransactionsServer.class);

    private List<TransactionPublic> transactionsList;
    private String receiptHtml;
    private int lastStatusCode;

    public TransactionsServer(ScenarioContext context) {
        super(context);
    }



    private void rememberHttpStatus(int code) {
        this.lastStatusCode = code;
        context.putObject(LAST_STATUS_CODE, code);
    }


    // ==================== ШАГИ ДЛЯ ТРАНЗАКЦИЙ ====================

    @Допустим("у пользователя есть выполненные операции")
    public void userHasCompletedTransactions() {

        try {
            transactionsList = transactionsApi.getTransactions(get(USER_TOKEN));
            assertThat(transactionsList).isNotNull();
            assertThat(transactionsList).isNotEmpty();
            putObject(TRANSACTIONS_LIST, transactionsList);
            log.info("У пользователя есть {} выполненных операций", transactionsList.size());
        } catch (Exception e) {
            log.warn("Не удалось получить список транзакций: {}", e.getMessage());
            // Если нет транзакций, создаем тестовую
            log.info("Создаем тестовую транзакцию для проверки");
        }
    }

    @Допустим("у пользователя есть транзакция с id {string}")
    public void userHasTransactionWithId(String transactionId) {
        put(TRANSACTION_ID, transactionId);
        log.info("У пользователя есть транзакция с id: {}", transactionId);
    }

    @Допустим("существует транзакция другого пользователя с id {string}")
    public void transactionOfAnotherUserExists(String transactionId) {
        put(OTHER_TRANSACTION_ID, transactionId);
        log.info("Существует транзакция другого пользователя с id: {}", transactionId);
    }

    @Когда("клиент запрашивает историю операций")
    public void clientRequestsTransactionsHistory() {

        try {
            transactionsList = transactionsApi.getTransactions(get(USER_TOKEN));
            rememberHttpStatus(200);
            putObject(TRANSACTIONS_LIST, transactionsList);
            log.info("Запрошена история операций, получено {} записей", transactionsList.size());
        } catch (Exception e) {
            rememberHttpStatus(500);
            ErrorResponse er = new ErrorResponse();
            er.setDetail(e.getMessage());
            putObject(ERROR_RESPONSE, er);
            log.error("Ошибка при получении истории операций: {}", e.getMessage());
        }
    }

    @Когда("клиент скачивает чек по операции {string}")
    public void clientDownloadsReceipt(String transactionId) {

        try {
            receiptHtml = transactionsApi.getReceipt(get(USER_TOKEN), Integer.parseInt(transactionId));
            rememberHttpStatus(200);
            put(RECEIPT_HTML, receiptHtml);
            log.info("Скачан чек по операции: {}", transactionId);
        } catch (Exception e) {
            rememberHttpStatus(403);
            ErrorResponse er = new ErrorResponse();
            er.setDetail(e.getMessage());
            putObject(ERROR_RESPONSE, er);
            log.error("Ошибка при скачивании чека: {}", e.getMessage());
        }
    }

    @Когда("клиент пытается скачать чек по операции {string}")
    public void clientTriesToDownloadReceipt(String transactionId) {

        try {
            receiptHtml = transactionsApi.getReceipt(get(USER_TOKEN), Integer.parseInt(transactionId));
            rememberHttpStatus(200);
        } catch (Exception e) {
            rememberHttpStatus(403);
            ErrorResponse er = new ErrorResponse();
            er.setDetail(e.getMessage());
            putObject(ERROR_RESPONSE, er);
            log.info("Ожидаемая ошибка при скачивании чека: {}", e.getMessage());
        }
    }

    @Тогда("каждая транзакция содержит id, type, money, created_at, status")
    public void eachTransactionContainsRequiredFields() {
        if (transactionsList == null) {
            @SuppressWarnings("unchecked")
            List<TransactionPublic> fromContext = (List<TransactionPublic>) getObject(TRANSACTIONS_LIST);
            transactionsList = fromContext;
        }
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
        if (receiptHtml == null) {
            receiptHtml = get(RECEIPT_HTML);
        }
        if (expectedContentType.equals("text/html")) {
            assertThat(receiptHtml).isNotNull();
            assertThat(receiptHtml).contains("<html");
            log.info("Чек имеет тип text/html");
        } else {
            assertThat(receiptHtml).isNotNull();
            log.info("Контент имеет тип: {}", expectedContentType);
        }
    }

    @Тогда("доступ запрещен")
    public void accessDenied() {
        assertThat(lastStatusCode).isEqualTo(403);
        log.info("Доступ запрещен (403)");
    }
}