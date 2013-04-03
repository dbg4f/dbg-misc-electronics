package dbg.electronics.robodrv.head;

import dbg.electronics.robodrv.Event;
import dbg.electronics.robodrv.EventListener;
import dbg.electronics.robodrv.GenericThread;
import dbg.electronics.robodrv.graphics.MultiBufferFullScreen;
import dbg.electronics.robodrv.graphics.ValueWithHistory;
import dbg.electronics.robodrv.hid.HidEventFileReader;
import dbg.electronics.robodrv.hid.InputControlListener;
import dbg.electronics.robodrv.hid.InputRangedControl;
import dbg.electronics.robodrv.hid.StickDriver;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

import java.util.List;

public class Orchestrator implements FailureListener, EventListener<Event>, InputControlListener {

    private static final SimpleLogger log = LoggerFactory.getLogger();

    private List<GenericThread> threads;

    private ValueWithHistory stickX;
    private ValueWithHistory stickY;


    public void setStickX(ValueWithHistory stickX) {
        this.stickX = stickX;
    }

    public void setStickY(ValueWithHistory stickY) {
        this.stickY = stickY;
    }

    public void setThreads(List<GenericThread> threads) {
        this.threads = threads;
    }

    public Orchestrator() {
    }

    public void start() {

        for (GenericThread thread : threads) {
            thread.launch();
        }


    }

    @Override
    public void onUpdate(InputRangedControl control, int value) {
        if (control.getName().equals(StickDriver.Control.AXIS_X.name())) {
            stickX.update(value);
        }
        else if (control.getName().equals(StickDriver.Control.AXIS_Y.name())) {
            stickY.update(value);
        }
    }

    @Override
    public void onFailure(Failure failure) {
        log.error(String.valueOf(failure));
    }

    @Override
    public void onEvent(Event event) {

        log.info(String.valueOf(event));

        if (event.getEventCode() == Event.EventCode.SHUTDOWN) {

            for (GenericThread thread : threads) {
                thread.terminate();
            }
        }

        System.exit(0);


    }
}
