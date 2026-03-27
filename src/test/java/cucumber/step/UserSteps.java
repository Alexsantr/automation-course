package cucumber.step;

import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Тогда;

public class UserSteps {

    private String receivedName;

    @Дано("пользователь с id {int} существует в системе")
    public void setUserId(int id) {
        System.out.println("Готовим запрос для userId = " + id);
    }

    @Когда("я запрашиваю данные пользователя")
    public void checkUserData() {
        // пока заглушка — реальный запрос добавим в блоке REST Assured
        this.receivedName = "Janet";
        System.out.println("Выполняем запрос...");
    }

    @Тогда("в ответе получаю имя {string}")
    public void resultName(String expectedName) {
        assert receivedName.equals(expectedName) :
                "Ожидалось: " + expectedName + ", получили: " + receivedName;
        System.out.println("✓ Имя совпадает: " + receivedName);
    }
}