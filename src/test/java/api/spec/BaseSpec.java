package api.spec;

import config.DataConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.aeonbits.owner.ConfigFactory;

public class BaseSpec {

    private static final DataConfig config = ConfigFactory.create(DataConfig.class);

    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .setContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification statusCode200 =
            new ResponseSpecBuilder()
                    .expectStatusCode(200)
                    .log(LogDetail.ALL)
                    .build();

    public static ResponseSpecification statusCode404 =
            new ResponseSpecBuilder()
                    .expectStatusCode(404)
                    .log(LogDetail.ALL)
                    .build();
}