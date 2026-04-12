package cucumber.step;

import api.helper.HelperApi;
import api.helper.IHelperApi;
import io.cucumber.java.ru.Допустим;
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
        String token = get(USER_TOKEN);
        otpCode = helperApi.getOtpCode(token);
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
        String token = get(USER_TOKEN);
        String accountId = get(ACCOUNT_ID);

        // Сохраняем баланс до увеличения
        String balanceBefore = get(ACCOUNT_BALANCE);

        updatedAccount = helperApi.increaseBalance(token, Integer.parseInt(accountId), amount, null);

        put(ACCOUNT_BALANCE, updatedAccount.getBalance());
        log.info("Баланс счета {} увеличен на {}: {} -> {}",
                accountId, amount, balanceBefore, updatedAccount.getBalance());
    }

    @Когда("клиент увеличивает баланс счета на сумму {string} с назначением {string}")
    public void increaseBalanceWithPurpose(String amount, String purpose) {
        String token = get(USER_TOKEN);
        String accountId = get(ACCOUNT_ID);

        String balanceBefore = get(ACCOUNT_BALANCE);

        updatedAccount = helperApi.increaseBalance(token, Integer.parseInt(accountId), amount, purpose);

        put(ACCOUNT_BALANCE, updatedAccount.getBalance());
        log.info("Баланс счета {} увеличен на {} с назначением {}: {} -> {}",
                accountId, amount, purpose, balanceBefore, updatedAccount.getBalance());
    }

    // ==================== Уменьшение баланса ====================

    @Когда("клиент уменьшает баланс счета на сумму {string} через helper")
    public void decreaseBalanceViaHelper(String amount) {
        String token = get(USER_TOKEN);
        String accountId = get(ACCOUNT_ID);

        String balanceBefore = get(ACCOUNT_BALANCE);

        updatedAccount = helperApi.decreaseBalance(token, Integer.parseInt(accountId), amount);

        put(ACCOUNT_BALANCE, updatedAccount.getBalance());
        log.info("Баланс счета {} уменьшен на {}: {} -> {}",
                accountId, amount, balanceBefore, updatedAccount.getBalance());
    }

    // ==================== Обнуление баланса ====================

    @Когда("клиент обнуляет баланс счета через helper")
    public void zeroBalanceViaHelper() {
        String token = get(USER_TOKEN);
        String accountId = get(ACCOUNT_ID);

        String balanceBefore = get(ACCOUNT_BALANCE);

        updatedAccount = helperApi.zeroBalance(token, Integer.parseInt(accountId));

        put(ACCOUNT_BALANCE, updatedAccount.getBalance());
        log.info("Баланс счета {} обнулен: {} -> {}",
                accountId, balanceBefore, updatedAccount.getBalance());
    }

    // ==================== Очистка кеша ====================

    @Когда("клиент запрашивает очистку кеша браузера")
    public void requestClearBrowserCache() {
        String token = get(USER_TOKEN);
        clearCacheInstructions = helperApi.clearBrowserCache(token);
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
        String token = get(USER_TOKEN);
        List<AccountPublic> accounts = helperApi.getHelperAccounts(token);

        assertThat(accounts).isNotNull();
        assertThat(accounts).isNotEmpty();

        // Сохраняем первый счет
        AccountPublic firstAccount = accounts.get(0);
        put(ACCOUNT_ID, String.valueOf(firstAccount.getId()));
        put(ACCOUNT_BALANCE, firstAccount.getBalance());

        log.info("Через helper получено {} счетов. Взят первый счет: id={}, balance={}",
                accounts.size(), firstAccount.getId(), firstAccount.getBalance());
    }

    // ==================== Проверки ====================

    @Тогда("баланс счета увеличился на {string}")
    public void accountBalanceIncreasedBy(String expectedIncrease) {
        String balanceBefore = get(ACCOUNT_BALANCE);
        String balanceAfter = updatedAccount.getBalance();

        BigDecimal before = new BigDecimal(balanceBefore);
        BigDecimal after = new BigDecimal(balanceAfter);
        BigDecimal increase = new BigDecimal(expectedIncrease);

        assertThat(after).isEqualTo(before.add(increase));
        log.info("Баланс увеличился на {}: {} -> {}", expectedIncrease, balanceBefore, balanceAfter);
    }

    @Тогда("баланс счета уменьшился на {string}")
    public void accountBalanceDecreasedBy(String expectedDecrease) {
        String balanceBefore = get(ACCOUNT_BALANCE);
        String balanceAfter = updatedAccount.getBalance();

        BigDecimal before = new BigDecimal(balanceBefore);
        BigDecimal after = new BigDecimal(balanceAfter);
        BigDecimal decrease = new BigDecimal(expectedDecrease);

        assertThat(after).isEqualTo(before.subtract(decrease));
        log.info("Баланс уменьшился на {}: {} -> {}", expectedDecrease, balanceBefore, balanceAfter);
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