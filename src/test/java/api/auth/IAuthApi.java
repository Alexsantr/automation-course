package api.auth;

import model.auth.AuthRequest;
import model.auth.AuthResponse;
import model.auth.RegisterRequest;
import model.auth.RegisterResponse;

public interface IAuthApi {
    /**
     * Метод регистрации клиента
     */
    RegisterResponse authUser(RegisterRequest authRequest);

    /**
     * Метод авторизации клиента
     */
    AuthResponse getAuthUser(AuthRequest authRequest);

}