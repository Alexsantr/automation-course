package api;

public class EndPoints {

    private EndPoints() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ==================== HEALTH ====================
    public static final class Health {
        public static final String GET_HEALTH = "/health";

        private Health() {}
    }

    // ==================== DEV ====================
    public static final class Dev {
        public static final String GET_TRACE_RECENT = "/api/v1/dev/trace/recent";
        public static final String POST_TRACE_CLEAR = "/api/v1/dev/trace/clear";

        private Dev() {}
    }

    // ==================== HELPER (для тестов) ====================
    public static final class Helper {
        public static final String GET_ACCOUNTS = "/api/v1/helper/accounts";
        public static final String GET_OTP_PREVIEW = "/api/v1/helper/otp/preview";
        public static final String POST_CLEAR_BROWSER = "/api/v1/helper/clear-browser";

        public static String increaseBalance(int accountId) {
            return String.format("/api/v1/helper/accounts/%d/increase", accountId);
        }

        public static String decreaseBalance(int accountId) {
            return String.format("/api/v1/helper/accounts/%d/decrease", accountId);
        }

        public static String zeroBalance(int accountId) {
            return String.format("/api/v1/helper/accounts/%d/zero", accountId);
        }

        private Helper() {}
    }

    // ==================== AUTH ====================
    public static final class Auth {
        public static final String POST_REGISTER = "/api/v1/auth/register";
        public static final String POST_LOGIN = "/api/v1/auth/login";

        private Auth() {}
    }

    // ==================== PROFILE ====================
    public static final class Profile {
        public static final String GET_PROFILE = "/api/v1/profile";
        public static final String PUT_PROFILE = "/api/v1/profile";

        private Profile() {}
    }

    // ==================== ACCOUNTS ====================
    public static final class Accounts {
        public static final String GET_ACCOUNTS = "/api/v1/accounts";
        public static final String POST_CREATE_ACCOUNT = "/api/v1/accounts";
        public static final String PUT_PRIMARY_ACCOUNTS = "/api/v1/accounts/primary";

        public static String closeAccount(int accountId) {
            return String.format("/api/v1/accounts/%d", accountId);
        }

        public static String topUpAccount(int accountId) {
            return String.format("/api/v1/accounts/%d/topup", accountId);
        }

        private Accounts() {}
    }

    // ==================== TRANSFERS ====================
    public static final class Transfers {
        public static final String POST_TRANSFER = "/api/v1/transfers";
        public static final String POST_TRANSFER_BY_ACCOUNT = "/api/v1/transfers/by-account";
        public static final String GET_CHECK_BY_ACCOUNT = "/api/v1/transfers/by-account/check";
        public static final String POST_EXTERNAL_BY_ACCOUNT = "/api/v1/transfers/external-by-account";
        public static final String GET_CHECK_BY_PHONE = "/api/v1/transfers/by-phone/check";
        public static final String POST_TRANSFER_BY_PHONE = "/api/v1/transfers/by-phone";
        public static final String POST_EXCHANGE = "/api/v1/transfers/exchange";
        public static final String GET_DAILY_USAGE = "/api/v1/transfers/daily-usage";
        public static final String GET_RATES = "/api/v1/transfers/rates";

        private Transfers() {}
    }

    // ==================== TRANSACTIONS ====================
    public static final class Transactions {
        public static final String GET_TRANSACTIONS = "/api/v1/transactions";

        public static String getReceipt(int transactionId) {
            return String.format("/api/v1/transactions/%d/receipt", transactionId);
        }

        private Transactions() {}
    }

    // ==================== PAYMENTS ====================
    public static final class Payments {
        public static final String GET_MOBILE_OPERATORS = "/api/v1/payments/mobile/operators";
        public static final String POST_MOBILE_PAYMENT = "/api/v1/payments/mobile";
        public static final String GET_VENDOR_PROVIDERS = "/api/v1/payments/vendor/providers";
        public static final String POST_VENDOR_PAYMENT = "/api/v1/payments/vendor";

        private Payments() {}
    }

    // ==================== ADMIN ====================
    public static final class Admin {
        public static final String GET_USERS = "/api/v1/admin/users";
        public static final String POST_RESTORE_INITIAL_STATE = "/api/v1/admin/restore-initial-state";

        public static String blockUser(int userId) {
            return String.format("/api/v1/admin/users/%d/block", userId);
        }

        public static String unblockUser(int userId) {
            return String.format("/api/v1/admin/users/%d/unblock", userId);
        }

        public static String deleteUser(int userId) {
            return String.format("/api/v1/admin/users/%d", userId);
        }

        public static String getUserBanks(int userId) {
            return String.format("/api/v1/admin/users/%d/banks", userId);
        }

        public static String updateUserBanks(int userId) {
            return String.format("/api/v1/admin/users/%d/banks", userId);
        }

        public static String getUserTransactions(int userId) {
            return String.format("/api/v1/admin/users/%d/transactions", userId);
        }

        private Admin() {}
    }
}