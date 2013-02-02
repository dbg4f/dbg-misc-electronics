package dbg.electronics.robodrv.hid;

import dbg.electronics.robodrv.EventListener;
import dbg.electronics.robodrv.Range;
import dbg.electronics.robodrv.head.MasterParameterType;
import dbg.electronics.robodrv.head.MultilineReportable;
import dbg.electronics.robodrv.head.ParameterUpdater;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created: 1/31/13  9:44 PM
 *
 *
 *

 30001 - 0 fwd  FF back
 30000 - 0 left FF right
 10120 - discharge
 30005 - 0 fwd FF back

 10124 - 5
 10125 - 6
 10126 - 7
 10127 - 8
 10123 - 4
 10121 - 2
 10122 - 3

 30011 - -1 fwd , 1 back
 30010 - -1 right, 1 left
 *
 *
 */
public class StickDriver implements EventListener<HidInputReport> , MultilineReportable {

    private ParameterUpdater updater;

    private Map<Long, Long> currentState = new LinkedHashMap<Long, Long>();


    public void setUpdater(ParameterUpdater updater) {
        this.updater = updater;
    }

    @Override
    public String[] toStringArray() {

        Map<Long, Long> currentStateSnapshot;

        synchronized (this) {
             currentStateSnapshot = new LinkedHashMap<Long, Long>(currentState);
        }

        String[] lines = new String[currentStateSnapshot.size()];

        int i=0;
        for (Map.Entry<Long, Long> entry : currentStateSnapshot.entrySet()) {
            lines[i++] = String.format("%08X - %08X", entry.getKey(), entry.getValue());
        }

        return lines;

    }

    @Override
    public void onEvent(HidInputReport event) {

        long controlId = event.getType() * 0x10000 + event.getCode();

        synchronized (this) {
            currentState.put(controlId, event.getValue());
        }

        if (controlId == 0x30001) {
            updater.update(MasterParameterType.MOTION_SPEED, (int)event.getValue(), new Range(0, 255));
        }
        else if (controlId == 0x30000) {
            updater.update(MasterParameterType.STEERING_ANGLE, (int)event.getValue(), new Range(0, 255));
        }


    }
}
