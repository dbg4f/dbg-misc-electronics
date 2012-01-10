package dbg.electronics.ui;

import dbg.electronics.*;
import org.apache.log4j.Logger;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class Steering {

    private static Logger log = Logger.getLogger(Steering.class);

    protected int currentPos;

    protected boolean currentDir = true;

    protected RemoteDisplay displayPos;

    private final McConnection mc;

    Steering(McConnection mc) throws IOException, McCommunicationException {

        this.mc = mc;

        displayPos = new RemoteDisplay("127.0.0.1", 5556);

    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void applyOffset(int offset) throws IOException, InterruptedException, McCommunicationException {

        int finalPos = currentPos + offset;

        SteeringMovement movement = calcMovement(finalPos);

        log.debug(movement.toString());

        setDir(movement.dir);

        if (movement.overCenter) {


            turn(movement.stepsToCenter, 10);

            center();

        }

        if (movement.stepsAbs > 0) {

            turn(movement.stepsAbs, 10);

        }

        //if (currentDir != movement.dir) {

        //    setDir(movement.dir);

        //    currentDir = movement.dir;

        //}

        currentPos = finalPos;

    }


    protected SteeringMovement calcMovement(int finalPos) {

        boolean overCenter = false;

        int remain = Math.abs(finalPos - currentPos);

        //int stepsToCenter = 0;

        if (finalPos > 0 && currentPos < 0) {
            remain = finalPos;
            overCenter = true;
            //stepsToCenter = -currentPos;
        }

        if (finalPos < 0 && currentPos > 0) {
            remain = -currentPos;
            overCenter = true;
            //stepsToCenter = currentPos;
        }

        if (finalPos == 0) {
            remain = 0;
            overCenter = true;
        }

        return new SteeringMovement(finalPos - currentPos > 0, overCenter, remain, Math.abs(currentPos));

    }


    private void center() throws IOException, McCommunicationException, InterruptedException {

        boolean initPos = getComparator();

        while (getComparator() == initPos) {
            setDir(initPos);
            turn(1, 11);
            sleep(1);
        }

    }

    private boolean getComparator() throws IOException, McCommunicationException {
       return (mc.readReg(At2313Reg.ACSR) & 0x20) != 0;
    }

    private void turn(int steps, int pwm) throws IOException, McCommunicationException, InterruptedException {

        //setDir(dir);

        mc.send(McCommand.SET_PWM0, (byte)0x00);

        mc.send(McCommand.CANCEL_SCHEDULE_PWM1);

        mc.send(McCommand.SCHEDULE_PWM1, (byte) steps);

        mc.send(McCommand.SET_PWM0, (byte)(0xFF- pwm));


        while (!Thread.currentThread().isInterrupted()) {

            int resp = mc.send(McCommand.GET_INT_COUNTER);

            sleep(5);

            if (resp >= steps) {
                break;
            }

        }

    }

    private void setDir(boolean value) throws IOException, McCommunicationException {

        byte pb = mc.readReg(At2313Reg.PORTB);

        mc.writeReg(At2313Reg.PORTB, value ? (byte)(pb | (1<<5)) : (byte)(~(1<<5) & pb));

    }


}
