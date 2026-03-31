package utils;

import java.util.UUID;

public class UserDataGenerator {

    public static String generateLogin() {
        return "User" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public static String generatePassword() {
        return "Pass" + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + "A1@";
    }

    public static String resolve(String value) {
        if (value.equalsIgnoreCase("RANDOM_LOGIN")) {
            return generateLogin();
        }
        if (value.equalsIgnoreCase("RANDOM_PASSWORD")) {
            return generatePassword();
        }
        if (value.equalsIgnoreCase("RANDOM")) {
            return generateLogin(); // дефолт
        }
        return value; // возвращает как есть если не RANDOM
    }
}