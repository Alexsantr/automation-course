package cucumber.step;

import api.admin.AdminApi;
import api.user.UserApi;
import api.user.IUserApi;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Затем;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import model.transaction.TransactionPublic;
import model.user.UserPublic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AdminServer {
    private static final AdminApi adminApi = new AdminApi();
    private static final UserApi userApi = new UserApi();
    private static final Logger log = LoggerFactory.getLogger(AdminServer.class);

    private final ScenarioContext context;
    private List<UserPublic> usersList;
    private UserPublic updatedUser;
    private List<TransactionPublic> userTransactions;
    private String actionResponse;

    public AdminServer(ScenarioContext context) {
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

    private String get(String key) {
        return context.get(key);
    }

    // ==================== ШАГИ ДЛЯ АДМИНИСТРИРОВАНИЯ ====================

    @Допустим("администратор авторизован в системе")
    public void adminIsAuthorized() {
        String adminLogin = "admin";
        String adminPassword = "admin";

        // Здесь должна быть авторизация администратора
        // AuthResponse authResponse = authApi.getAuthUser(new AuthRequest(adminLogin, adminPassword));
        // put(ScenarioContext.ADMIN_TOKEN, authResponse.getAccess_token());

        log.info("Администратор авторизован: login={}", adminLogin);
    }

    @Допустим("существует пользователь с id {string}")
    public void userExistsWithId(String userId) {
        put(ScenarioContext.USER_ID, userId);
        log.info("Существует пользователь с id: {}", userId);
    }

    @Допустим("существует пользователь с id {string} и статусом {string}")
    public void userExistsWithIdAndStatus(String userId, String status) {
        put(ScenarioContext.USER_ID, userId);
        put(ScenarioContext.USER_STATUS, status);
        log.info("Существует пользователь: id={}, status={}", userId, status);
    }

    @Допустим("пользователь с логином {string} уже зарегистрирован")
    public void userAlreadyRegistered(String login) {
        put(ScenarioContext.USER_LOGIN, login);
        log.info("Пользователь с логином {} уже зарегистрирован", login);
    }

    @Когда("администратор запрашивает список всех пользователей")
    public void adminRequestsAllUsers() {
        String adminToken = get(ScenarioContext.ADMIN_TOKEN);
        usersList = adminApi.getAllUsers(adminToken);
        putObject(ScenarioContext.USERS_LIST, usersList);
        log.info("Получен список пользователей, количество: {}", usersList.size());
    }

    @Когда("администратор блокирует пользователя с id {string}")
    public void adminBlocksUser(String userId) {
        String adminToken = get(ScenarioContext.ADMIN_TOKEN);
        updatedUser = adminApi.blockUser(adminToken, Integer.parseInt(userId));
        putObject(ScenarioContext.UPDATED_USER, updatedUser);
        log.info("Пользователь {} заблокирован", userId);
    }

    @Когда("администратор разблокирует пользователя с id {string}")
    public void adminUnblocksUser(String userId) {
        String adminToken = get(ScenarioContext.ADMIN_TOKEN);
        updatedUser = adminApi.unblockUser(adminToken, Integer.parseInt(userId));
        putObject(ScenarioContext.UPDATED_USER, updatedUser);
        log.info("Пользователь {} разблокирован", userId);
    }

    @Когда("администратор удаляет пользователя с id {string}")
    public void adminDeletesUser(String userId) {
        String adminToken = get(ScenarioContext.ADMIN_TOKEN);
        actionResponse = adminApi.deleteUser(adminToken, Integer.parseInt(userId));
        log.info("Пользователь {} удален", userId);
    }

    @Когда("администратор запрашивает транзакции пользователя")
    public void adminRequestsUserTransactions() {
        String adminToken = get(ScenarioContext.ADMIN_TOKEN);
        String userId = get(ScenarioContext.USER_ID);
        userTransactions = adminApi.getUserTransactions(adminToken, Integer.parseInt(userId));
        putObject(ScenarioContext.USER_TRANSACTIONS, userTransactions);
        log.info("Получены транзакции пользователя {}", userId);
    }

    @Когда("администратор восстанавливает базу данных до начального состояния")
    public void adminRestoresDatabase() {
        String adminToken = get(ScenarioContext.ADMIN_TOKEN);
        actionResponse = adminApi.restoreInitialState(adminToken);
        log.info("База данных восстановлена до начального состояния");
    }

    @Тогда("список пользователей не пустой")
    public void usersListNotEmpty() {
        assertThat(usersList).isNotNull();
        assertThat(usersList).isNotEmpty();
        log.info("Список пользователей не пустой, размер: {}", usersList.size());
    }

    @Тогда("каждый пользователь содержит id, login, role, status")
    public void eachUserContainsRequiredFields() {
        for (UserPublic user : usersList) {
            assertThat(user.getId()).isNotZero();
            assertThat(user.getLogin()).isNotBlank();
            assertThat(user.getRole()).isNotNull();
            assertThat(user.getStatus()).isNotNull();
        }
        log.info("Все пользователи содержат обязательные поля");
    }

    @Тогда("статус пользователя изменился на {string}")
    public void userStatusChangedTo(String expectedStatus) {
        assertThat(updatedUser.getStatus()).isEqualTo(expectedStatus);
        log.info("Статус пользователя изменен на: {}", expectedStatus);
    }

    @Тогда("список транзакций не пустой")
    public void transactionsListNotEmpty() {
        assertThat(userTransactions).isNotNull();
        assertThat(userTransactions).isNotEmpty();
        log.info("Список транзакций не пустой");
    }

    @Тогда("остался только один пользователь - администратор")
    public void onlyAdminUserRemains() {
        String adminToken = get(ScenarioContext.ADMIN_TOKEN);
        usersList = adminApi.getAllUsers(adminToken);

        assertThat(usersList).isNotNull();
        assertThat(usersList).hasSize(1);

        UserPublic onlyUser = usersList.get(0);
        assertThat(onlyUser.getRole()).isEqualTo("ADMIN");

        log.info("Остался только один пользователь - администратор");
    }

    @Тогда("у администратора логин {string}")
    public void adminHasLogin(String expectedLogin) {
        String adminToken = get(ScenarioContext.ADMIN_TOKEN);
        usersList = adminApi.getAllUsers(adminToken);

        assertThat(usersList).isNotNull();
        assertThat(usersList).isNotEmpty();

        // Находим администратора в списке
        UserPublic admin = usersList.stream()
                .filter(user -> "ADMIN".equals(user.getRole()))
                .findFirst()
                .orElse(null);

        assertThat(admin).isNotNull();
        assertThat(admin.getLogin()).isEqualTo(expectedLogin);
        log.info("Логин администратора: {}", expectedLogin);
    }

    @Тогда("пользователь удален")
    public void userDeleted() {
        assertThat(actionResponse).contains("deleted");
        log.info("Пользователь успешно удален");
    }

    @Тогда("база данных восстановлена")
    public void databaseRestored() {
        assertThat(actionResponse).contains("ok");
        log.info("База данных восстановлена");
    }
}