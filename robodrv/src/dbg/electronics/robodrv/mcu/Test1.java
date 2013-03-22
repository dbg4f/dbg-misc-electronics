package dbg.electronics.robodrv.mcu;

import org.apache.log4j.Logger;

import java.io.IOException;

public class Test1 implements McuBytesListener {

     private static final Logger log = Logger.getLogger("MCU");


    public static void main(String[] args) throws IOException, InterruptedException {

        Test1 t = new Test1();

        final McuSocketCommunicator communicator = new McuSocketCommunicator("192.168.1.1", 4444);

        communicator.init();

        communicator.setBytesListener(t);


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


        McuCommand cmd = McuCommand.createCommand(CommandCode.ENABLE_ADC);

        System.out.println("cmd.toRawBytesString() = " + cmd.toRawBytesString());
        System.out.println("cmd.toRawBytesString() = " + cmd.toString());



        communicator.write(cmd.getRawCommand());





    }


    @Override
    public void onNextByte(byte nextByte) {
        log.info(String.format("%02X", nextByte));
    }
}
