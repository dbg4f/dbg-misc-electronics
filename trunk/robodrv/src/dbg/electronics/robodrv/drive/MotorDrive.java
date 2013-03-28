package dbg.electronics.robodrv.drive;


public interface MotorDrive {

    void setDirection(boolean forward);

    void setPwm(int value);

}
