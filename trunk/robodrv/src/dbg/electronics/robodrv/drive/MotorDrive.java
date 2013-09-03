package dbg.electronics.robodrv.drive;


public interface MotorDrive {

    void setEnabled(boolean enabled);

    void setDirection(boolean forward);

    void setPwm(int value);

}
