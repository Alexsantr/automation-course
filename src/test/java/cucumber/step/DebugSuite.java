package cucumber.step;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = "cucumber.glue", value = "cucumber.step")
@ConfigurationParameter(key = "cucumber.filter.tags", value = "@Debug")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm")
@ConfigurationParameter(key = "cucumber.object-factory", value = "io.cucumber.picocontainer.PicoFactory")
public class DebugSuite {
}
