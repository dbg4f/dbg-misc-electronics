package dbg.electronics.robodrv.drive;


import dbg.electronics.robodrv.mcu.McuCommunicationException;

import java.io.IOException;

public interface MotorDrive {

    void setEnabled(boolean enabled);

    void setDirection(boolean forward) throws InterruptedException, IOException, McuCommunicationException;

    void setPwm(int value) throws InterruptedException, IOException, McuCommunicationException;

}
