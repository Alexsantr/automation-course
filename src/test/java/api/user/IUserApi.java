package api.user;

import model.user.UserPublic;

public interface IUserApi {
    /**
     * Получить пользователя по ID
     */
    UserPublic getUserById(String token, int userId);

    /**
     * Получить пользователя по логину
     */
    UserPublic getUserByLogin(String token, String login);
}