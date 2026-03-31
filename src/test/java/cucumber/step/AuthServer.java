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
}