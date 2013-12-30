package dbg.electronics.robodrv.pid;

import dbg.electronics.robodrv.drive.MotorDrive;
import dbg.electronics.robodrv.mcu.McuCommunicationException;

import java.io.IOException;

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

    private int position;
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
        return position;
    }

    public void recalculate(int dt) {

        int signV = forward ? 1 : -1;

        int v1 = setup.getRegularSpeed(pwm);

        int dv = v1 - speed;

        int ta = Math.abs(dv) * 1000 / setup.acceleration; // msec

        int signA = dv > 0 ? 1 : -1;

        if (ta < dt) {

            int pos1 = position + signV * lawDistance(0, speed, signA * setup.acceleration, ta);

            speed = v1;

            position = pos1 + signV * lawDistance(0, v1, 0, dt - ta);


        }
        else {

            position = position + signV * lawDistance(position, speed, signA * setup.acceleration, dt);

            speed = lawSpeed(speed, signA * setup.acceleration, dt);

        }

        if(forward && setup.isMaxReached(position)) {
           speed = 0;
           position = setup.posMax;
        }

        if (!forward && setup.isMinReached(position)) {
            speed = 0;
            position = setup.posMin;
        }

    }

    private int lawSpeed(int v0, int a, int dt) {
        return v0 + a * dt / 1000;
    }

    private int lawDistance(int d0, int v0, int a, int dt) {
        return d0 + v0 * dt / 1000 + a * dt * dt / (2 * 1000 * 1000);
    }



    public static void main(String[] args) throws InterruptedException, McuCommunicationException, IOException {
        ServoEmulator emulator = new ServoEmulator(140);
        Setup s = new Setup(100, 200);

        emulator.setPwm(255);
        emulator.recalculate(100);
        int pos2 = emulator.getPosition();
        System.out.println("pos2 = " + pos2);

        emulator.recalculate(100);
        pos2 = emulator.getPosition();
        System.out.println("pos3 = " + pos2);

        emulator.setPwm(0);
        emulator.recalculate(100);
        pos2 = emulator.getPosition();
        System.out.println("pos4 = " + pos2);

        emulator.setPwm(255);
        emulator.setDirection(false);
        emulator.recalculate(100);
        pos2 = emulator.getPosition();
        System.out.println("pos5 = " + pos2);

        emulator.recalculate(100);
        pos2 = emulator.getPosition();
        System.out.println("pos6 = " + pos2);



    }


    @Override
    public String toString() {
        return "ServoEmulator{" +
                "position=" + position +
                ", pwm=" + pwm +
                ", speed=" + speed +
                ", forward=" + forward +
                ", setup=" + setup +
                '}';
    }
}
