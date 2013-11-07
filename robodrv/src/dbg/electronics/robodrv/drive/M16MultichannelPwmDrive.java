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

    public M16MultichannelPwmDrive(SynchronousExecutor executor) {

        this.executor = executor;

        mcuRegisterAccess = new McuRegisterAccess(executor);

    }

    public void init() throws InterruptedException, IOException, McuCommunicationException {

        McuPwmDrive driveTimerA = new McuPwmDrive(M16Reg.PORTB, DIR_PIN_A, M16Reg.OCR1AH);
        McuPwmDrive driveTimerB = new McuPwmDrive(M16Reg.PORTB, DIR_PIN_B, M16Reg.OCR1BH);

        drives.put(0, driveTimerA);
        drives.put(1, driveTimerB);

        new RegBitSetter()
                .set(4, 1)
                .set(5, 1)
                .applyRegValue(mcuRegisterAccess, M16Reg.DDRD); // configure out direction for PWM pins (OC1A=PD4, OC1B=PD5)

        new RegBitSetter()
                .set(DIR_PIN_A, 1)
                .set(DIR_PIN_B, 1)
                .applyRegValue(mcuRegisterAccess, M16Reg.DDRB); // configure out pins for relays (DIR signal)

        /*

        TCCR1A

        COM1A1 COM1A0 COM1B1 COM1B0 FOC1A FOC1B WGM11 WGM10
             7      6      5      4     3     2     1     0

        For Phase correct PWM
        COM1A1/COM1B1 COM1A0/COM1B0
        0             0             Normal port operation, OC1A/OC1B disconnected.
        0             1             CTC
        1             0             Clear OC1A/OC1B on compare match when up-counting. Set OC1A/OC1B on compare match when downcounting.
        1             1             Set OC1A/OC1B on compare match when up-counting. Clear OC1A/OC1B on compare match when downcounting.

        FOC1A: Force Output Compare for Channel A
        FOC1B: Force Output Compare for Channel B
        The FOC1A/FOC1B bits are only active when the WGM13:0 bits specifies a non-PWM mode.


WGM13 WGM12 WGM11 WGM10
0     0     0     1       PWM Phase correct


        TCCR1B

        ICNC1 ICES1    –  WGM13 WGM12 CS12 CS11 CS10
            7     6    5      4     3    2    1    0


CS12 CS11 CS10
0    0     0   No clock source (Timer/Counter stopped).
0    0     1   clkI/O/1 (No prescaling)
0    1     0   clkI/O/8 (From prescaler)
0    1     1   clkI/O/64 (From prescaler)
1    0     0   clkI/O/256 (From prescaler)
1    0     1   clkI/O/1024 (From prescaler)
1    1     0   External clock source on T1 pin. Clock on falling edge.
1    1     1   External clock source on T1 pin. Clock on rising edge.


Fpwm = Fclk/(2*0x100*N), N = 1,8,64,256,1024

#define F_CPU 7372800

1	  14400
8	  1800
64    225
256	  56.25
1024  14.0625

         */


        new RegBitSetter()
            .set(7, 1) // COM1A1
            .set(6, 1) // COM1A0
            .set(5, 1) // COM1B1
            .set(4, 1) // COM1B0
            .set(1, 0) // WGM11
            .set(0, 1) // WGM10
        .applyRegValue(mcuRegisterAccess, M16Reg.TCCR1A); // , phase correct PWM, non-inverted

        new RegBitSetter()
            .set(4, 0) // WGM13
            .set(3, 0) // WGM12
            .set(2, 0) // CS12
            .set(1, 0) // CS11
            .set(0, 1) // CS10
        .applyRegValue(mcuRegisterAccess, M16Reg.TCCR1B); // no prescaling, 14kHz, phase correct PWM


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
