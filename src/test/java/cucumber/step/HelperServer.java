package cucumber.step;

import api.helper.HelperApi;
import api.helper.IHelperApi;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import model.account.AccountPublic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.ScenarioContext.*;

public class HelperServer extends BaseServer {
    private static final IHelperApi helperApi = new HelperApi();
    private static final Logger log = LoggerFactory.getLogger(HelperServer.class);

    private String otpCode;
    private AccountPublic updatedAccount;
    private String clearCacheInstructions;

    public HelperServer(ScenarioContext context) {
        super(context);
    }


    // ==================== OTP ====================

    @Когда("клиент запрашивает OTP код")
    public void requestOtpCode() {
        otpCode = helperApi.getOtpCode(get(USER_TOKEN));
        put(OTP_CODE, otpCode);
        log.info("OTP код получен: {}", otpCode);
    }

    @Тогда("OTP код получен")
    public void otpCodeReceived() {
        assertThat(otpCode).isNotNull();
        log.info("OTP код успешно получен");
    }

    @Тогда("код состоит из 4 цифр")
    public void otpCodeHas4Digits() {
        assertThat(otpCode).matches("\\d{4}");
        log.info("OTP код состоит из 4 цифр: {}", otpCode);
    }

    // ==================== Увеличение баланса ====================

    @Когда("клиент увеличивает баланс счета на сумму {string} через helper")
    public void increaseBalanceViaHelper(String amount) {

        // Сохраняем баланс до увеличения

        updatedAccount = helperApi.increaseBalance(get(USER_TOKEN), Integer.parseInt(get(ACCOUNT_ID)), amount, null);

        put(ACCOUNT_BALANCE, updatedAccount.getBalance());
        log.info("Баланс счета {} увеличен на {}: {} -> {}",
                get(ACCOUNT_ID), amount, get(ACCOUNT_BALANCE), updatedAccount.getBalance());
    }

    @Когда("клиент увеличивает баланс счета на сумму {string} с назначением {string}")
    public void increaseBalanceWithPurpose(String amount, String purpose) {

        updatedAccount = helperApi.increaseBalance(get(USER_TOKEN), Integer.parseInt(get(ACCOUNT_ID)), amount, purpose);

        put(ACCOUNT_BALANCE, updatedAccount.getBalance());
        log.info("Баланс счета {} увеличен на {} с назначением {}: {} -> {}",
                get(ACCOUNT_ID), amount, purpose, get(ACCOUNT_BALANCE), updatedAccount.getBalance());
    }

    // ==================== Уменьшение баланса ====================

    @Когда("клиент уменьшает баланс счета на сумму {string} через helper")
    public void decreaseBalanceViaHelper(String amount) {

        updatedAccount = helperApi.decreaseBalance(get(USER_TOKEN), Integer.parseInt(get(ACCOUNT_ID)), amount);

        put(ACCOUNT_BALANCE, updatedAccount.getBalance());
        log.info("Баланс счета {} уменьшен на {}: {} -> {}",
                get(ACCOUNT_ID), amount, get(ACCOUNT_BALANCE), updatedAccount.getBalance());
    }

    // ==================== Обнуление баланса ====================

    @Когда("клиент обнуляет баланс счета через helper")
    public void zeroBalanceViaHelper() {

        updatedAccount = helperApi.zeroBalance(get(USER_TOKEN), Integer.parseInt(get(ACCOUNT_ID)));

        put(ACCOUNT_BALANCE, updatedAccount.getBalance());
        log.info("Баланс счета {} обнулен: {} -> {}",
                get(ACCOUNT_ID), get(ACCOUNT_BALANCE), updatedAccount.getBalance());
    }

    // ==================== Очистка кеша ====================

    @Когда("клиент запрашивает очистку кеша браузера")
    public void requestClearBrowserCache() {
        clearCacheInstructions = helperApi.clearBrowserCache(get(USER_TOKEN));
        put(CLEAR_CACHE_INSTRUCTIONS, clearCacheInstructions);
        log.info("Запрошена очистка кеша браузера");
    }

    @Тогда("инструкция по очистке получена")
    public void clearCacheInstructionsReceived() {
        assertThat(clearCacheInstructions).isNotNull();
        assertThat(clearCacheInstructions).isNotBlank();
        log.info("Инструкция по очистке получена: {}", clearCacheInstructions);
    }

    // ==================== Получение всех счетов через helper ====================

    @Когда("клиент запрашивает список счетов через helper")
    public void requestHelperAccounts() {
        List<AccountPublic> accounts = helperApi.getHelperAccounts(get(USER_TOKEN));

        assertThat(accounts).isNotNull();
        assertThat(accounts).isNotEmpty();

        // Сохраняем первый счет
        AccountPublic firstAccount = accounts.getFirst();
        put(ACCOUNT_ID, String.valueOf(firstAccount.getId()));
        put(ACCOUNT_BALANCE, firstAccount.getBalance());

        log.info("Через helper получено {} счетов. Взят первый счет: id={}, balance={}",
                accounts.size(), firstAccount.getId(), firstAccount.getBalance());
    }

    // ==================== Проверки ====================

    @Тогда("баланс счета увеличился на {string}")
    public void accountBalanceIncreasedBy(String expectedIncrease) {

        BigDecimal before = new BigDecimal(get(ACCOUNT_BALANCE));
        BigDecimal after = new BigDecimal(updatedAccount.getBalance());
        BigDecimal increase = new BigDecimal(expectedIncrease);

        assertThat(after).isEqualTo(before.add(increase));
        log.info("Баланс увеличился на {}: {} -> {}", expectedIncrease, get(ACCOUNT_BALANCE), updatedAccount.getBalance());
    }

    @Тогда("баланс счета уменьшился на {string}")
    public void accountBalanceDecreasedBy(String expectedDecrease) {

        BigDecimal before = new BigDecimal(get(ACCOUNT_BALANCE));
        BigDecimal after = new BigDecimal(updatedAccount.getBalance());
        BigDecimal decrease = new BigDecimal(expectedDecrease);

        assertThat(after).isEqualTo(before.subtract(decrease));
        log.info("Баланс уменьшился на {}: {} -> {}", expectedDecrease, get(ACCOUNT_BALANCE), updatedAccount.getBalance());
    }

    @Тогда("баланс счета стал равен {string}")
    public void accountBalanceEquals(String expectedBalance) {
        assertThat(updatedAccount.getBalance()).isEqualTo(expectedBalance);
        log.info("Баланс счета стал равен: {}", expectedBalance);
    }

    @Тогда("баланс счета стал равен нулю")
    public void accountBalanceEqualsZero() {
        assertThat(new BigDecimal(updatedAccount.getBalance())).isZero();
        log.info("Баланс счета стал равен нулю");
    }
}