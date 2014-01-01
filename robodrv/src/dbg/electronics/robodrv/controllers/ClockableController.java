package dbg.electronics.robodrv.controllers;

import dbg.electronics.robodrv.drive.MotorDrive;

public class ClockableController {

    public static final int CLOCK_PER_POSITION_TIMEOUT = 20;

    private MotorDrive motorDrive;

    private int savedCommand;
    private int timeoutCounter;




    public void onClock(int commandPosition, int actualPosition) throws Exception {

        int currentError = commandPosition - actualPosition;

        int errorSize = Math.abs(currentError);

        if (commandPosition != savedCommand) {

            timeoutCounter = errorSize * CLOCK_PER_POSITION_TIMEOUT;

            savedCommand = commandPosition;
        }


        if (timeoutCounter <= 0) {
            motorDrive.setPwm(0);
        }
        else {

            timeoutCounter--;
        }







        // TODO: restrict reversals count and speed (relay jitter)
        // TODO: restrict reg interval cycles count (timeout, set pwm to 0)
        // TODO: detect HID/savedCommand signal jitter

    }


}
