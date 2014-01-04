package dbg.electronics.robodrv.groovy;


import dbg.electronics.robodrv.drive.DriveState;
import dbg.electronics.robodrv.drive.M16MultichannelPwmDrive;
import dbg.electronics.robodrv.drive.M32U4MultichannelPwmDrive;
import dbg.electronics.robodrv.drive.MotorDrive;
import dbg.electronics.robodrv.graphics.TimeSeries;
import dbg.electronics.robodrv.graphics.ValueWithHistory;
import dbg.electronics.robodrv.logging.ValueHistorySerializer;
import dbg.electronics.robodrv.mcu.*;

import static dbg.electronics.robodrv.mcu.CommandCode.ENABLE_ADC;
import static dbg.electronics.robodrv.mcu.CommandCode.ECHO;
import static dbg.electronics.robodrv.mcu.McuCommand.createCommand;

import dbg.electronics.robodrv.controllers.PidController;
import dbg.electronics.robodrv.controllers.PidWeights;
import dbg.electronics.robodrv.controllers.RangeRestriction;
import dbg.electronics.robodrv.emulator.ServoEmulator;
import dbg.electronics.robodrv.util.BinUtils;
import groovy.lang.Script;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Functions extends Script {

    private static final Logger log = Logger.getLogger(Functions.class);

    private static McuRegisterAccess<M16Reg> mcuRegisterAccess;
    private static McuSocketCommunicator socketCommunicator;
    private static SynchronousExecutor executor;
    private static M16MultichannelPwmDrive drive;
    public static M32U4MultichannelPwmDrive drive2;
    private static List<ValueWithHistory> valueWithHistoryList;
    private static ValueHistorySerializer serializer;
    private static DriveState driveState;
    public static boolean adc = false;


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

            for (int i = 0; i < 1000; i++) {

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
        } catch (Exception e) {
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


    public String runPid2(int P, int I, int commandPos) throws InterruptedException, McuCommunicationException, IOException {

        unfreeze();

        log.info("PID sample: P=" + P + " I=" + I + " cmd pos=" + commandPos + " current pos=" + driveState.getCurrentRawPos());

        PidController regulator = new PidController(new PidWeights(P, I, 0), new RangeRestriction(-255, 255));


        int time = 0;
        int dt = 1;

        M32U4MultichannelPwmDrive drive = drive2;

        int currentError = 0;

        MotorDrive motorDrive = drive2.getChannelDrive(1);

        for (int i = 0; i < 250; i++) {

            Thread.sleep(dt);

            time += dt;

            int position = driveState.getCurrentRawPos();

            currentError = commandPos - position;

            int pidResultValue = (int) regulator.getValue(currentError);

            motorDrive.setDirection(pidResultValue > 0);

            int pwmValue = Math.abs(pidResultValue);

            if (pwmValue > 255) {
                pwmValue = 255;
            }

            motorDrive.setPwm(pwmValue);

            log.info(String.format("PID: t=%d c=%d, err=%d, reg=%d", commandPos, position, currentError, pidResultValue));

            driveState.updateValueWithHistory(commandPos);

            if (currentError == 0) {
               // break;
            }


        }

        regulator.reset();

        freeze();

        String result = String.format("PID K=%d time=%d err=%d t=%d", P, time, currentError, commandPos);

        motorDrive.setPwm(0);

        log.info("PID finished: " + result);

        return result;

    }

    public void runPid(int P, int D) throws InterruptedException, McuCommunicationException, IOException {

        List<TimeSeries> res = new ArrayList<TimeSeries>();
        List<TimeSeries> ref = new ArrayList<TimeSeries>();

        ServoEmulator emulator = new ServoEmulator(140);

        PidController regulator = new PidController(new PidWeights(P, 0, D), new RangeRestriction(0, 255));

        int commandPos = 180;

        System.out.println("regulator = " + regulator);

        int time = 0;
        int dt = 10;


        for (int i = 0; i < 100; i++) {
            emulator.recalculate(dt);
            time += dt;

            int position = emulator.getPosition();

            int value = (int) regulator.getValue(commandPos - position);

            emulator.setDirection(value > 0);

            emulator.setPwm(Math.abs(value));

            System.out.println(time + " = " + emulator + " " + regulator.getLastTriplet());

            res.add(new TimeSeries(time*3, position));
            ref.add(new TimeSeries(time*3, commandPos));

        }


        freeze();

        valueWithHistoryList.get(0).setSnapshot(ref);
        valueWithHistoryList.get(1).setSnapshot(res);

    }


    public String pid(int P, int I, int commandPos) {

        try {
            return runPid2(P, I, commandPos);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "ERROR: " + e.getMessage();
        }

    }


    public String write(String reg, String value) {
        try {
            mcuRegisterAccess.writeReg(M16Reg.valueOf(reg), BinUtils.asNumber(value));
            return reg + "<=" + value;
        } catch (Exception e) {
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
            Functions.adc = true;
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
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    public String pwm(int channel, int value) {
        try {
            M32U4MultichannelPwmDrive drive = drive2;
            drive.getChannelDrive(channel).setPwm(value);
            return "pwm[" + channel + "] = " + value;
        } catch (Exception e) {
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
