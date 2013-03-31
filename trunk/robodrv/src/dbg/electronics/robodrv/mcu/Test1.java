package dbg.electronics.robodrv.mcu;

import org.apache.log4j.Logger;

import java.io.IOException;

import static dbg.electronics.robodrv.mcu.CommandCode.ECHO;
import static dbg.electronics.robodrv.mcu.CommandCode.ENABLE_ADC;
import static dbg.electronics.robodrv.mcu.McuCommand.createCommand;

public class Test1 implements McuBytesListener, McuReportListener {

    private static final Logger log = Logger.getLogger(Test1.class);

    private byte[] adcValues = new byte[4];
    private byte counter = 0;
    

    public static void main(String[] args) throws IOException, InterruptedException, McuCommunicationException {

        Test1 t = new Test1();

        final McuSocketCommunicator communicator = new McuSocketCommunicator("192.168.1.1", 4444);

        SynchronousExecutor executor = new SynchronousExecutor();
        executor.setBytesWriter(communicator);

        McuReportDecoder decoder = new McuReportDecoder();

        decoder.setReportListener(executor);
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



        //executor.sendOnly(createCommand(ENABLE_ADC));

        McuCommand cmd;

        for (int i = 0; i< 20; i++) {

            cmd = createCommand(ECHO, i, i);
            //communicator.write(cmd.getRawCommand());
            executor.execute(cmd);
            Thread.sleep(100);

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