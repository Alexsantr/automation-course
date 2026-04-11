package api.admin;

import model.user.UserPublic;
import model.transaction.TransactionPublic;
import model.admin.UserBanksUpdateRequest;

import java.util.List;

public interface IAdminApi {
    /**
     * Получить список всех пользователей
     */
    List<UserPublic> getAllUsers(String token);

    /**
     * Заблокировать пользователя
     */
    UserPublic blockUser(String token, int userId);

    /**
     * Разблокировать пользователя
     */
    UserPublic unblockUser(String token, int userId);

    /**
     * Удалить пользователя
     */
    String deleteUser(String token, int userId);

    /**
     * Восстановить БД до начального состояния
     */
    String restoreInitialState(String token);

    /**
     * Получить банки пользователя для перевода по телефону
     */
    List<String> getUserBanks(String token, int userId);

    /**
     * Настроить банки пользователя
     */
    String updateUserBanks(String token, int userId, UserBanksUpdateRequest request);

    /**
     * Получить транзакции пользователя
     */
    List<TransactionPublic> getUserTransactions(String token, int userId);
}