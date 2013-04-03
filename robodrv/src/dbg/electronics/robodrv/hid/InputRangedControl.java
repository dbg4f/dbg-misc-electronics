package dbg.electronics.robodrv.hid;

import dbg.electronics.robodrv.Range;

/**
 */
public class InputRangedControl {

    private final String name;
    private final Range range;

    public static final Range RANGE_AXIS = new Range(0, 255);
    public static final Range RANGE_BUTTON = new Range(0, 1);

    public InputRangedControl(String name, Range range) {
        this.name = name;
        this.range = range;
    }

    public String getName() {
        return name;
    }

    public Range getRange() {
        return range;
    }

    @Override
    public String toString() {
        return "InputRangedControl{" +
                "name='" + name + '\'' +
                ", range=" + range +
                '}';
    }
}
