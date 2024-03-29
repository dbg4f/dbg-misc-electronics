package dbg.electronics.robodrv.head;

import dbg.electronics.robodrv.Event;
import dbg.electronics.robodrv.EventListener;
import dbg.electronics.robodrv.GenericThread;
import dbg.electronics.robodrv.Range;
import dbg.electronics.robodrv.controllers.Steering;
import dbg.electronics.robodrv.drive.DriveState;
import dbg.electronics.robodrv.drive.MotorDrive;
import dbg.electronics.robodrv.graphics.ValueWithHistory;
import dbg.electronics.robodrv.groovy.Functions;
import dbg.electronics.robodrv.hid.InputControlListener;
import dbg.electronics.robodrv.hid.InputRangedControl;
import dbg.electronics.robodrv.hid.StickDriver;
import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;
import dbg.electronics.robodrv.controllers.PidController;
import dbg.electronics.robodrv.controllers.PidWeights;
import dbg.electronics.robodrv.controllers.RangeRestriction;

import java.util.List;

public class Orchestrator implements FailureListener, EventListener<Event>, InputControlListener {

    private static final SimpleLogger log = LoggerFactory.getLogger();

    private List<GenericThread> threads;

    private ValueWithHistory stickX;
    private ValueWithHistory stickY;

    private Functions functions;
    public DriveState driveState;


    public void setDriveState(DriveState driveState) {
        this.driveState = driveState;
    }

    public void setStickX(ValueWithHistory stickX) {
        this.stickX = stickX;
    }

    public void setStickY(ValueWithHistory stickY) {
        this.stickY = stickY;
    }

    public void setThreads(List<GenericThread> threads) {
        this.threads = threads;
    }

    public void setFunctions(Functions functions) {
        this.functions = functions;
    }

    public Orchestrator() {
    }

    public void start() {

        for (GenericThread thread : threads) {
            thread.launch();
        }

        new Thread(new Steering(this)).start();


    }


    int currentTargetPos;

    @Override
    public void onUpdate(InputRangedControl control, int value) {
        if (control.getName().equals(StickDriver.Control.AXIS_X.name())) {
            stickX.update(value);
            int unsignedValue = (0xFF & value);

            Range stickRange = new Range(0, 255);
            Range posRange = new Range(140, 220);

            currentTargetPos = stickRange.remapTo(unsignedValue, posRange);

            driveState.setCurrentTargetPos(currentTargetPos);

            //if (Math.abs(currentTargetPos - driveState.getCurrentRawPos()) > 2) {
            //    functions.pos(currentTargetPos);
            //}


        }
        else if (control.getName().equals(StickDriver.Control.AXIS_Y.name())) {
            stickY.update(value);

            int unsignedValue = (0xFF & value);

            Range stickRange = new Range(0, 255);

            Range throttleRange = new Range(-255, 255);

            int currentThrottleValue = stickRange.remapTo(unsignedValue, throttleRange);

            driveState.setCurrentTractionTarget(currentThrottleValue);

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
