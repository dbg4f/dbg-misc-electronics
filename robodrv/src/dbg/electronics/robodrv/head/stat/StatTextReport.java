package dbg.electronics.robodrv.head.stat;

import dbg.electronics.robodrv.head.MultilineReportable;
import dbg.electronics.robodrv.io.FormatUtils;

public class StatTextReport implements MultilineReportable {

    private Statistics statistics;

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    public String[] toStringArray() {
        String[] lines = new String[StatisticCounterType.values().length];
        int i=0;
        for (StatisticCounterType type : StatisticCounterType.values()) {
            StatCounter counter = statistics.getCounter(type);
            lines[i++] = String.format("%s %15s %8s", FormatUtils.time(counter.lastUpdated), type, counter.value);
        }
        return lines;
    }
}
