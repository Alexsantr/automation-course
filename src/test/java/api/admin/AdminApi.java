package api.admin;

import api.spec.BaseSpec;
import model.admin.UserBanksUpdateRequest;
import model.transaction.TransactionPublic;
import model.user.UserPublic;

import java.util.List;

import static api.EndPoints.Admin.*;
import static io.restassured.RestAssured.given;

public class AdminApi implements IAdminApi {

    @Override
    public List<UserPublic> getAllUsers(String token) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(GET_USERS)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList(".", UserPublic.class);
    }

    @Override
    public UserPublic blockUser(String token, int userId) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .post(getBlockUserPath(userId))
                .then()
                .statusCode(200)
                .extract()
                .as(UserPublic.class);
    }

    @Override
    public UserPublic unblockUser(String token, int userId) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .post(getUnblockUserPath(userId))
                .then()
                .statusCode(200)
                .extract()
                .as(UserPublic.class);
    }

    @Override
    public String deleteUser(String token, int userId) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .delete(getDeleteUserPath(userId))
                .then()
                .statusCode(200)
                .extract()
                .path("detail");
    }

    @Override
    public String restoreInitialState(String token) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .post(POST_RESTORE_INITIAL_STATE)
                .then()
                .statusCode(200)
                .extract()
                .path("detail");
    }

    @Override
    public List<String> getUserBanks(String token, int userId) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(getUserBanksPath(userId))
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("bank_codes");
    }

    @Override
    public String updateUserBanks(String token, int userId, UserBanksUpdateRequest request) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .body(request)
                .when()
                .put(getUpdateUserBanksPath(userId))
                .then()
                .statusCode(200)
                .extract()
                .path("detail");
    }

    @Override
    public List<TransactionPublic> getUserTransactions(String token, int userId) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(getUserTransactionsPath(userId))
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList(".", TransactionPublic.class);
    }
}