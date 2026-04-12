package api.helper;

import api.spec.BaseSpec;
import model.account.AccountPublic;

import java.util.List;

import static api.EndPoints.Accounts.GET_ACCOUNTS;
import static api.EndPoints.Helper.*;
import static io.restassured.RestAssured.given;

public class HelperApi implements IHelperApi {

    @Override
    public List<AccountPublic> getHelperAccounts(String token) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(GET_ACCOUNTS)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList(".", AccountPublic.class);
    }

    @Override
    public String getOtpCode(String token) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(GET_OTP_PREVIEW)
                .then()
                .statusCode(200)
                .extract()
                .path("otp");
    }

    @Override
    public AccountPublic increaseBalance(String token, int accountId, String amount, String purpose) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .queryParam("amount", amount)
                .queryParam("purpose", purpose != null ? purpose : "")
                .when()
                .post(getIncreaseBalancePath(accountId))
                .then()
                .statusCode(200)
                .extract()
                .as(AccountPublic.class);
    }

    @Override
    public AccountPublic decreaseBalance(String token, int accountId, String amount) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .queryParam("amount", amount)
                .when()
                .post(getDecreaseBalancePath(accountId))
                .then()
                .statusCode(200)
                .extract()
                .as(AccountPublic.class);
    }

    @Override
    public AccountPublic zeroBalance(String token, int accountId) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .post(getZeroBalancePath(accountId))
                .then()
                .statusCode(200)
                .extract()
                .as(AccountPublic.class);
    }

    @Override
    public String clearBrowserCache(String token) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .post(POST_CLEAR_BROWSER)
                .then()
                .statusCode(200)
                .extract()
                .path("instructions");
    }
}