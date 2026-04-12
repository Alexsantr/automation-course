package config;

import org.aeonbits.owner.Config;

@Config.Sources({
        "classpath:${env}.properties",
        "file:local.properties",
        "system:properties",
        "system:env"
})
public interface DataConfig extends Config {

    @Key("browser")
    @DefaultValue("CHROME")
    String getBrowser();

    @Key("browserSize")
    @DefaultValue("1920x1080")
    String getBrowserSize();

    @Key("browserVersion")
    @DefaultValue("127.0")
    String getBrowserVersion();

    @Key("baseUrl")
    String getBaseUrl();

    @Key("isRemote")
    @DefaultValue("false")
    boolean isRemote();

    @Key("remoteUrl")
    String remoteUrl();

    @Key("db.url")
    String getDbUrl();

    @Key("db.user")
    String getDbUser();

    @Key("db.password")
    String getDbPassword();
}