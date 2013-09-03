package dbg.electronics.robodrv.drive;

import dbg.electronics.robodrv.mcu.M16Reg;

import java.util.LinkedHashMap;
import java.util.Map;

public class M16MultichannelPwmDrive {





    class McuPwmDrive implements MotorDrive {

        M16Reg directionPinPort;
        M16Reg directionPinDir;
        M16Reg pwmValueReg;
        int pinInport;

        @Override
        public void setEnabled(boolean enabled) {




        }

        @Override
        public void setDirection(boolean forward) {

        }

        @Override
        public void setPwm(int value) {

        }
    }


    private Map<Integer, McuPwmDrive> drives = new LinkedHashMap<Integer, McuPwmDrive>();

    public M16MultichannelPwmDrive() {

        drives.put(0, new McuPwmDrive());
        drives.put(1, new McuPwmDrive());
    }
    public MotorDrive getChannelDrive(int channel) {
        return drives.get(channel);
    }


}
