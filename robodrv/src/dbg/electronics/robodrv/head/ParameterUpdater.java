package dbg.electronics.robodrv.head;

import dbg.electronics.robodrv.Range;

public interface ParameterUpdater {

    void update(MasterParameterType type, int value, Range valueRange);

}
