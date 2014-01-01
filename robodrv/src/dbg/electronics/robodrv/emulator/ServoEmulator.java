package dbg.electronics.robodrv.emulator;

import dbg.electronics.robodrv.drive.MotorDrive;
import dbg.electronics.robodrv.mcu.McuCommunicationException;
import dbg.electronics.robodrv.controllers.PidController;
import dbg.electronics.robodrv.controllers.PidWeights;
import dbg.electronics.robodrv.controllers.RangeRestriction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ServoEmulator implements MotorDrive {


    static class Setup {

        final int posMax;
        final int posMin;

        final int acceleration = 2000; // [pos/sec*sec], speed 0..100 for 50 msec (100/0.05), reaction time included

        Setup(int posMin, int posMax) {
            this.posMax = posMax;
            this.posMin = posMin;
        }

        final int maxSpeed = 100; // [pos/sec] 1 pos / 10 msec
        final int maxPwm = 255;
        final int minPwm = 140;

        int getRegularSpeed(int pwm) {
            if (pwm < minPwm) {
                return 0;
            }
            else if (pwm >= maxPwm) {
                return maxSpeed;
            }
            else {
                return ((pwm - minPwm) * maxSpeed / (maxPwm - minPwm));
            }

        }

        boolean isMaxReached(int pos) {
            return pos >= posMax;
        }

        boolean isMinReached(int pos) {
            return pos <= posMin;
        }


    }

    private double position;
    private int pwm;
    private int speed; // pos/sec
    private boolean forward = true;
    private Setup setup = new Setup(140, 225);

    public ServoEmulator(int position) {
        this.position = position;
    }


    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public void setDirection(boolean forward) throws InterruptedException, IOException, McuCommunicationException {
        this.forward = forward;
    }


    @Override
    public void setPwm(int value) throws InterruptedException, IOException, McuCommunicationException {
        this.pwm = value;
    }

    public int getPosition() {
        return (int)Math.round(position);
    }

    public void recalculate(int dt) {

        int signV = forward ? 1 : -1;

        int v1 = setup.getRegularSpeed(pwm);

        int dv = v1 - speed;

        int ta = Math.abs(dv) * 1000 / setup.acceleration; // msec

        int signA = dv > 0 ? 1 : -1;

        if (ta < dt) {

            double posAcceleration = acceleratedMovement(signV, ta, signA);

            speed = v1;

            position = posAcceleration + signV * lawDistance(v1, 0, dt - ta);


        }
        else {

            position = acceleratedMovement(signV, dt, signA);

            speed = lawSpeed(speed, signA * setup.acceleration, dt);

        }

        checkUpperLimit();

        checkLowerLimit();

        position = roundPosition();

    }

    private double roundPosition() {
        return new BigDecimal(position).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    private double acceleratedMovement(int signV, int ta, int signA) {
        return position + signV * lawDistance(speed, signA * setup.acceleration, ta);
    }

    private void checkLowerLimit() {
        if (!forward && setup.isMinReached(getPosition())) {
            speed = 0;
            position = setup.posMin;
        }
    }

    private void checkUpperLimit() {
        if(forward && setup.isMaxReached(getPosition())) {
           speed = 0;
           position = setup.posMax;
        }
    }

    private int lawSpeed(int v0, int a, int dt) {
        return v0 + a * dt / 1000;
    }

    private double lawDistance(int v0, int a, int dt) {
        return (double)(v0 * dt)/ 1000 + (double)(a * dt * dt) / (2 * 1000 * 1000);
    }



    public static void main(String[] args) throws InterruptedException, McuCommunicationException, IOException {
        ServoEmulator emulator = new ServoEmulator(140);

        PidController regulator = new PidController(new PidWeights(10, 0, 0), new RangeRestriction(0, 255));

        int commandPos = 180;

        System.out.println("regulator = " + regulator);

        int time = 0;
        int dt = 10;


        time = move(emulator, regulator, time, dt, 100, commandPos);



    }

    private static int move(ServoEmulator emulator, PidController regulator, int time, int dt, int iterations, int commandPos) throws InterruptedException, McuCommunicationException, IOException {
        for (int i=0; i< iterations; i++) {
            emulator.recalculate(dt);
            time += dt;

            int value = (int)regulator.getValue(commandPos - emulator.getPosition());

            emulator.setDirection(value > 0);

            emulator.setPwm(Math.abs(value));

            System.out.println(time + " = " + emulator + " " + regulator.getLastTriplet());


        }
        return time;
    }


    @Override
    public String toString() {
        return "ServoEmulator{" +
                "position=" + position +
                ", pwm=" + pwm +
                ", speed=" + speed +
                ", forward=" + forward +
                '}';
    }
}
