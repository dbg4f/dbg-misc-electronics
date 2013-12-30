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


    }

    private int position;
    private int pwm;
    private int speed; // pos/sec
    private boolean forward;
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
        this.pwm = pwm;
    }

    public int getPosition() {
        return position;
    }


    public void recalculate(int millisecElapsed) {
        if (pwm == 0) {
            return;
        }

        if (position > setup.posMax || position < setup.posMin) {
            speed = 0;
            return;
        }

        int regularSpeed = setup.getRegularSpeed(pwm);

        int movementSign = forward ? 1 : -1;

        if (speed == regularSpeed) {
            position += movementSign * (speed * millisecElapsed) / 1000;
            return;
        }

        int deltaSpeed = regularSpeed - speed;

        int accelerationSign = (deltaSpeed > 0) ? 1 : -1;

        int timeToReachSpeedMsec = (Math.abs(deltaSpeed) * 1000/ setup.acceleration);

        if (timeToReachSpeedMsec >= millisecElapsed) {

            speed += accelerationSign * (setup.acceleration * millisecElapsed) / 1000;

            //position +=

        }
        else {

            int regularMotionTime = millisecElapsed - timeToReachSpeedMsec;

            speed = regularSpeed;





        }





    }




    public static void main(String[] args) throws InterruptedException, McuCommunicationException, IOException {
        ServoEmulator emulator = new ServoEmulator(100);
        Setup s = new Setup(100, 200);

        emulator.setPwm(255);
        emulator.recalculate(100);

        //int pos2 = emulator.getPosition()

    }



}
