package dbg.electronics.robodrv.groovy;


import dbg.electronics.robodrv.drive.M16MultichannelPwmDrive;
import dbg.electronics.robodrv.mcu.*;

import static dbg.electronics.robodrv.mcu.CommandCode.ENABLE_ADC;
import static dbg.electronics.robodrv.mcu.CommandCode.ECHO;
import static dbg.electronics.robodrv.mcu.McuCommand.createCommand;

import dbg.electronics.robodrv.util.BinUtils;
import groovy.lang.Script;

import java.io.IOException;
import java.util.Date;

public class Functions extends Script {

    private static McuRegisterAccess mcuRegisterAccess;
    private static McuSocketCommunicator socketCommunicator;
    private static SynchronousExecutor executor;
    private static M16MultichannelPwmDrive drive;

    public void setSocketCommunicator(McuSocketCommunicator socketCommunicator) {
        Functions.socketCommunicator = socketCommunicator;
    }

    public void setMcuRegisterAccess(McuRegisterAccess mcuRegisterAccess) {
        Functions.mcuRegisterAccess = mcuRegisterAccess;
    }

    public void setExecutor(SynchronousExecutor executor) {
        Functions.executor = executor;
    }

    public void setDrive(M16MultichannelPwmDrive drive) {
        Functions.drive = drive;
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

    public String read(String reg) {
        try {
            return reg + "=" + mcuRegisterAccess.readReg(M16Reg.valueOf(reg));
        }
        catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "ERROR: " + e.getMessage();
        }

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
        pwm(0, 255);
        Thread.sleep(msec);
        pwm(0, 0);
        return "pulse " + msec;
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
            drive.getChannelDrive(channel).setPwm(value);
            return "pwm[" + channel + "] = " + value;
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }


    public String drv() throws InterruptedException, McuCommunicationException, IOException {

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
