package api.auth;

import api.spec.BaseSpec;
import io.restassured.http.ContentType;
import model.auth.AuthRequest;
import model.auth.AuthResponse;
import model.auth.RegisterRequest;
import model.auth.RegisterResponse;

import static api.EndPoints.Auth.POST_LOGIN;
import static api.EndPoints.Auth.POST_REGISTER;
import static io.restassured.RestAssured.given;

public class AuthApi implements IAuthApi {

    @Override
    public RegisterResponse authUser(RegisterRequest authRequest) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .contentType(ContentType.JSON)
                .body(authRequest)
                .when()
                .post(POST_REGISTER)
                .then()
                .extract()
                .as(RegisterResponse.class);
    }

    @Override
    public AuthResponse getAuthUser(AuthRequest authRequest) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .contentType(ContentType.JSON)
                .body(authRequest)
                .when()
                .post(POST_LOGIN)
                .then()
                .extract()
                .as(AuthResponse.class);
    }
}