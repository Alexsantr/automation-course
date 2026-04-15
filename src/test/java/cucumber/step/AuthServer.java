package cucumber.step;

import api.auth.AuthApi;
import api.auth.IAuthApi;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.Затем;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import model.auth.AuthRequest;
import model.auth.AuthResponse;
import model.auth.RegisterRequest;
import model.auth.RegisterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DatabaseHelper;
import utils.ScenarioContext;
import utils.UserDataGenerator;


import static org.assertj.core.api.Assertions.assertThat;
import static utils.ScenarioContext.*;

public class AuthServer extends BaseServer {
    private static final IAuthApi authUsr = new AuthApi();
    private static final Logger log = LoggerFactory.getLogger(AuthServer.class);


    private RegisterRequest.RegisterRequestBuilder requestBuilder;
    private RegisterResponse registerResponse;

    public AuthServer(ScenarioContext context) {
        super(context);
    }

    /**
     * Один экземпляр на сценарий в {@link ScenarioContext}; закрывается в {@link Hooks#tearDown()}.
     */
    private DatabaseHelper databaseHelper() {
        if (context.getObject(DB_HELPER) instanceof DatabaseHelper) {
            return (DatabaseHelper) context.getObject(DB_HELPER);
        }
        context.putObject(DB_HELPER, new DatabaseHelper());
        return new DatabaseHelper();
    }


    // ==================== НОВЫЕ ШАГИ ДЛЯ РАБОТЫ С БД ====================

    @Допустим("я беру случайного пользователя из базы данных")
    public void takeRandomUserFromDatabase() {
        DatabaseHelper.UserCredentials user = databaseHelper().getRandomUser();
        assertThat(user).isNotNull();

        put(USER_LOGIN, user.getLogin());
        put(USER_PASSWORD, user.getPassword());

        log.info("Взят случайный пользователь из БД: login={}", user.getLogin());
    }

    @Допустим("я беру пользователя с логином {string} из базы данных")
    public void takeUserByLoginFromDatabase(String login) {
        assertThat(databaseHelper().getUserByLogin(login)).isNotNull();

        put(USER_LOGIN, databaseHelper().getUserByLogin(login).getLogin());
        put(USER_PASSWORD, databaseHelper().getUserByLogin(login).getPassword());

        log.info("Взят пользователь из БД: login={}", databaseHelper().getUserByLogin(login).getLogin());
    }

    @Допустим("я беру пользователя с id {int} из базы данных")
    public void takeUserByIdFromDatabase(int userId) {
        assertThat(databaseHelper().getUserById(userId)).isNotNull();

        put(USER_LOGIN, databaseHelper().getUserById(userId).getLogin());
        put(USER_PASSWORD, databaseHelper().getUserById(userId).getPassword());

        log.info("Взят пользователь из БД: id={}, login={}", userId, databaseHelper().getUserById(userId).getLogin());
    }

    @Допустим("пользователь с логином {string} существует в базе данных")
    public void userExistsInDatabase(String login) {
        assertThat(databaseHelper().getUserByLogin(login)).isNotNull();
        assertThat(databaseHelper().getUserByLogin(login).getLogin()).isEqualTo(login);
        log.info("Пользователь с логином {} существует в БД", login);
    }

    @Допустим("я авторизуюсь под пользователем из базы данных")
    public void authorizeWithUserFromDatabase() {

        assertThat(get(USER_LOGIN)).isNotNull();
        assertThat(get(USER_PASSWORD)).isNotNull();

        AuthResponse authResponse = authUsr.getAuthUser(new AuthRequest(get(USER_LOGIN), get(USER_PASSWORD)));
        put(USER_TOKEN, authResponse.getAccess_token());

        log.info("Авторизован под пользователем из БД: login={}", get(USER_LOGIN));
    }

    // ==================== СУЩЕСТВУЮЩИЕ ШАГИ ====================

    @Допустим("я создаю пользователя с логином {string} и паролем {string}")
    public void createNewUser(String login, String password) {

        requestBuilder = RegisterRequest.builder()
                .login(UserDataGenerator.resolve(login))
                .password(UserDataGenerator.resolve(password));
        log.info("Установлены учетные данные: login={}, password={}", UserDataGenerator.resolve(login), UserDataGenerator.resolve(password));
    }

    @Когда("клиент регистрируется на сайте шляпабанк")
    public void registerUser() {
        registerResponse = authUsr.authUser(requestBuilder.build());
        log.info("Регистрация выполнена. Получен ответ: {}", registerResponse);
    }

    @Тогда("регистрация должна быть успешной")
    public void checkRegistration() {
        assertThat(registerResponse).isNotNull();
        assertThat(registerResponse.getId()).isNotZero();
        assertThat(registerResponse.getStatus()).isEqualTo("ACTIVE");
        log.info("Регистрация успешна, id: {}", registerResponse.getId());
    }

    @Затем("запоминаем данные по клиенту")
    public void saveDataUser() {
        put(USER_LOGIN, requestBuilder.build().getLogin());
        put(USER_PASSWORD, requestBuilder.build().getPassword());
        log.info("Сохранены данные: login={}, password={}",
                get(USER_LOGIN),
                get(USER_PASSWORD));
    }

    @Затем("клиент авторизуется")
    public void authUser() {

        AuthResponse authResponse = authUsr.getAuthUser(new AuthRequest(get(USER_LOGIN), get(USER_PASSWORD)));

        put(USER_TOKEN, authResponse.getAccess_token());
        log.info("Токен получен и сохранён: {}", authResponse.getAccess_token());
    }

    @Тогда("клиент авторизован")
    public void checkAuthUser() {

        assertThat(get(USER_TOKEN))
                .isNotNull()
                .isNotBlank();
        log.info("Проверка пройдена, токен валиден: {}", get(USER_TOKEN));
    }

    @Допустим("пользователь авторизован в системе")
    public void userIsAuthorized() {
        if (get(USER_TOKEN) != null && !get(USER_TOKEN).isEmpty()) {
            log.info("Пользователь уже авторизован");
            return;
        }

        String login = get(USER_LOGIN);
        String password = get(USER_PASSWORD);

        if (login == null || password == null) {
            // Если нет данных, берем случайного пользователя из БД
            DatabaseHelper.UserCredentials user = databaseHelper().getRandomUser();
            if (user != null) {
                login = user.getLogin();
                password = user.getPassword();
                put(USER_LOGIN, login);
                put(USER_PASSWORD, password);
            } else {
                throw new IllegalStateException("Нет доступных пользователей в БД");
            }
        }

        AuthResponse authResponse = authUsr.getAuthUser(new AuthRequest(login, password));
        put(USER_TOKEN, authResponse.getAccess_token());
        log.info("Пользователь авторизован: login={}", login);
    }
}