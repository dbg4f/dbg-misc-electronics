package dbg.electronics.robodrv.head.stat;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class Statistics {

    Map<StatisticCounterType, Long> counters = new LinkedHashMap<StatisticCounterType, Long>();
    Map<StatisticCounterType, Date> countersUpdate = new LinkedHashMap<StatisticCounterType, Date>();

    public Statistics() {
        for (StatisticCounterType type : StatisticCounterType.values()) {
            counters.put(type, 0L);
            countersUpdate.put(type, new Date());
        }

    }

    public synchronized  void update(StatisticCounterType type, long add) {
        counters.put(type, counters.get(type) + add);
        countersUpdate.put(type, new Date());
    }

    public synchronized StatCounter getCounter(StatisticCounterType type) {
        return new StatCounter(counters.get(type), countersUpdate.get(type));
    }

}
