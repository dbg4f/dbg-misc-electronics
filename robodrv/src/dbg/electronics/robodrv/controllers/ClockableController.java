package dbg.electronics.robodrv.controllers;

import dbg.electronics.robodrv.drive.MotorDrive;
import dbg.electronics.robodrv.mcu.McuCommunicationException;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ClockableController {

    private static final Logger log = Logger.getLogger(ClockableController.class);

    public static final int CLOCK_PER_POSITION_TIMEOUT = 10;
    public static final int MAX_REV_COUNT = 3;

    private MotorDrive motorDriveSteering;
    private MotorDrive motorDriveTraction;

    private PidController regulator = new PidController(new PidWeights(30, 0, 0));

    private int savedCommand;
    private int timeoutCounter;
    private int reverseCounter;
    private int savedError;
    private int lastTractionValue = 0;
    private int tractionTarget = 0;

    private int savedPwm = 0;
    private boolean savedDirection ;

    public void setMotorDriveSteering(MotorDrive motorDriveSteering) {
        this.motorDriveSteering = motorDriveSteering;
    }

    public void setMotorDriveTraction(MotorDrive motorDriveTraction) {
        this.motorDriveTraction = motorDriveTraction;
    }

    public void setTractionTarget(int tractionTarget) {
        this.tractionTarget = tractionTarget;
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


    private boolean inJitterZone(int target) {
        return target <= 1 && target >= -1;
    }

    private void controlTraction() throws InterruptedException, McuCommunicationException, IOException {

        if (lastTractionValue != tractionTarget && !inJitterZone(tractionTarget)) {

            boolean dir = (tractionTarget > 0);
            int pwm = Math.abs(tractionTarget);

            motorDriveTraction.setDirection(dir);
            motorDriveTraction.setPwm(pwm);

        }

    }

    public void onClock(int commandPosition, int actualPosition) throws Exception {

        controlTraction();

        log.debug(String.format("clock cmd=%d, pos=%d, %s ", commandPosition, actualPosition, this));

        int currentError = commandPosition - actualPosition;

        if (isCommandChanged(commandPosition)) {

            log.info(String.format("Command changed: %d->%d", savedCommand, commandPosition));

            resetCounters(currentError);

            savedCommand = commandPosition;
        }


        if (timeoutCounter <= 0 || reverseCounter <= 0) {
            if (savedPwm != 0) {
                setPwm(0);
            }
        }
        else {

            currentError = commandPosition - actualPosition;

            int pidResultValue = (int) regulator.getValue(currentError);

            int pwmValue = Math.abs(pidResultValue);

            if (pwmValue > 255) {
                pwmValue = 255;
            }

            setDirection(pidResultValue > 0);
            setPwm(pwmValue);

            timeoutCounter--;

            if (isReversed(currentError)) {
                reverseCounter--;
                log.info(String.format("Reverse: %d->%d", savedError, currentError));
            }

        }

        if (currentError != savedError) {
            log.info(String.format("Error changed: %d->%d", savedError, currentError));
        }


        savedError = currentError;



        // TODO: restrict reversals count and speed (relay jitter)
        // TODO: restrict reg interval cycles count (timeout, set pwm to 0)
        // TODO: detect HID/savedCommand signal jitter

    }

    private void setDirection(boolean forward) throws InterruptedException, IOException, McuCommunicationException {
        motorDriveSteering.setDirection(forward);
        savedDirection = forward;
    }

    private void setPwm(int pwm) throws InterruptedException, IOException, McuCommunicationException {
        motorDriveSteering.setPwm(pwm);
        if (savedPwm != pwm) {
            log.info(String.format("Pwm changed: %d->%d", savedPwm, pwm));
        }
        savedPwm = pwm;
    }

    @Override
    public String toString() {
        return "ClockableController{" +
                ", savedCommand=" + savedCommand +
                ", timeoutCounter=" + timeoutCounter +
                ", reverseCounter=" + reverseCounter +
                ", savedError=" + savedError +
                ", savedPwm=" + savedPwm +
                ", savedDirection=" + savedDirection +
                '}';
    }
}
