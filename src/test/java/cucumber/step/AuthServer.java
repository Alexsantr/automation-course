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
import utils.ScenarioContext;
import utils.UserDataGenerator;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthServer {
    private static final IAuthApi authUsr = new AuthApi();
    private static final Logger log = LoggerFactory.getLogger(AuthServer.class);

    private final ScenarioContext context;
    private RegisterRequest.RegisterRequestBuilder requestBuilder;
    private RegisterResponse registerResponse;

    public AuthServer(ScenarioContext context) {
        this.context = context;
    }

    private void put(String key, String value) {
        context.put(key, value);
    }

    private String get(String key) {
        return context.get(key);
    }

    // ==================== НОВЫЕ ШАГИ ====================

    @Допустим("пользователь авторизован в системе")
    public void userIsAuthorized() {
        String token = get(ScenarioContext.USER_TOKEN);
        if (token != null && !token.isEmpty()) {
            log.info("Пользователь уже авторизован, токен: {}", token);
            return;
        }

        String login = get(ScenarioContext.USER_LOGIN);
        String password = get(ScenarioContext.USER_PASSWORD);

        if (login == null || password == null) {
            // Создаем нового пользователя
            String newLogin = UserDataGenerator.generateLogin();
            String newPassword = UserDataGenerator.generatePassword();

            RegisterRequest registerRequest = RegisterRequest.builder()
                    .login(newLogin)
                    .password(newPassword)
                    .build();

            registerResponse = authUsr.authUser(registerRequest);

            AuthResponse authResponse = authUsr.getAuthUser(new AuthRequest(newLogin, newPassword));

            put(ScenarioContext.USER_LOGIN, newLogin);
            put(ScenarioContext.USER_PASSWORD, newPassword);
            put(ScenarioContext.USER_TOKEN, authResponse.getAccess_token());

            log.info("Создан и авторизован новый пользователь: login={}", newLogin);
        } else {
            AuthResponse authResponse = authUsr.getAuthUser(new AuthRequest(login, password));
            put(ScenarioContext.USER_TOKEN, authResponse.getAccess_token());
            log.info("Пользователь авторизован: login={}", login);
        }
    }

    @Допустим("администратор авторизован в системе")
    public void adminIsAuthorized() {
        String adminLogin = "admin";
        String adminPassword = "admin";

        AuthResponse authResponse = authUsr.getAuthUser(new AuthRequest(adminLogin, adminPassword));
        put(ScenarioContext.ADMIN_TOKEN, authResponse.getAccess_token());
        put(ScenarioContext.USER_TOKEN, authResponse.getAccess_token());

        log.info("Администратор авторизован: login={}", adminLogin);
    }

    // ==================== СУЩЕСТВУЮЩИЕ ШАГИ ====================

    @Допустим("я создаю пользователя с логином {string} и паролем {string}")
    public void createNewUser(String login, String password) {
        String finalLogin = UserDataGenerator.resolve(login);
        String finalPassword = UserDataGenerator.resolve(password);

        requestBuilder = RegisterRequest.builder()
                .login(finalLogin)
                .password(finalPassword);
        log.info("Установлены учетные данные: login={}, password={}", finalLogin, finalPassword);
    }

    @Когда("клиент регистрируется на сайте шляпабанк")
    public void registerUser() {
        RegisterRequest request = requestBuilder.build();
        registerResponse = authUsr.authUser(request);
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
        RegisterRequest request = requestBuilder.build();
        put(ScenarioContext.USER_LOGIN, request.getLogin());
        put(ScenarioContext.USER_PASSWORD, request.getPassword());
        log.info("Сохранены данные: login={}, password={}",
                get(ScenarioContext.USER_LOGIN),
                get(ScenarioContext.USER_PASSWORD));
    }

    @Затем("клиент авторизуется")
    public void authUser() {
        String login = get(ScenarioContext.USER_LOGIN);
        String password = get(ScenarioContext.USER_PASSWORD);

        AuthResponse authResponse = authUsr.getAuthUser(new AuthRequest(login, password));

        put(ScenarioContext.USER_TOKEN, authResponse.getAccess_token());
        log.info("Токен получен и сохранён: {}", authResponse.getAccess_token());
    }

    @Тогда("клиент авторизован")
    public void checkAuthUser() {
        String token = get(ScenarioContext.USER_TOKEN);

        assertThat(token)
                .isNotNull()
                .isNotBlank();
        log.info("Проверка пройдена, токен валиден: {}", token);
    }

    @Тогда("токен доступа не должен быть пустым")
    public void tokenShouldNotBeEmpty() {
        String token = get(ScenarioContext.USER_TOKEN);
        assertThat(token).isNotNull().isNotBlank();
        log.info("Токен не пустой");
    }
}