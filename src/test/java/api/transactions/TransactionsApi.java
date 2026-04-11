package api.transactions;

import api.spec.BaseSpec;
import model.transaction.TransactionPublic;

import java.util.List;

import static api.EndPoints.Transactions.GET_TRANSACTIONS;
import static api.EndPoints.Transactions.getReceiptPath;
import static io.restassured.RestAssured.given;

public class TransactionsApi implements ITransactionsApi {

    @Override
    public List<TransactionPublic> getTransactions(String token) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(GET_TRANSACTIONS)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList(".", TransactionPublic.class);
    }

    @Override
    public String getReceipt(String token, int transactionId) {
        return given()
                .spec(BaseSpec.getRequestSpec())
                .auth().oauth2(token)
                .when()
                .get(getReceiptPath(transactionId))
                .then()
                .statusCode(200)
                .extract()
                .asString();
    }
}