package utils;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
    // Auth
    public static final String USER_LOGIN = "USER_LOGIN";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static final String USER_TOKEN = "USER_TOKEN";
    public static final String ADMIN_TOKEN = "ADMIN_TOKEN";

    // Helper
    public static final String OTP_CODE = "OTP_CODE";
    public static final String CLEAR_CACHE_INSTRUCTIONS = "CLEAR_CACHE_INSTRUCTIONS";

    // User
    public static final String USER_ID = "USER_ID";
    public static final String USER_RESPONSE = "USER_RESPONSE";
    public static final String USERS_LIST = "USERS_LIST";
    public static final String UPDATED_USER = "UPDATED_USER";
    public static final String USER_TRANSACTIONS = "USER_TRANSACTIONS";

    // Accounts
    public static final String ACCOUNT_ID = "ACCOUNT_ID";
    public static final String ACCOUNT_BALANCE = "ACCOUNT_BALANCE";
    public static final String FROM_ACCOUNT_ID = "FROM_ACCOUNT_ID";
    public static final String FROM_ACCOUNT_BALANCE = "FROM_ACCOUNT_BALANCE";
    public static final String TO_ACCOUNT_ID = "TO_ACCOUNT_ID";
    public static final String TO_ACCOUNT_BALANCE = "TO_ACCOUNT_BALANCE";

    // Transfers
    public static final String TARGET_ACCOUNT_NUMBER = "TARGET_ACCOUNT_NUMBER";
    public static final String RECIPIENT_PHONE = "RECIPIENT_PHONE";
    public static final String RECIPIENT_BANK_ID = "RECIPIENT_BANK_ID";
    public static final String RECIPIENT_IN_OUR_BANK = "RECIPIENT_IN_OUR_BANK";
    public static final String DAILY_TRANSFERRED_AMOUNT = "DAILY_TRANSFERRED_AMOUNT";


    // Admin
    public static final String USER_STATUS = "USER_STATUS";

    // Transactions
    public static final String TRANSACTION_ID = "TRANSACTION_ID";
    public static final String OTHER_TRANSACTION_ID = "OTHER_TRANSACTION_ID";
    public static final String TRANSACTIONS_LIST = "TRANSACTIONS_LIST";
    public static final String RECEIPT_HTML = "RECEIPT_HTML";

    /** Ключ для {@link utils.DatabaseHelper} в objectData; закрывается в {@code Hooks}. */
    public static final String DB_HELPER = "dbHelper";

    // Common
    public static final String LAST_STATUS_CODE = "LAST_STATUS_CODE";
    public static final String LAST_ERROR_MESSAGE = "LAST_ERROR_MESSAGE";
    public static final String LAST_TRANSACTION = "LAST_TRANSACTION";
    public static final String ERROR_RESPONSE = "ERROR_RESPONSE";

    private final Map<String, String> stringData = new HashMap<>();
    private final Map<String, Object> objectData = new HashMap<>();

    public void put(String key, String value) {
        stringData.put(key, value);
    }

    public String get(String key) {
        return stringData.get(key);
    }

    public void putObject(String key, Object value) {
        objectData.put(key, value);
    }

    public Object getObject(String key) {
        return objectData.get(key);
    }

    public void clear() {
        stringData.clear();
        objectData.clear();
    }
}