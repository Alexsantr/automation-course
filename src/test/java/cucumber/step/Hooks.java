package cucumber.step;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import utils.DatabaseHelper;
import utils.ScenarioContext;

public class Hooks {
    private final ScenarioContext scenarioContext;
    private DatabaseHelper dbHelper;

    public Hooks(ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    @Before(order = 0)
    public void clearScenarioContext() {
        scenarioContext.clear();
        System.out.println("ScenarioContext cleared for new test");
    }

    @Before(order = 1)
    public void setUp() {
        RestAssured.reset();
        RestAssured.filters(
                new AllureRestAssured(),
                new RequestLoggingFilter(),
                new ResponseLoggingFilter()
        );
        System.out.println("RestAssured configured for test");
    }

    @Before(order = 2, value = "@Database")
    public void initDatabaseConnection() {
        // Инициализируем БД только для тестов с тегом @Database
        dbHelper = new DatabaseHelper();
        scenarioContext.putObject("dbHelper", dbHelper);
        System.out.println("Database connection initialized");
    }

    @After
    public void tearDown() {
        if (dbHelper != null) {
            dbHelper.close();
            System.out.println("Database connection closed");
        }
    }
}