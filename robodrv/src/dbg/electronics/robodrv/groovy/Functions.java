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
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "ERROR: " + e.getMessage();
        }
    }

    public String write(M16Reg reg, String value) {
        try {
            mcuRegisterAccess.writeReg(reg, BinUtils.asNumber(value));
            return reg + "<=" + value;
        }
        catch (Exception e) {
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

    public String init() {
        try {
            socketCommunicator.init();
            drive.init();
            //executor.sendOnly(createCommand(ENABLE_ADC));

            //drive.getChannelDrive(0).setDirection(true);
            Thread.sleep(1000);
            //drive.getChannelDrive(0).setDirection(false);
            drive.getChannelDrive(0).setPwm(255);
            Thread.sleep(1000);
            drive.getChannelDrive(0).setPwm(0);


            //drive.getChannelDrive(1).setDirection(true);
            Thread.sleep(1000);
            //drive.getChannelDrive(1).setDirection(false);
            drive.getChannelDrive(1).setPwm(255);
            Thread.sleep(1000);
            drive.getChannelDrive(1).setPwm(0);



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
