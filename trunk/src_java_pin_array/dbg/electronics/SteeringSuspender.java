package dbg.electronics;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class SteeringSuspender implements Runnable {

    private McConnection mc;

    SteeringState intendedState;

    SteeringState lastState;

    public SteeringSuspender(McConnection mc) throws IOException, McCommunicationException, InterruptedException {
        this.mc = mc;

        center();

        intendedState = new SteeringState(0x7F);

        lastState = intendedState;

    }

    private void center() throws IOException, McCommunicationException, InterruptedException {

        boolean initPos = getComparator();

        while (getComparator() == initPos) {
            turn(1, initPos, 11);
            sleep(10);
        }

    }

    private boolean getComparator() throws IOException, McCommunicationException {
       return (mc.readReg(At2313Reg.ACSR) & 0x20) != 0;
    }

    private void turn(int steps, boolean dir, int pwm) throws IOException, McCommunicationException, InterruptedException {

        setDir(dir);

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


    public void applyPending() {


        synchronized (this) {

          if (intendedState.ticks != lastState.ticks) {


              //calculate offsets

              lastState = intendedState;

          }

        }

        // apply offset



    }

    public void applyPendingSafe() {

        try {
            applyPending();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void run() {

        while (!Thread.currentThread().isInterrupted()) {

            applyPending();

            try {
                sleep(10);
            }
            catch (InterruptedException e) {
                break;
            }


        }

    }


    public synchronized void onSteer(byte value) {

        intendedState = new SteeringState(value);

    }


}
