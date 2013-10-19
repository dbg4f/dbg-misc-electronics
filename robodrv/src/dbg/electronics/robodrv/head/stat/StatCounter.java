package dbg.electronics.robodrv.head.stat;

import java.util.Date;

public class StatCounter {
    public final long value;
    public final Date lastUpdated;

    StatCounter(long value, Date lastUpdated) {
        this.value = value;
        this.lastUpdated = lastUpdated;
    }
}
