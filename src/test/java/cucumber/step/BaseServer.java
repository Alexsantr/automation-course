package cucumber.step;

import utils.ScenarioContext;

public abstract class BaseServer {
    protected final ScenarioContext context;

    public BaseServer(ScenarioContext context) {
        this.context = context;
    }

    protected void put(String key, String value) {
        context.put(key, value);
    }

    protected String get(String key) {
        return context.get(key);
    }

    protected void putObject(String key, Object value) {
        context.putObject(key, value);
    }

    protected Object getObject(String key) {
        return context.getObject(key);
    }
}