package api.auth;

import io.restassured.http.ContentType;
import model.auth.AuthRequest;
import model.auth.AuthResponse;
import model.auth.RegisterRequest;
import model.auth.RegisterResponse;

import static io.restassured.RestAssured.given;

public class AuthApi implements IAuthApi {

    @Override
    public RegisterResponse authUser(RegisterRequest authRequest) {
        return given()
                .contentType(ContentType.JSON)
                .body(authRequest)
                .when()
                .post("http://localhost:8001/api/v1/auth/register")
                .then()
                .extract()
                .as(RegisterResponse.class);
    }

    @Override
    public AuthResponse getAuthUser(AuthRequest authRequest) {
        return given()
                .contentType(ContentType.JSON)
                .body(authRequest)
                .when()
                .post("http://localhost:8001/api/v1/auth/login")
                .then()
                .extract()
                .as(AuthResponse.class);
    }
}