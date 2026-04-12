package utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class UserDataGenerator {
    private static final Random RANDOM = new Random();

    private static final List<String> FIRST_NAMES = Arrays.asList(
            "Alex", "Max", "Leo", "Tom", "Sam", "Ben", "Dan", "Nick", "Paul", "John",
            "Anna", "Eva", "Mia", "Zoe", "Lia", "Kate", "Nina", "Vera", "Lena", "Sonya",
            "Ivan", "Olga", "Peter", "Maria", "Dmitry", "Elena", "Sergey", "Natalia"
    );



    public static String generateLogin() {
        String name = FIRST_NAMES.get(RANDOM.nextInt(FIRST_NAMES.size()));
        int number = RANDOM.nextInt(1000);
        return name + number;
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