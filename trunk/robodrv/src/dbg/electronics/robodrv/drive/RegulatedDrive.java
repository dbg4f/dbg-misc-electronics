package dbg.electronics.robodrv.drive;

public interface RegulatedDrive {

    void setPwm(int value);

    void setDirection(boolean value);

}
