package api.spec;

import config.DataConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.aeonbits.owner.ConfigFactory;

public class BaseSpec {

    private static final DataConfig config = ConfigFactory.create(DataConfig.class);

    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .setContentType(ContentType.JSON)
                .build();
    }

}