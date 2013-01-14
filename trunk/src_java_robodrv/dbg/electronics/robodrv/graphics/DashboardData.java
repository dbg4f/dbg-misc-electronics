package dbg.electronics.robodrv.graphics;

import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardData {

    private int testValuePercent;

    private Map<String, String> jsIndicators = new LinkedHashMap<String, String>();

    public synchronized void updateJs(String key, String value) {
        jsIndicators.put(key, value);
    }

    public synchronized void setTestValuePercent(int testValuePercent) {
        this.testValuePercent = testValuePercent;
    }

    public synchronized int getTestValuePercent() {
        return testValuePercent;
    }

    public synchronized Map<String, String> getJsIndicators() {
        return new LinkedHashMap<String, String>(jsIndicators);
    }
}
