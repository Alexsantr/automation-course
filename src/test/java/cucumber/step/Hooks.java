package cucumber.step;

import helpers.CustomAllureListener;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DatabaseHelper;
import utils.ScenarioContext;

public class Hooks {
    private static final Logger log = LoggerFactory.getLogger(Hooks.class);
    private static DatabaseHelper databaseHelper;

    static {
        // Shutdown hook для закрытия соединений с БД
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (databaseHelper != null) {
                databaseHelper.close();
                log.info("Database connection closed on JVM shutdown");
            }
        }));
    }

    private final ScenarioContext scenarioContext;

    public Hooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(order = 0)
    public void clearScenarioContext() {
        scenarioContext.clear();
        log.debug("ScenarioContext cleared for new scenario");
    }

    @Before(order = 1)
    public void setUp() {
        RestAssured.reset();
        RestAssured.filters(
                CustomAllureListener.withCustomTemplates(),
                new RequestLoggingFilter(),
                new ResponseLoggingFilter()
        );
        log.debug("RestAssured filters configured");
    }

    @Before(order = 2, value = "@Database")
    public void initDatabaseConnection() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper();
            log.info("Database connection pool initialized");
        }
        scenarioContext.putObject(ScenarioContext.DB_HELPER, databaseHelper);
    }

    @After
    public void tearDown() {
        // Очищаем контекст, но оставляем БД для следующих тестов
        log.debug("Scenario completed");
    }
}