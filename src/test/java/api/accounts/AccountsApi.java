package api.accounts;

import api.spec.BaseSpec;
import model.account.AccountCreateRequest;
import model.account.AccountPublic;
import model.account.AccountTopupRequest;
import model.account.PrimaryAccountsRequest;
import model.transaction.TransactionPublic;

import java.util.List;

import static api.EndPoints.Accounts.*;
import static io.restassured.RestAssured.given;

public class AccountsApi implements IAccountsApi {

    @Override
    public List<AccountPublic> getAccounts(String token) {
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
    public AccountPublic createAccount(String token, AccountCreateRequest request) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .body(request)
                .when()
                .post(POST_CREATE_ACCOUNT)
                .then()
                .statusCode(201)
                .extract()
                .as(AccountPublic.class);
    }

    @Override
    public TransactionPublic topUpAccount(String token, int accountId, AccountTopupRequest request) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .body(request)
                .when()
                .post(getTopUpAccountPath(accountId))
                .then()
                .statusCode(201)
                .extract()
                .as(TransactionPublic.class);
    }

    @Override
    public String closeAccount(String token, int accountId) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .delete(getCloseAccountPath(accountId))
                .then()
                .statusCode(200)
                .extract()
                .path("detail");
    }

    @Override
    public String setPrimaryAccounts(String token, List<Integer> accountIds) {
        PrimaryAccountsRequest request = new PrimaryAccountsRequest(accountIds);
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .body(request)
                .when()
                .put(PUT_PRIMARY_ACCOUNTS)
                .then()
                .statusCode(200)
                .extract()
                .path("detail");
    }

    @Override
    public AccountPublic getAccountById(String token, int accountId) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(getAccountByIdPath(accountId))
                .then()
                .extract()
                .as(AccountPublic.class);
    }
}