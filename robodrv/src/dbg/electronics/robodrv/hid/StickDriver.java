package dbg.electronics.robodrv.hid;

import dbg.electronics.robodrv.EventListener;
import dbg.electronics.robodrv.head.MultilineReportable;
import dbg.electronics.robodrv.head.ParameterUpdater;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created: 1/31/13  9:44 PM
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

        //System.out.println("event = " + event);




    }
}
