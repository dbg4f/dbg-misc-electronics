package dbg.electronics.robodrv.emulator;


import dbg.electronics.robodrv.GenericThread;
import dbg.electronics.robodrv.OutOfRange;
import dbg.electronics.robodrv.Range;

public class MotorDriveEmulator extends GenericThread {

    public static final Range SPEED_RANGE = new Range(0, 255);
    public static final Range PWM_RANGE = new Range(0, 255);
    public static final Range RELAY_POSITION_RANGE = new Range(0, 20);
    public static final Range SERVO_POSITION_SENSOR_RANGE = new Range(-100, 100);

    private int speed = 0;
    private boolean direction;
    private int position = 0;
    private int ticks = 0;
    private int relayPosition = 0;
    private int pwm;

    public static final double PWM_SPEED_RATIO = 1.0;
    public static final int CLOCK_MS = 10;
    public static final int RELAY_FULL_SWITCH_CLOCKS = 20;
    public static final int SPEED_INCREASE_PER_CLOCK = 20;  // acceleration
    public static final int SPEED_DECREASE_PER_CLOCK = 80;  // braking

    public void setPwm(int value) throws OutOfRange {
        PWM_RANGE.validate(value);
        this.pwm = value;
    }

    public void setDirection(boolean dir) {
        direction = dir;
    }

    private boolean isRelayInStaticPosition() {
        return direction ? relayPosition == 0  : relayPosition == RELAY_FULL_SWITCH_CLOCKS;
    }

    private void moveRelayContact() {
        relayPosition += (direction ? -1 : 1);
    }



    private void applyPwmValue(int value) {

        double targetSpeed = PWM_SPEED_RATIO * value;

        if (speed != targetSpeed) {
            speed += targetSpeed < speed ? SPEED_INCREASE_PER_CLOCK : -SPEED_DECREASE_PER_CLOCK;
        }

    }



    public void onClock() {

        boolean relayInStaticPosition = isRelayInStaticPosition();

        int targetPwmValue = relayInStaticPosition ? pwm : 0;

        applyPwmValue(targetPwmValue);

        if (!relayInStaticPosition) {
            moveRelayContact();
        }

    }


    @Override
    public void startWork() {
        while (!Thread.currentThread().isInterrupted()) {

            try {
                onClock();
            }
            catch (Exception e) {
                e.printStackTrace();
                break;
            }

            try {
                Thread.sleep(CLOCK_MS);
            } catch (InterruptedException e) {
                break;
            }


        }

    }

}
