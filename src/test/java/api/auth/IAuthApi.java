package api.auth;

import model.auth.AuthRequest;
import model.auth.AuthResponse;
import model.auth.RegisterRequest;
import model.auth.RegisterResponse;

public interface IAuthApi
{
    RegisterResponse authUser(RegisterRequest authRequest);

      AuthResponse getAuthUser(AuthRequest authRequest);

}