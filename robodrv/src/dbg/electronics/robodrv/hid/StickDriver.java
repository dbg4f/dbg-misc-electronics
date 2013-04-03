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
 * <p/>
 * <p/>
 * <p/>
 * <p/>
 * 30001 - 0 fwd  FF back
 * 30000 - 0 left FF right
 * 10120 - discharge
 * 30005 - 0 fwd FF back
 * <p/>
 * 10124 - 5
 * 10125 - 6
 * 10126 - 7
 * 10127 - 8
 * 10123 - 4
 * 10121 - 2
 * 10122 - 3
 * <p/>
 * 30011 - -1 fwd , 1 back
 * 30010 - -1 right, 1 left
 */
public class StickDriver implements EventListener<HidInputReport>, MultilineReportable {

    public enum Control {

        AXIS_X(InputRangedControl.RANGE_AXIS, 0x30000),
        AXIS_Y(InputRangedControl.RANGE_AXIS, 0x30001);

        final Range range;
        final long hidId;
        final InputRangedControl rangedControl;

        Control(Range range, long hidId) {
            this.range = range;
            this.hidId = hidId;
            rangedControl = new InputRangedControl(name(), range);
        }
    }

    private Map<Long, Control> controls = new LinkedHashMap<Long, Control>();

    private ParameterUpdater updater;

    private Map<Long, Long> currentState = new LinkedHashMap<Long, Long>();

    private InputControlListener inputControlListener;

    public StickDriver() {
        for (Control control : Control.values()) {
            controls.put(control.hidId, control);
        }
    }

    public void setUpdater(ParameterUpdater updater) {
        this.updater = updater;
    }

    public void setInputControlListener(InputControlListener inputControlListener) {
        this.inputControlListener = inputControlListener;
    }

    @Override
    public String[] toStringArray() {

        Map<Long, Long> currentStateSnapshot;

        synchronized (this) {
            currentStateSnapshot = new LinkedHashMap<Long, Long>(currentState);
        }

        String[] lines = new String[currentStateSnapshot.size()];

        int i = 0;
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

        if (controlId == Control.AXIS_Y.hidId) {
            updater.update(MasterParameterType.MOTION_SPEED, (int) event.getValue(), new Range(0, 255));
        } else if (controlId == Control.AXIS_X.hidId) {
            updater.update(MasterParameterType.STEERING_ANGLE, (int) event.getValue(), new Range(0, 255));
        }

        if (controls.containsKey(controlId)) {
            Control control = controls.get(controlId);
            inputControlListener.onUpdate(control.rangedControl, (int)event.getValue());
        }


    }
}
