package utils;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

    public static final String USER_LOGIN = "USER_LOGIN";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static final String USER_TOKEN = "USER_TOKEN";

    private final Map<String, String> data = new HashMap<>();

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String get(String key) {
        return data.get(key);
    }



}