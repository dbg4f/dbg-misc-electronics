package dbg.electronics.robodrv.mcu;

import dbg.electronics.robodrv.logging.LoggerFactory;
import dbg.electronics.robodrv.logging.SimpleLogger;

import java.io.IOException;

public class CommunicationController implements McuBytesWriter{

    private static final SimpleLogger log = LoggerFactory.getLogger();
    
    enum Status {
        UNKNWON,
        CONNECTED,
        FAILURE
    }

    private Status status = Status.UNKNWON;

    private McuSocketCommunicator socketCommunicator;

    public void setSocketCommunicator(McuSocketCommunicator socketCommunicator) {
        this.socketCommunicator = socketCommunicator;
    }

    @Override
    public void write(byte[] command) throws IOException {

        try {
            socketCommunicator.write(command);
            status = Status.CONNECTED;
        }
        catch (Exception e) {
            log.error("Communication error while sending bytes command : " + e.getMessage(), e);
            status = Status.FAILURE;
        }

    }

    public void listeningCycle() throws IOException {

        while (!Thread.currentThread().isInterrupted()) {

            try {
                socketCommunicator.waitAndProcessNextByte();
            }
            catch (Exception e) {
                log.error("Communication error while listening bytes report : " + e.getMessage(), e);
                status = Status.FAILURE;
            }

            if (status == Status.FAILURE) {

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    break;
                }

                try {

                    socketCommunicator.init();

                    status = Status.CONNECTED;
                }
                catch (Exception e) {
                    log.error("Failed to restart communication : " + e.getMessage(), e);
                }


            }


        }

    }







}
