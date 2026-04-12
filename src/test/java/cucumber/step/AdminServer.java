package cucumber.step;

import api.admin.AdminApi;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import model.transaction.TransactionPublic;
import model.user.UserPublic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.ScenarioContext.*;

public class AdminServer extends BaseServer {
    private static final AdminApi adminApi = new AdminApi();
    private static final Logger log = LoggerFactory.getLogger(AdminServer.class);


    private List<UserPublic> usersList;
    private UserPublic updatedUser;
    private String actionResponse;

    public AdminServer(ScenarioContext context) {
        super(context);
    }
    
    // ==================== ШАГИ ДЛЯ АДМИНИСТРИРОВАНИЯ ====================
    // «администратор авторизован в системе» — шаг из AuthServer (реальная авторизация)

    @Допустим("существует пользователь с id {string}")
    public void userExistsWithId(String userId) {
        put(USER_ID, userId);
        log.info("Существует пользователь с id: {}", userId);
    }

    @Допустим("существует пользователь с id {string} и статусом {string}")
    public void userExistsWithIdAndStatus(String userId, String status) {
        put(USER_ID, userId);
        put(USER_STATUS, status);
        log.info("Существует пользователь: id={}, status={}", userId, status);
    }

    @Допустим("пользователь с логином {string} уже зарегистрирован")
    public void userAlreadyRegistered(String login) {
        put(USER_LOGIN, login);
        log.info("Пользователь с логином {} уже зарегистрирован", login);
    }

    @Когда("администратор запрашивает список всех пользователей")
    public void adminRequestsAllUsers() {
        String adminToken = get(ADMIN_TOKEN);
        usersList = adminApi.getAllUsers(adminToken);
        putObject(USERS_LIST, usersList);
        log.info("Получен список пользователей, количество: {}", usersList.size());
    }

    @Когда("администратор блокирует пользователя с id {string}")
    public void adminBlocksUser(String userId) {
        String adminToken = get(ADMIN_TOKEN);
        updatedUser = adminApi.blockUser(adminToken, Integer.parseInt(userId));
        putObject(UPDATED_USER, updatedUser);
        log.info("Пользователь {} заблокирован", userId);
    }

    @Когда("администратор разблокирует пользователя с id {string}")
    public void adminUnblocksUser(String userId) {
        String adminToken = get(ADMIN_TOKEN);
        updatedUser = adminApi.unblockUser(adminToken, Integer.parseInt(userId));
        putObject(UPDATED_USER, updatedUser);
        log.info("Пользователь {} разблокирован", userId);
    }

    @Когда("администратор удаляет пользователя с id {string}")
    public void adminDeletesUser(String userId) {
        String adminToken = get(ADMIN_TOKEN);
        actionResponse = adminApi.deleteUser(adminToken, Integer.parseInt(userId));
        log.info("Пользователь {} удален", userId);
    }

    @Когда("администратор запрашивает транзакции пользователя")
    public void adminRequestsUserTransactions() {
        String adminToken = get(ADMIN_TOKEN);
        String userId = get(USER_ID);
        List<TransactionPublic> userTransactions = adminApi.getUserTransactions(adminToken, Integer.parseInt(userId));
        putObject(USER_TRANSACTIONS, userTransactions);
        log.info("Получены транзакции пользователя {}", userId);
    }

    @Когда("администратор восстанавливает базу данных до начального состояния")
    public void adminRestoresDatabase() {
        String adminToken = get(ADMIN_TOKEN);
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

    @Тогда("остался только один пользователь - администратор")
    public void onlyAdminUserRemains() {
        String adminToken = get(ADMIN_TOKEN);
        usersList = adminApi.getAllUsers(adminToken);

        assertThat(usersList).isNotNull();
        assertThat(usersList).hasSize(1);

        UserPublic onlyUser = usersList.getFirst();
        assertThat(onlyUser.getRole()).isEqualTo("ADMIN");

        log.info("Остался только один пользователь - администратор");
    }

    @Тогда("у администратора логин {string}")
    public void adminHasLogin(String expectedLogin) {
        String adminToken = get(ADMIN_TOKEN);
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