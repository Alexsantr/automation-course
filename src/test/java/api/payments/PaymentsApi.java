package api.payments;

import api.spec.BaseSpec;
import model.transaction.TransactionPublic;
import model.payment.MobilePaymentRequest;
import model.payment.VendorPaymentRequest;

import java.util.List;
import java.util.Map;

import static api.EndPoints.Payments.*;
import static io.restassured.RestAssured.given;

public class PaymentsApi implements IPaymentsApi {

    @Override
    public List<Map<String, String>> getMobileOperators(String token) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(GET_MOBILE_OPERATORS)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList(".");
    }

    @Override
    public TransactionPublic payMobile(String token, MobilePaymentRequest request) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .body(request)
                .when()
                .post(POST_MOBILE_PAYMENT)
                .then()
                .statusCode(201)
                .extract()
                .as(TransactionPublic.class);
    }

    @Override
    public List<Map<String, String>> getVendorProviders(String token) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(GET_VENDOR_PROVIDERS)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList(".");
    }

    @Override
    public TransactionPublic payVendor(String token, VendorPaymentRequest request) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .body(request)
                .when()
                .post(POST_VENDOR_PAYMENT)
                .then()
                .statusCode(201)
                .extract()
                .as(TransactionPublic.class);
    }
}