package dbg.electronics.robodrv.drive;

import dbg.electronics.robodrv.mcu.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static dbg.electronics.robodrv.mcu.CommandCode.*;
import static dbg.electronics.robodrv.mcu.McuCommand.createCommand;

public class M16MultichannelPwmDrive {


    public static final int DIR_PIN_A = 3;
    public static final int DIR_PIN_B = 4;
    private final SynchronousExecutor executor;
    private final McuRegisterAccess mcuRegisterAccess;



    class McuPwmDrive implements MotorDrive {

        final M16Reg dirPinPort;
        final M16Reg pwmReg;
        final int dirPin;

        McuPwmDrive(M16Reg dirPinPort,  int dirPin, M16Reg pwmReg) {
            this.dirPinPort = dirPinPort;
            this.pwmReg = pwmReg;
            this.dirPin = dirPin;
        }

        @Override
        public void setEnabled(boolean enabled) {

        }

        @Override
        public void setDirection(boolean forward) throws InterruptedException, IOException, McuCommunicationException {
            setPortRegBit(dirPinPort, dirPin, forward ? 1 : 0);
        }

        @Override
        public void setPwm(int value) throws InterruptedException, IOException, McuCommunicationException {
            writeReg(pwmReg, value);
        }
    }


    private Map<Integer, McuPwmDrive> drives = new LinkedHashMap<Integer, McuPwmDrive>();

    public M16MultichannelPwmDrive(SynchronousExecutor executor) throws InterruptedException, IOException, McuCommunicationException {
        this.executor = executor;

        mcuRegisterAccess = new McuRegisterAccess(executor);

        McuPwmDrive driveTimerA = new McuPwmDrive(M16Reg.PORTB, DIR_PIN_A, M16Reg.OCR1AH);
        McuPwmDrive driveTimerB = new McuPwmDrive(M16Reg.PORTB, DIR_PIN_B, M16Reg.OCR1BH);

        drives.put(0, driveTimerA);
        drives.put(1, driveTimerB);

        setPortRegBit(M16Reg.PORTD, 4, 1); // out direction for PWM pins (OC1A=PD4, OC1B=PD5)
        setPortRegBit(M16Reg.PORTD, 5, 1);
        setPortRegBit(M16Reg.dirRegForPort(driveTimerA.dirPinPort), driveTimerA.dirPin, 1); // out direction for relay pins
        setPortRegBit(M16Reg.dirRegForPort(driveTimerB.dirPinPort), driveTimerB.dirPin, 1);

        new RegBitSetter()
            .set(7, 1) // COM1A1
            .set(6, 0) // COM1A0
            .set(5, 1) // COM1B1
            .set(4, 0) // COM1B0
            .set(3, 0) // FOC1A
            .set(2, 0) // FOC1B
            .set(1, 0) // WGM11
            .set(0, 0) // WGM10
        .applyRegValue(mcuRegisterAccess, M16Reg.TCCR1A); // direct(non-inverted) PWM  for A,B channels


    }

    public MotorDrive getChannelDrive(int channel) {
        return drives.get(channel);
    }

    void setRegOneBit(M16Reg reg, int bit, int value) throws InterruptedException, IOException, McuCommunicationException {

        RegBitSetter regBitSetter = new RegBitSetter();

        regBitSetter.set(bit, value);

        int regValue = readReg(reg);

        regValue = regBitSetter.apply(regValue);

        writeReg(reg, regValue);
    }

    void writeReg(M16Reg reg, int value) throws InterruptedException, McuCommunicationException, IOException {
        mcuRegisterAccess.writeReg(reg, value);
    }


    int readReg(M16Reg reg) throws InterruptedException, McuCommunicationException, IOException {
        return mcuRegisterAccess.readReg(reg);
    }

    private void setPortRegBit(M16Reg reg, int bit, int value) throws InterruptedException, McuCommunicationException, IOException {

        CommandResponse response;

        CommandCode commandCode;

        int mask = (1 << bit);

        if (value == 0) {
            commandCode = CLEAR_PORT_BITS;
            mask = (~mask);
        }
        else {
            commandCode = SET_PORT_BITS;
        }

        McuCommand command = createCommand(commandCode, reg.toCode(), mask);

        response = executor.execute(command);

    }


}
