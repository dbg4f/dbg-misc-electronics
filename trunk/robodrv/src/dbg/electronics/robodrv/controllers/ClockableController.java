package dbg.electronics.robodrv.controllers;

import dbg.electronics.robodrv.drive.MotorDrive;

public class ClockableController {

    public static final int CLOCK_PER_POSITION_TIMEOUT = 20;
    public static final int MAX_REV_COUNT = 3;

    private MotorDrive motorDrive;

    private int savedCommand;
    private int timeoutCounter;
    private int reverseCounter;
    private int savedError;

    public void setMotorDrive(MotorDrive motorDrive) {
        this.motorDrive = motorDrive;
    }

    private void resetCounters(int currentError) {

        timeoutCounter = Math.abs(currentError) * CLOCK_PER_POSITION_TIMEOUT;

        reverseCounter = MAX_REV_COUNT;

    }

    private boolean isCommandChanged(int command) {
        return command != savedCommand;
    }

    private boolean isReversed(int currentError) {
        return (currentError > 0) ^ (savedError > 0);
    }

    public void onClock(int commandPosition, int actualPosition) throws Exception {

        int currentError = commandPosition - actualPosition;

        if (isCommandChanged(commandPosition)) {

            resetCounters(currentError);

            savedCommand = commandPosition;
        }


        if (timeoutCounter <= 0 || reverseCounter <= 0) {
            motorDrive.setPwm(0);
        }
        else {




            timeoutCounter--;

            if (isReversed(currentError)) {
                reverseCounter--;
            }

        }


        savedError = currentError;



        // TODO: restrict reversals count and speed (relay jitter)
        // TODO: restrict reg interval cycles count (timeout, set pwm to 0)
        // TODO: detect HID/savedCommand signal jitter

    }


}
