package dbg.electronics.robodrv.groovy;


import dbg.electronics.robodrv.drive.DriveState;
import dbg.electronics.robodrv.drive.M16MultichannelPwmDrive;
import dbg.electronics.robodrv.drive.M32U4MultichannelPwmDrive;
import dbg.electronics.robodrv.drive.MotorDrive;
import dbg.electronics.robodrv.graphics.ValueWithHistory;
import dbg.electronics.robodrv.logging.ValueHistorySerializer;
import dbg.electronics.robodrv.mcu.*;

import static dbg.electronics.robodrv.mcu.CommandCode.ENABLE_ADC;
import static dbg.electronics.robodrv.mcu.CommandCode.ECHO;
import static dbg.electronics.robodrv.mcu.McuCommand.createCommand;

import dbg.electronics.robodrv.util.BinUtils;
import groovy.lang.Script;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Functions extends Script {

    private static McuRegisterAccess<M16Reg> mcuRegisterAccess;
    private static McuSocketCommunicator socketCommunicator;
    private static SynchronousExecutor executor;
    private static M16MultichannelPwmDrive drive;
    private static M32U4MultichannelPwmDrive drive2;
    private static List<ValueWithHistory> valueWithHistoryList;
    private static ValueHistorySerializer serializer;
    private static DriveState driveState;


    public void setSocketCommunicator(McuSocketCommunicator socketCommunicator) {
        Functions.socketCommunicator = socketCommunicator;
    }

    public void setMcuRegisterAccess(McuRegisterAccess<M16Reg> mcuRegisterAccess) {
        Functions.mcuRegisterAccess = mcuRegisterAccess;
    }

    public void setDrive2(M32U4MultichannelPwmDrive drive2) {
        Functions.drive2 = drive2;
    }

    public void setExecutor(SynchronousExecutor executor) {
        Functions.executor = executor;
    }

    public void setDrive(M16MultichannelPwmDrive drive) {
        Functions.drive = drive;
    }

    public void setValueWithHistoryList(List<ValueWithHistory> valueWithHistoryList) {
        Functions.valueWithHistoryList = valueWithHistoryList;
    }

    public void setSerializer(ValueHistorySerializer serializer) {
        Functions.serializer = serializer;
    }


    public void setDriveState(DriveState driveState) {
        Functions.driveState = driveState;
    }

    public String echo(int value) {
        try {
            return "ECHO res=" + executor.execute(createCommand(ECHO, 1)).getResult();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "ERROR: " + e.getMessage();
        }
    }

    public String pos(int pos) {
        return pos2(pos, 250);
    }

    public String pos2(int pos, int pwm) {
        return pos3(pos, pwm, 7);
    }

    public String pos3(int pos, int pwm, int gap) {

        try {

            int initial = driveState.getCurrentRawPos();

            int current = initial;

            int delta = pos - current;

            boolean dir = delta > 0;

            // ts = System.currentTimeMillis();

            MotorDrive motorDrive = drive2.getChannelDrive(1);

            motorDrive.setDirection(dir);
            motorDrive.setPwm(pwm);

            boolean mustGrow = (pos > current);

            for(int i=0; i<1000; i++) {

                Thread.sleep(2);

                current = driveState.getCurrentRawPos();

                int posWithClearance = mustGrow ? pos - gap : pos + gap;

                boolean mustStop = mustGrow ? (current >= posWithClearance) : (current <= posWithClearance);

                if (mustStop) {
                    motorDrive.setPwm(0);
                    Thread.sleep(100);
                    int end = driveState.getCurrentRawPos();
                    return String.format("target=%d (current=%d/%d), count=%d, from %d to %d, pwm=%d", pos, current, end, i, initial, current, pwm);
                }


            }

            return String.format("TIMEOUT target=%d (current=%d), from %d to %d, pwm=%d", pos, current, initial, current, pwm);

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "ERROR: " + e.getMessage();
        }



    }

    public String read(String reg) {
        try {
            return reg + "=" + mcuRegisterAccess.readReg(M16Reg.valueOf(reg));
        }
        catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "ERROR: " + e.getMessage();
        }

    }

    public String freeze() {
        for (ValueWithHistory valueWithHistory : valueWithHistoryList) {
            valueWithHistory.freeze();
        }
        return "frozen";
    }

    public String unfreeze() {
        for (ValueWithHistory valueWithHistory : valueWithHistoryList) {
            valueWithHistory.unfreeze();
        }
        return "unfrozen";
    }

    public String write(String reg, String value) {
        try {
            mcuRegisterAccess.writeReg(M16Reg.valueOf(reg), BinUtils.asNumber(value));
            return reg + "<=" + value;
        }
        catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "ERROR: " + e.getMessage();
        }

    }


    public String pulse(int msec) throws InterruptedException {
        pwm(1, 255);
        Thread.sleep(msec);
        pwm(1, 0);
        Thread.sleep(2200);
        freeze();
        return "pulse " + msec;
    }


    public String save(String fileName) {
        try {
            serializer.save(fileName);
            return "saved to " + fileName;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "ERROR: " + e.getMessage();
        }
    }

    public String restore(String fileName) {
        try {
            serializer.restore(fileName);
            return "restored from " + fileName;
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "ERROR: " + e.getMessage();
        }
    }

    public String adc() {
        try {
            executor.sendOnly(createCommand(ENABLE_ADC));
            return "ADC enabled";
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "ERROR: " + e.getMessage();
        }

    }

    public String comm() throws IOException {
        socketCommunicator.init();
        echo(1);
        echo(2);
        echo(3);
        echo(4);
        echo(5);
        return "initialized";

    }


    public String dir(int channel, int value) {
        try {
            M32U4MultichannelPwmDrive drive = drive2;
            drive.getChannelDrive(channel).setDirection(value != 0);
            return "dir[" + channel + "] = " + (value != 0);
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    public String pwm(int channel, int value) {
        try {
            M32U4MultichannelPwmDrive drive = drive2;
            drive.getChannelDrive(channel).setPwm(value);
            return "pwm[" + channel + "] = " + value;
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }


    public String drv() throws InterruptedException, McuCommunicationException, IOException {

        M32U4MultichannelPwmDrive drive = drive2;

        drive.getChannelDrive(0).setPwm(0);
        drive.getChannelDrive(0).setDirection(true);
        Thread.sleep(1000);
        drive.getChannelDrive(0).setDirection(false);
        drive.getChannelDrive(0).setPwm(255);
        Thread.sleep(1000);
        drive.getChannelDrive(0).setPwm(0);


        drive.getChannelDrive(1).setPwm(1);
        drive.getChannelDrive(1).setDirection(true);
        Thread.sleep(1000);
        drive.getChannelDrive(1).setDirection(false);
        drive.getChannelDrive(1).setPwm(255);
        Thread.sleep(1000);
        drive.getChannelDrive(1).setPwm(0);

        return "drv ok";

    }

    public String init() {
        try {
            socketCommunicator.init();

            M32U4MultichannelPwmDrive drive = drive2;

            drive.init();
            //executor.sendOnly(createCommand(ENABLE_ADC));

            return "Socket communicator initialized";
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "ERROR: " + e.getMessage();
        }


    }


    public String time() {

        String t = new Date().toString();

        System.out.println("t = " + t);

        return t;
    }


    @Override
    public Object run() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return "Functions{" +
                "mcuRegisterAccess=" + mcuRegisterAccess +
                ", socketCommunicator=" + socketCommunicator +
                '}';
    }
}
