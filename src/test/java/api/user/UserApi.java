package api.user;

import api.spec.BaseSpec;
import model.user.UserPublic;

import static api.EndPoints.Users.GET_USER_BY_ID;
import static api.EndPoints.Users.getUserByIdPath;
import static io.restassured.RestAssured.given;

public class UserApi implements IUserApi {

    @Override
    public UserPublic getUserById(String token, int userId) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(getUserByIdPath(userId))
                .then()
                .statusCode(200)
                .extract()
                .as(UserPublic.class);
    }

    @Override
    public UserPublic getUserByLogin(String token, String login) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .queryParam("login", login)
                .when()
                .get(GET_USER_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .as(UserPublic.class);
    }
}