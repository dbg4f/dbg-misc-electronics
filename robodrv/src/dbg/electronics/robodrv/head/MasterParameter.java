package dbg.electronics.robodrv.head;

import dbg.electronics.robodrv.OutOfRange;
import dbg.electronics.robodrv.Range;


/**
 * Created: 1/28/13  9:32 PM
 */
public class MasterParameter {

    private final MasterParameterType type;
    private final Range range;
    private int value;
    private long lastUpdated;

    public MasterParameter(MasterParameterType type, Range range) {
        this.type = type;
        this.range = range;
    }

    public MasterParameterType getType() {
        return type;
    }

    public synchronized int getValue() {
        return value;
    }

    public synchronized long getLastUpdated() {
        return lastUpdated;
    }

    public Range getRange() {
        return range;
    }

    public MasterParameter update(int value) {
        range.validate(value);
        long ts = System.currentTimeMillis();
        MasterParameter oldParameter = new MasterParameter(type, range);
        synchronized (this) {
            oldParameter.value = value;
            oldParameter.lastUpdated = lastUpdated;
            this.value = value;
            lastUpdated = ts;
        }
        return oldParameter;
    }

    public MasterParameter snapshot() {
        MasterParameter snapshot = new MasterParameter(type, range);
        synchronized (this) {
            snapshot.value = value;
            snapshot.lastUpdated = lastUpdated;
        }
        return snapshot;
    }

    @Override
    public String toString() {
        return "MasterParameter{" +
                "type=" + type +
                ", range=" + range +
                ", value=" + value +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
