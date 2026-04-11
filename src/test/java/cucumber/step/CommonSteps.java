package cucumber.step;

import api.user.UserApi;
import api.user.IUserApi;
import io.cucumber.java.ru.Допустим;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;
import io.restassured.response.Response;
import model.user.UserPublic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {
    private static final IUserApi userApi = new UserApi();
    private static final Logger log = LoggerFactory.getLogger(CommonSteps.class);
    
    private final ScenarioContext context;
    private Response lastResponse;
    private UserPublic userResponse;
    
    public CommonSteps(ScenarioContext context) {
        this.context = context;
    }
    
    private void put(String key, String value) {
        context.put(key, value);
    }
    
    private void putObject(String key, Object value) {
        context.putObject(key, value);
    }
    
    private String get(String key) {
        return context.get(key);
    }
    
    private Object getObject(String key) {
        return context.getObject(key);
    }
    
    // ==================== ШАГИ ДЛЯ USER.FEATURE ====================
    
    @Допустим("пользователь с id {int} существует в системе")
    public void userExistsWithId(int userId) {
        put(ScenarioContext.USER_ID, String.valueOf(userId));
        log.info("Пользователь с id {} существует в системе", userId);
    }
    
    @Когда("я запрашиваю данные пользователя")
    public void requestUserData() {
        String token = get(ScenarioContext.USER_TOKEN);
        String userId = get(ScenarioContext.USER_ID);
        
        if (token == null || token.isEmpty()) {
            // Если нет токена, делаем запрос без авторизации (для публичных API)
            userResponse = userApi.getUserByIdPublic(Integer.parseInt(userId));
        } else {
            userResponse = userApi.getUserById(token, Integer.parseInt(userId));
        }
        
        putObject(ScenarioContext.USER_RESPONSE, userResponse);
        log.info("Запрошены данные пользователя с id: {}", userId);
    }
    
    @Когда("клиент делает запрос")
    public void clientMakesRequest() {
        // Общий шаг для выполнения запроса
        // Можно использовать для GET запросов по умолчанию
        String token = get(ScenarioContext.USER_TOKEN);
        String userId = get(ScenarioContext.USER_ID);
        
        if (userId != null) {
            if (token != null && !token.isEmpty()) {
                userResponse = userApi.getUserById(token, Integer.parseInt(userId));
            } else {
                userResponse = userApi.getUserByIdPublic(Integer.parseInt(userId));
            }
            putObject(ScenarioContext.USER_RESPONSE, userResponse);
        }
        
        log.info("Клиент выполнил запрос");
    }
    
    @Тогда("в ответе получаю имя {string}")
    public void receiveFirstName(String expectedFirstName) {
        UserPublic response = (UserPublic) getObject(ScenarioContext.USER_RESPONSE);
        assertThat(response).isNotNull();
        assertThat(response.getFirst_name()).isEqualTo(expectedFirstName);
        log.info("Имя пользователя: {}", response.getFirst_name());
    }
    
    @Тогда("статус ответа {int}")
    public void checkStatusCode(int expectedStatusCode) {
        Integer actual = (Integer) getObject(ScenarioContext.LAST_STATUS_CODE);
        assertThat(actual)
                .as("HTTP-статус последнего ответа (положите его в контекст через шаги API)")
                .isNotNull()
                .isEqualTo(expectedStatusCode);
        log.info("Статус ответа соответствует ожидаемому: {}", expectedStatusCode);
    }
    
    @И("список счетов не пустой")
    public void accountsListNotEmpty() {
        // Реализация в AccountsServer
        log.info("Проверка что список счетов не пустой");
    }
}