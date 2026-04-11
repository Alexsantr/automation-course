package cucumber.step;

import io.cucumber.java.ru.Тогда;
import model.user.UserPublic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ScenarioContext;

import static org.assertj.core.api.Assertions.assertThat;

public class UserServer {
    private static final Logger log = LoggerFactory.getLogger(UserServer.class);

    private final ScenarioContext context;
    private UserPublic userResponse;

    public UserServer(ScenarioContext context) {
        this.context = context;
    }

    private Object getObject(String key) {
        return context.getObject(key);
    }

    @Тогда("в ответе получаю фамилию {string}")
    public void receiveLastName(String expectedLastName) {
        userResponse = (UserPublic) getObject(ScenarioContext.USER_RESPONSE);
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getLast_name()).isEqualTo(expectedLastName);
        log.info("Фамилия пользователя: {}", userResponse.getLast_name());
    }

    @Тогда("в ответе получаю email {string}")
    public void receiveEmail(String expectedEmail) {
        userResponse = (UserPublic) getObject(ScenarioContext.USER_RESPONSE);
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getEmail()).isEqualTo(expectedEmail);
        log.info("Email пользователя: {}", userResponse.getEmail());
    }

    @Тогда("статус пользователя {string}")
    public void userStatusIs(String expectedStatus) {
        userResponse = (UserPublic) getObject(ScenarioContext.USER_RESPONSE);
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getStatus()).isEqualTo(expectedStatus);
        log.info("Статус пользователя: {}", expectedStatus);
    }

    @Тогда("роль пользователя {string}")
    public void userRoleIs(String expectedRole) {
        userResponse = (UserPublic) getObject(ScenarioContext.USER_RESPONSE);
        assertThat(userResponse).isNotNull();
        assertThat(userResponse.getRole()).isEqualTo(expectedRole);
        log.info("Роль пользователя: {}", expectedRole);
    }
}
