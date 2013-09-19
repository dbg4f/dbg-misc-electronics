package dbg.electronics.robodrv.mcu;

import dbg.electronics.robodrv.drive.DriveState;
import dbg.electronics.robodrv.drive.M16MultichannelPwmDrive;
import dbg.electronics.robodrv.util.BinUtils;
import groovy.ui.Console;
import org.apache.log4j.Logger;

import java.io.IOException;

import static dbg.electronics.robodrv.drive.DriveState.*;
import static dbg.electronics.robodrv.mcu.CommandCode.ECHO;
import static dbg.electronics.robodrv.mcu.CommandCode.ENABLE_ADC;
import static dbg.electronics.robodrv.mcu.CommandCode.WRITE_REG;
import static dbg.electronics.robodrv.mcu.McuCommand.createCommand;

public class Test1 implements McuBytesListener, McuReportListener {

    private static final Logger log = Logger.getLogger(Test1.class);

    private byte[] adcValues = new byte[4];
    private byte counter = 0;
    private McuRegisterAccess mcuRegisterAccess;
    

    public static void main(String[] args) throws IOException, InterruptedException, McuCommunicationException {

        final Test1 t = new Test1();

        final McuSocketCommunicator communicator = new McuSocketCommunicator("127.0.0.1", 4444);

        final DriveState driveState = new DriveState();

        SynchronousExecutor executor = new SynchronousExecutor();
        executor.setBytesWriter(communicator);

        t.mcuRegisterAccess = new McuRegisterAccess(executor);

        McuReportDecoder decoder = new McuReportDecoder();

        decoder.setReportListener(executor);
        decoder.setStatusListener(new ChannelStatusListener<ProtocolState>() {
            @Override
            public void onStatusChanged(ProtocolState status) {
                System.out.println("status = " + status);
            }
        }

       );
        executor.setNextListener(t);

        communicator.init();

        communicator.setBytesListener(decoder);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    communicator.listeningCycle();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }).start();


        M16MultichannelPwmDrive drive = new M16MultichannelPwmDrive(executor);

        executor.sendOnly(createCommand(ENABLE_ADC));


        //CommandResponse response = executor.execute(createCommand(WRITE_REG, M16Reg.DDRB.toCode()));

        //System.out.println("response = " + response);

        /*

        new Thread(new Runnable() {

            @Override
            public void run() {
                Console console = new Console();
                GroovyMcu groovyMcu = new GroovyMcu(t.mcuRegisterAccess);
                console.setVariable("t", groovyMcu);
                console.run();

            }
        }).start();

          */


        GroovyMcu t1 = new GroovyMcu(t.mcuRegisterAccess);


        t1.write("DDRD",   "00110000");
        t1.write("TCCR1A", "10100001");
        t1.write("TCCR1B", "00001001");
        t1.write("OCR1AH", "00000000");
        t1.write("OCR1AL", "00000000");
        t1.write("OCR1BH", "00000000");
        t1.write("OCR1BL", "10000000");
        t1.write("DDRB",   "00011000");


        McuCommand cmd;

        for (int i = 0; i< 10; i++) {

            //cmd = createCommand(ECHO, i, i);
            //communicator.write(cmd.getRawCommand());
            //executor.execute(cmd);

            t1.write("PORTB", "00011000");
            t1.write("OCR1AH", "00000000");
            t1.write("OCR1AL", "11111111");

            Thread.sleep(3000);

            t1.write("OCR1AH", "00000000");
            t1.write("OCR1AL", "00000000");

            Thread.sleep(100);

            t1.write("PORTB", "00000000");

            Thread.sleep(100);

            t1.write("OCR1AH", "00000000");
            t1.write("OCR1AL", "11111111");

            Thread.sleep(3000);

            t1.write("OCR1AH", "00000000");
            t1.write("OCR1AL", "00000000");


        }


    }




    static class GroovyMcu {

        private final McuRegisterAccess mcuRegisterAccess;


        GroovyMcu(McuRegisterAccess mcuRegisterAccess) {
            this.mcuRegisterAccess = mcuRegisterAccess;
        }

        public String read(String regName) throws InterruptedException, IOException, McuCommunicationException {
            int regValue = mcuRegisterAccess.readReg(M16Reg.valueOf(regName));
            regValue &= 0xFF;
            return String.format("%s = 0x%02X (%s)", regName, regValue, BinUtils.asString(regValue, 8));
        }

        public void write(String regName, String binValue) throws InterruptedException, IOException, McuCommunicationException {
            mcuRegisterAccess.writeReg(M16Reg.valueOf(regName), BinUtils.asNumber(binValue));
        }


    }


    @Override
    public void onAdcValue(int channel, byte value) {

        byte existingValue = adcValues[channel];

        if (Math.abs(existingValue - value) > 2) {
            log.info(String.format("ADC [%d] %d -> %d", channel, existingValue & 0xFF, value & 0xFF));
        }

        adcValues[channel] = value;

    }

    @Override
    public void onCounterUpdate(byte value) {
        log.info(String.format("counter %d -> %d", counter, value));
        counter = value;
    }

    @Override
    public void onMcuReset() {

    }

    @Override
    public void onCommandResponse(byte sequence, byte[] params) {
        log.info(" cmd " + sequence + " " + McuUtils.bytesToString(params));
    }

    @Override
    public void onNextByte(byte nextByte) {
        log.info(String.format("%02X", nextByte));
    }
}
