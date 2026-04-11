package api.tranfers;

import api.spec.BaseSpec;
import model.transaction.TransactionPublic;
import model.transfer.*;

import static api.EndPoints.Transfers.*;
import static io.restassured.RestAssured.given;

public class TransfersApi implements api.transfers.ITransfersApi {

    @Override
    public TransactionPublic createTransfer(String token, TransferCreateRequest request) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .body(request)
                .when()
                .post(POST_TRANSFER)
                .then()
                .statusCode(201)
                .extract()
                .as(TransactionPublic.class);
    }

    @Override
    public TransactionPublic createTransferByAccount(String token, TransferByAccountRequest request) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .body(request)
                .when()
                .post(POST_TRANSFER_BY_ACCOUNT)
                .then()
                .statusCode(201)
                .extract()
                .as(TransactionPublic.class);
    }

    @Override
    public TransferByAccountCheckResponse checkAccountByNumber(String token, String accountNumber) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .queryParam("target_account_number", accountNumber)
                .when()
                .get(GET_CHECK_BY_ACCOUNT)
                .then()
                .statusCode(200)
                .extract()
                .as(TransferByAccountCheckResponse.class);
    }

    @Override
    public TransactionPublic createExternalTransfer(String token, TransferByAccountRequest request) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .body(request)
                .when()
                .post(POST_EXTERNAL_BY_ACCOUNT)
                .then()
                .statusCode(201)
                .extract()
                .as(TransactionPublic.class);
    }

    @Override
    public TransferByPhoneCheckResponse checkPhone(String token, String phone) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .queryParam("phone", phone)
                .when()
                .get(GET_CHECK_BY_PHONE)
                .then()
                .statusCode(200)
                .extract()
                .as(TransferByPhoneCheckResponse.class);
    }

    @Override
    public TransactionPublic createTransferByPhone(String token, TransferByPhoneRequest request) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .body(request)
                .when()
                .post(POST_TRANSFER_BY_PHONE)
                .then()
                .statusCode(201)
                .extract()
                .as(TransactionPublic.class);
    }

    @Override
    public TransactionPublic exchangeCurrency(String token, ExchangeRequest request) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .body(request)
                .when()
                .post(POST_EXCHANGE)
                .then()
                .statusCode(201)
                .extract()
                .as(TransactionPublic.class);
    }

    @Override
    public DailyUsageResponse getDailyUsage(String token) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(GET_DAILY_USAGE)
                .then()
                .statusCode(200)
                .extract()
                .as(DailyUsageResponse.class);
    }

    @Override
    public ExchangeRatesResponse getExchangeRates(String token) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(GET_RATES)
                .then()
                .statusCode(200)
                .extract()
                .as(ExchangeRatesResponse.class);
    }
}